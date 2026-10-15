/**
 * Local Stripe Terminal stub for Android dev.
 * Reads STRIPE_SECRET_KEY from the environment (never commit it).
 *
 * Endpoints:
 *   POST /connection_token  -> { secret }
 *   POST /payment_intents   -> { id, clientSecret }  body: { amountCents, currency }
 */
import http from "node:http";

const PORT = Number(process.env.PORT || 4242);
const SECRET = process.env.STRIPE_SECRET_KEY;

if (!SECRET || !SECRET.startsWith("sk_")) {
  console.error("Set STRIPE_SECRET_KEY to a Stripe test secret (sk_test_…).");
  process.exit(1);
}

async function stripeForm(path, params) {
  const body = new URLSearchParams(params);
  const res = await fetch(`https://api.stripe.com/v1${path}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${SECRET}`,
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body,
  });
  const json = await res.json();
  if (!res.ok) {
    const message = json?.error?.message || res.statusText;
    const err = new Error(message);
    err.status = res.status;
    err.payload = json;
    throw err;
  }
  return json;
}

function readJson(req) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    req.on("data", (c) => chunks.push(c));
    req.on("end", () => {
      if (chunks.length === 0) {
        resolve({});
        return;
      }
      try {
        resolve(JSON.parse(Buffer.concat(chunks).toString("utf8")));
      } catch (e) {
        reject(e);
      }
    });
    req.on("error", reject);
  });
}

function send(res, status, obj) {
  const body = JSON.stringify(obj);
  res.writeHead(status, {
    "Content-Type": "application/json",
    "Content-Length": Buffer.byteLength(body),
    "Access-Control-Allow-Origin": "*",
  });
  res.end(body);
}

const server = http.createServer(async (req, res) => {
  try {
    if (req.method === "OPTIONS") {
      res.writeHead(204, {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "POST, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type",
      });
      res.end();
      return;
    }

    const url = new URL(req.url || "/", `http://127.0.0.1:${PORT}`);
    if (req.method === "POST" && url.pathname === "/connection_token") {
      const token = await stripeForm("/terminal/connection_tokens", {});
      send(res, 200, { secret: token.secret });
      return;
    }

    if (req.method === "POST" && url.pathname === "/payment_intents") {
      const body = await readJson(req);
      const amount = Number(body.amountCents);
      const currency = String(body.currency || "usd").toLowerCase();
      if (!Number.isFinite(amount) || amount <= 0) {
        send(res, 400, { error: "amountCents must be a positive integer" });
        return;
      }
      const intent = await stripeForm("/payment_intents", {
        amount: String(Math.trunc(amount)),
        currency,
        "payment_method_types[]": "card_present",
        capture_method: "automatic",
      });
      send(res, 200, { id: intent.id, clientSecret: intent.client_secret });
      return;
    }

    send(res, 404, { error: "not found" });
  } catch (e) {
    console.error(e.message || e);
    send(res, e.status || 500, { error: e.message || "stub failure" });
  }
});

server.listen(PORT, "0.0.0.0", () => {
  console.log(`Stripe Terminal stub listening on http://0.0.0.0:${PORT}`);
  console.log("POST /connection_token  POST /payment_intents");
});
