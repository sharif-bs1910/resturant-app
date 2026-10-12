#!/usr/bin/env python3
"""Figma MCP local cache helper for Cursor/Claude hooks.

Caches Figma MCP tool results under .ai/figma-cache/ so agents can reuse
node context without re-calling the MCP server on every validation pass.

Modes (argv[1]):
  before  — beforeMCPExecution / preToolUse: deny on cache hit (serve path)
  after   — afterMCPExecution / postToolUse: persist result_json / tool_output
"""

from __future__ import annotations

import hashlib
import json
import os
import re
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path
from typing import Any

# Tools that mutate Figma or mint ephemeral sessions — always allow, still record.
ALWAYS_ALLOW_TOOLS = {
    "create_session",
    "create_new_file",
    "use_figma",
    "generate_figma_design",
    "generate_diagram",
    "generate_image",
    "create_generative_plugin",
    "update_generative_plugin",
    "create_shader",
    "update_shader",
    "upload_assets",
    "add_code_connect_map",
    "send_code_connect_mappings",
}

# Strip these from the cache key so create_session rotation does not bust caches.
VOLATILE_INPUT_KEYS = {"sessionId", "session_id"}

FIGMA_SERVER_RE = re.compile(r"figma", re.I)
MCP_TOOL_RE = re.compile(r"^MCP:(.+)$")


def project_dir() -> Path:
    env = os.environ.get("CURSOR_PROJECT_DIR") or os.environ.get("CLAUDE_PROJECT_DIR")
    if env:
        return Path(env)
    return Path(__file__).resolve().parents[2]


def cache_root() -> Path:
    root = project_dir() / ".ai" / "figma-cache"
    root.mkdir(parents=True, exist_ok=True)
    return root


def emit(obj: dict[str, Any]) -> None:
    sys.stdout.write(json.dumps(obj, ensure_ascii=False))
    sys.stdout.flush()


def parse_tool_input(raw: Any) -> dict[str, Any]:
    if raw is None:
        return {}
    if isinstance(raw, dict):
        return raw
    if isinstance(raw, str):
        raw = raw.strip()
        if not raw:
            return {}
        try:
            val = json.loads(raw)
            return val if isinstance(val, dict) else {"_raw": val}
        except json.JSONDecodeError:
            return {"_raw": raw}
    return {"_raw": raw}


def normalize_tool_name(name: str) -> str:
    m = MCP_TOOL_RE.match(name or "")
    return m.group(1) if m else (name or "")


def is_figma_call(event: dict[str, Any], tool_name: str) -> bool:
    server = str(event.get("mcp_server_name") or event.get("mcpServerName") or "")
    if FIGMA_SERVER_RE.search(server):
        return True
    # postToolUse / preToolUse often use MCP:<tool> without server name
    if tool_name.startswith("MCP:") or event.get("tool_name", "").startswith("MCP:"):
        # Heuristic: known Figma tool names or fileKey/nodeId inputs
        bare = normalize_tool_name(tool_name)
        if bare in ALWAYS_ALLOW_TOOLS or bare.startswith("get_") or bare.startswith("generate_"):
            return True
        tip = parse_tool_input(event.get("tool_input"))
        if tip.get("fileKey") or tip.get("nodeId"):
            return True
    tip = parse_tool_input(event.get("tool_input"))
    if tip.get("fileKey") and tip.get("nodeId"):
        return True
    # Explicit allow-list of Figma plugin tools by bare name
    bare = normalize_tool_name(tool_name)
    figma_tools = ALWAYS_ALLOW_TOOLS | {
        "get_design_context",
        "get_screenshot",
        "get_metadata",
        "get_variable_defs",
        "get_figjam",
        "get_code_connect_map",
        "get_code_connect_suggestions",
        "get_context_for_code_connect",
        "get_motion_context",
        "get_libraries",
        "search_design_system",
        "whoami",
        "list_file_components_for_code_connect",
        "list_file_shaders",
        "list_shaders",
        "get_shader",
        "list_generative_plugins",
        "get_generative_plugin",
        "export_video",
        "weave_list_tools",
        "weave_get_tool_inputs",
        "weave_run_tool",
        "weave_upload_asset",
        "weave_get_tool_run_output",
        "weave_cancel_tool_run",
        "weave_find_model",
        "weave_run_model",
        "weave_get_model_run_output",
    }
    return bare in figma_tools


def bypass_requested(tip: dict[str, Any]) -> bool:
    if os.environ.get("FIGMA_CACHE_BYPASS", "").strip() in {"1", "true", "yes"}:
        return True
    for key in ("forceRefresh", "refreshCache", "bypassCache", "_cache"):
        val = tip.get(key)
        if val in (True, 1, "1", "true", "yes", "bypass", "refresh"):
            return True
    return False


def stable_key_payload(tool: str, tip: dict[str, Any]) -> dict[str, Any]:
    cleaned = {k: v for k, v in tip.items() if k not in VOLATILE_INPUT_KEYS}
    # Drop bypass flags from key so refresh overwrites same slot
    for key in ("forceRefresh", "refreshCache", "bypassCache", "_cache"):
        cleaned.pop(key, None)
    return {"tool": tool, "input": cleaned}


def cache_hash(tool: str, tip: dict[str, Any]) -> str:
    payload = json.dumps(stable_key_payload(tool, tip), sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(payload.encode("utf-8")).hexdigest()[:16]


def safe_segment(value: str, fallback: str = "_") -> str:
    value = (value or fallback).strip() or fallback
    value = value.replace(":", "-").replace("/", "-").replace("\\", "-")
    value = re.sub(r"[^A-Za-z0-9._-]+", "_", value)
    return value[:120] or fallback


def entry_paths(tool: str, tip: dict[str, Any]) -> tuple[Path, Path]:
    """Return (json_path, meta_path) for this call."""
    file_key = safe_segment(str(tip.get("fileKey") or tip.get("makeFileKey") or "global"))
    node_id = tip.get("nodeId") or tip.get("node_id")
    node_seg = safe_segment(str(node_id)) if node_id else "no-node"
    digest = cache_hash(tool, tip)
    folder = cache_root() / file_key / node_seg
    folder.mkdir(parents=True, exist_ok=True)
    base = f"{safe_segment(tool)}__{digest}"
    return folder / f"{base}.json", folder / f"{base}.meta.json"


def index_path() -> Path:
    return cache_root() / "index.jsonl"


def append_index(record: dict[str, Any]) -> None:
    with index_path().open("a", encoding="utf-8") as fh:
        fh.write(json.dumps(record, ensure_ascii=False) + "\n")


def load_result(path: Path) -> Any:
    with path.open("r", encoding="utf-8") as fh:
        return json.load(fh)


def unwrap_mcp_result(result: Any) -> Any:
    """Normalize MCP {content:[{type:text,text:...}]} envelopes to usable JSON/text."""
    if not isinstance(result, dict):
        return result
    content = result.get("content")
    if not isinstance(content, list) or not content:
        return result
    texts: list[str] = []
    for item in content:
        if isinstance(item, dict) and item.get("type") == "text":
            texts.append(str(item.get("text") or ""))
    joined = "\n".join(t for t in texts if t).strip()
    if not joined:
        return result
    try:
        return json.loads(joined)
    except json.JSONDecodeError:
        return {"text": joined, "raw_envelope": result}


def result_is_complete(result: Any) -> bool:
    """Large MCP payloads are sometimes delivered empty to hooks — do not cache-hit those."""
    if result is None:
        return False
    if isinstance(result, str):
        return len(result.strip()) >= 32
    if isinstance(result, (list, dict)):
        # Empty MCP text envelope
        if isinstance(result, dict):
            content = result.get("content")
            if isinstance(content, list):
                texts = [
                    str(i.get("text") or "")
                    for i in content
                    if isinstance(i, dict) and i.get("type") == "text"
                ]
                if content and not any(t.strip() for t in texts) and result.get("isError") is False:
                    return False
        try:
            size = len(json.dumps(result, ensure_ascii=False))
        except (TypeError, ValueError):
            size = len(str(result))
        return size >= 32
    return True


def recover_large_output(tool: str) -> Any | None:
    """Best-effort: copy the newest agent-tools dump when hook payload was truncated."""
    candidates: list[Path] = []
    home = Path.home()
    patterns = [
        home / ".cursor" / "projects" / "*" / "agent-tools",
    ]
    # Prefer project-specific path derived from cwd/project dir name
    proj = project_dir()
    slug = proj.name.replace(" ", "-")
    projects = home / ".cursor" / "projects"
    if projects.is_dir():
        for d in projects.iterdir():
            if not d.is_dir():
                continue
            if slug in d.name or "resturant-app" in d.name:
                tools = d / "agent-tools"
                if tools.is_dir():
                    candidates.append(tools)
    for tools_dir in candidates:
        try:
            files = sorted(tools_dir.glob("*.txt"), key=lambda p: p.stat().st_mtime, reverse=True)
        except OSError:
            continue
        now = time.time()
        for path in files[:12]:
            try:
                age = now - path.stat().st_mtime
                if age > 120:
                    continue
                data = path.read_text(encoding="utf-8", errors="replace")
            except OSError:
                continue
            if len(data) < 256:
                continue
            # Prefer dumps that look like design context / figma output
            if tool.startswith("get_") or "figma" in data[:500].lower() or "data-node-id" in data[:2000]:
                return {
                    "text": data,
                    "recovered_from": str(path),
                    "tool": tool,
                }
    return None


def maybe_download_assets(result: Any, json_path: Path) -> list[str]:
    """Best-effort download of short-lived Figma asset URLs next to the cache file."""
    saved: list[str] = []
    urls: list[str] = []

    def walk(obj: Any) -> None:
        if isinstance(obj, dict):
            for k, v in obj.items():
                if k in {"image_url", "url"} and isinstance(v, str) and v.startswith("http"):
                    if "figma.com" in v or "mcp/asset" in v:
                        urls.append(v)
                else:
                    walk(v)
        elif isinstance(obj, list):
            for item in obj:
                walk(item)
        elif isinstance(obj, str) and obj.startswith("http") and ("figma.com/api/mcp/asset" in obj):
            urls.append(obj)

    walk(result)
    # de-dupe preserve order
    seen: set[str] = set()
    unique = []
    for u in urls:
        if u not in seen:
            seen.add(u)
            unique.append(u)

    for i, url in enumerate(unique[:8]):
        ext = ".png"
        if ".svg" in url:
            ext = ".svg"
        elif ".jpg" in url or ".jpeg" in url:
            ext = ".jpg"
        dest = json_path.with_suffix(f".asset{i}{ext}")
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "noshitech-figma-cache/1.0"})
            with urllib.request.urlopen(req, timeout=20) as resp:
                dest.write_bytes(resp.read())
            saved.append(str(dest))
        except (urllib.error.URLError, TimeoutError, OSError):
            continue
    return saved


def handle_before(event: dict[str, Any]) -> dict[str, Any]:
    tool = normalize_tool_name(str(event.get("tool_name") or ""))
    if not is_figma_call(event, str(event.get("tool_name") or "")):
        return {"permission": "allow"}

    tip = parse_tool_input(event.get("tool_input"))
    if tool in ALWAYS_ALLOW_TOOLS or bypass_requested(tip):
        return {"permission": "allow"}

    json_path, meta_path = entry_paths(tool, tip)
    if not json_path.is_file():
        return {"permission": "allow"}

    meta = {}
    if meta_path.is_file():
        try:
            meta = load_result(meta_path)
        except json.JSONDecodeError:
            meta = {}
    if meta.get("incomplete"):
        return {"permission": "allow"}
    try:
        cached = load_result(json_path)
    except json.JSONDecodeError:
        return {"permission": "allow"}
    payload = cached.get("payload", cached) if isinstance(cached, dict) else cached
    if not result_is_complete(payload):
        return {"permission": "allow"}

    rel = json_path.relative_to(project_dir()) if json_path.is_relative_to(project_dir()) else json_path

    agent_msg = (
        f"Figma MCP cache HIT — skipped live call.\n"
        f"tool: {tool}\n"
        f"fileKey: {tip.get('fileKey', '')}\n"
        f"nodeId: {tip.get('nodeId', '')}\n"
        f"cached_at: {meta.get('cached_at', 'unknown')}\n"
        f"Read this file for the full cached payload (do not re-call MCP):\n"
        f"  {rel}\n"
        f"To force a fresh Figma MCP call, retry with tool input "
        f'"forceRefresh": true (or set FIGMA_CACHE_BYPASS=1).'
    )
    return {
        "permission": "deny",
        "user_message": f"Using cached Figma context for {tool} ({tip.get('nodeId', 'no-node')}).",
        "agent_message": agent_msg,
    }


def persist(event: dict[str, Any], result_raw: Any) -> dict[str, Any]:
    tool = normalize_tool_name(str(event.get("tool_name") or ""))
    if not is_figma_call(event, str(event.get("tool_name") or "")):
        return {}

    tip = parse_tool_input(event.get("tool_input"))
    json_path, meta_path = entry_paths(tool, tip)

    if isinstance(result_raw, str):
        try:
            result = json.loads(result_raw)
        except json.JSONDecodeError:
            result = {"raw": result_raw}
    else:
        result = result_raw

    incomplete = not result_is_complete(result)
    if incomplete:
        recovered = recover_large_output(tool)
        if recovered is not None:
            result = recovered
            incomplete = not result_is_complete(result)

    normalized = unwrap_mcp_result(result)
    to_store = {
        "tool": tool,
        "fileKey": tip.get("fileKey"),
        "nodeId": tip.get("nodeId"),
        "cached_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "payload": normalized,
        "raw": result,
    }

    json_path.write_text(json.dumps(to_store, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    assets = maybe_download_assets(normalized, json_path)

    meta = {
        "tool": tool,
        "fileKey": tip.get("fileKey"),
        "nodeId": tip.get("nodeId"),
        "mcp_server_name": event.get("mcp_server_name"),
        "cached_at": to_store["cached_at"],
        "incomplete": incomplete,
        "cache_file": str(json_path.relative_to(project_dir()))
        if json_path.is_relative_to(project_dir())
        else str(json_path),
        "assets": [
            str(Path(a).relative_to(project_dir())) if Path(a).is_relative_to(project_dir()) else a for a in assets
        ],
        "input": {k: v for k, v in tip.items() if k not in VOLATILE_INPUT_KEYS},
    }
    meta_path.write_text(json.dumps(meta, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    append_index(meta)

    # Track latest sessionId per fileKey when present in result or input
    session_id = tip.get("sessionId")
    if isinstance(result, dict):
        session_id = session_id or result.get("sessionId")
    if tip.get("fileKey") and session_id:
        sess_path = cache_root() / safe_segment(str(tip["fileKey"])) / "session.json"
        sess_path.write_text(
            json.dumps(
                {
                    "fileKey": tip["fileKey"],
                    "sessionId": session_id,
                    "cached_at": meta["cached_at"],
                },
                indent=2,
            )
            + "\n",
            encoding="utf-8",
        )

    rel = meta["cache_file"]
    return {
        "additional_context": (
            f"Figma MCP result cached locally for reuse/validation.\n"
            f"tool: {tool}\n"
            f"fileKey: {tip.get('fileKey', '')}\n"
            f"nodeId: {tip.get('nodeId', '')}\n"
            f"path: {rel}\n"
            f"Prefer reading this cache on later validations instead of re-calling Figma MCP. "
            f'Pass "forceRefresh": true to refresh.'
        )
    }


def handle_after(event: dict[str, Any]) -> dict[str, Any]:
    result = event.get("result_json")
    if result is None:
        result = event.get("tool_output")
    if result is None:
        return {}
    return persist(event, result)


def main() -> int:
    mode = (sys.argv[1] if len(sys.argv) > 1 else "").strip()
    try:
        event = json.load(sys.stdin)
    except json.JSONDecodeError:
        # Fail open
        if mode == "before":
            emit({"permission": "allow"})
        return 0

    hook_event = str(event.get("hook_event_name") or event.get("hookEventName") or "")
    if not mode:
        if "before" in hook_event.lower() or hook_event in {"PreToolUse", "preToolUse"}:
            mode = "before"
        else:
            mode = "after"

    if mode == "before":
        emit(handle_before(event))
    else:
        out = handle_after(event)
        # afterMCPExecution has no documented output fields — only emit for postToolUse
        if out and ("post" in hook_event.lower() or hook_event in {"PostToolUse", "postToolUse"}):
            emit(out)
        elif out and mode == "after" and not hook_event:
            # CLI/testing convenience
            emit(out)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
