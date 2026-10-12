package com.noshitechinc.restaurant.feature.checkout

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.role.DeviceRole

@Composable
fun TeamContent(state: CheckoutUiState, onTeam: (TeamState) -> Unit, modifier: Modifier = Modifier) {
    val team = state.team
    Column(
        modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.xl),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.md))
                .background(AppTheme.colors.card)
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.md))
                .padding(AppTheme.spacing.xxl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                    Text(
                        stringResource(R.string.checkout_nav_team),
                        style = AppTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary,
                    )
                    Text(
                        stringResource(R.string.team_subtitle, team.members.size),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                    )
                }
                if (!team.inviteOpen) {
                    TeamFilledButton(
                        label = stringResource(R.string.team_invite),
                        onClick = { onTeam(team.openInvite()) },
                    )
                }
            }
            if (team.inviteOpen) {
                InviteForm(team = team, onTeam = onTeam)
            }
            team.members.forEachIndexed { index, member ->
                TeamRow(
                    member = member,
                    menuOpen = team.menuForId == member.id,
                    onMenu = {
                        onTeam(
                            team.copy(
                                menuForId = if (team.menuForId == member.id) null else member.id,
                                inviteRoleMenuOpen = false,
                                editRoleMenuOpen = false,
                            ),
                        )
                    },
                    onDismissMenu = { onTeam(team.copy(menuForId = null)) },
                    onEdit = { onTeam(team.openEdit(member.id)) },
                    onRemove = { onTeam(team.openRemove(member.id)) },
                )
                if (index < team.members.lastIndex) {
                    TeamRowDivider()
                }
            }
        }
    }
}

@Composable
fun TeamDialogHost(state: CheckoutUiState, onTeam: (TeamState) -> Unit) {
    val team = state.team
    when (team.dialog) {
        TeamDialog.Edit -> EditMemberDialog(team, onTeam)
        TeamDialog.Remove -> RemoveMemberDialog(team, onTeam)
        TeamDialog.None -> Unit
    }
}

@Composable
private fun InviteForm(team: TeamState, onTeam: (TeamState) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        verticalAlignment = Alignment.Bottom,
    ) {
        TeamCompactField(
            label = stringResource(R.string.team_field_email),
            value = team.inviteEmail,
            placeholder = stringResource(R.string.team_email_hint),
            modifier = Modifier.weight(1.2f),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onValue = { onTeam(team.copy(inviteEmail = it, inviteRoleMenuOpen = false)) },
        )
        TeamCompactField(
            label = stringResource(R.string.team_field_name),
            value = team.inviteName,
            placeholder = stringResource(R.string.team_name_hint),
            modifier = Modifier.weight(1f),
            imeAction = ImeAction.Next,
            onValue = { onTeam(team.copy(inviteName = it, inviteRoleMenuOpen = false)) },
        )
        Box(Modifier.weight(1f)) {
            TeamRoleField(
                label = stringResource(R.string.team_field_role),
                selected = team.inviteRole,
                placeholder = stringResource(R.string.team_role_hint),
                menuOpen = team.inviteRoleMenuOpen,
                allowOwner = false,
                onToggle = { onTeam(team.copy(inviteRoleMenuOpen = !team.inviteRoleMenuOpen, menuForId = null)) },
                onDismiss = { onTeam(team.copy(inviteRoleMenuOpen = false)) },
                onSelect = { role -> onTeam(team.copy(inviteRole = role, inviteRoleMenuOpen = false)) },
            )
        }
        TeamCompactField(
            label = stringResource(R.string.team_field_pin),
            value = team.invitePin,
            placeholder = stringResource(R.string.team_pin_hint),
            modifier = Modifier.weight(0.9f),
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
            onValue = { raw ->
                onTeam(team.copy(invitePin = raw.filter { it.isDigit() }.take(TEAM_PIN_LENGTH), inviteRoleMenuOpen = false))
            },
        )
        TeamFilledButton(
            label = stringResource(R.string.team_create_now),
            onClick = { onTeam(team.createInvite()) },
            enabled = team.canCreateInvite,
        )
    }
}

@Composable
private fun EditMemberDialog(team: TeamState, onTeam: (TeamState) -> Unit) {
    val member = team.editingMember ?: return
    TeamFigmaDialog(
        onDismiss = { onTeam(team.dismissDialog()) },
        width = AppTheme.sizes.dialogMaxWidth,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            ) {
                Text(
                    stringResource(R.string.team_edit_title, member.name),
                    style = AppTheme.typography.titleLarge,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    stringResource(R.string.team_edit_subtitle),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                )
            }
            Text(
                stringResource(R.string.checkout_dismiss),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textMuted,
                modifier = Modifier
                    .clickable { onTeam(team.dismissDialog()) }
                    .padding(AppTheme.spacing.xs),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
            TeamCompactField(
                label = stringResource(R.string.team_field_email),
                value = team.editEmail,
                placeholder = stringResource(R.string.team_email_hint),
                enabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onValue = {},
            )
            TeamCompactField(
                label = stringResource(R.string.team_field_name),
                value = team.editName,
                placeholder = stringResource(R.string.team_name_hint),
                imeAction = ImeAction.Next,
                onValue = { onTeam(team.copy(editName = it, editRoleMenuOpen = false)) },
            )
            TeamRoleField(
                label = stringResource(R.string.team_field_role),
                selected = team.editRole,
                placeholder = stringResource(R.string.team_role_hint),
                menuOpen = team.editRoleMenuOpen,
                allowOwner = member.role == TeamRole.Owner,
                enabled = member.role != TeamRole.Owner,
                useBadgeLabel = true,
                onToggle = {
                    if (member.role != TeamRole.Owner) {
                        onTeam(team.copy(editRoleMenuOpen = !team.editRoleMenuOpen))
                    }
                },
                onDismiss = { onTeam(team.copy(editRoleMenuOpen = false)) },
                onSelect = { role -> onTeam(team.copy(editRole = role, editRoleMenuOpen = false)) },
            )
            TeamCompactField(
                label = stringResource(R.string.team_field_pin),
                value = team.editPin,
                placeholder = stringResource(R.string.team_pin_hint),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onValue = { raw ->
                    onTeam(
                        team.copy(
                            editPin = raw.filter { it.isDigit() }.take(TEAM_PIN_LENGTH),
                            editRoleMenuOpen = false,
                        ),
                    )
                },
            )
        }
        TeamDialogFooter(
            secondaryLabel = stringResource(R.string.team_edit_close),
            primaryLabel = stringResource(R.string.team_edit_save),
            secondaryBorderStrong = true,
            onSecondary = { onTeam(team.dismissDialog()) },
            onPrimary = { onTeam(team.saveEdit()) },
        )
    }
}

@Composable
private fun RemoveMemberDialog(team: TeamState, onTeam: (TeamState) -> Unit) {
    val member = team.editingMember ?: return
    val first = member.name.substringBefore(' ').ifBlank { member.name }
    TeamFigmaDialog(
        onDismiss = { onTeam(team.dismissDialog()) },
        width = AppTheme.sizes.checkoutDetailWidth,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Text(
                stringResource(R.string.team_remove_title, member.name),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                stringResource(R.string.team_remove_body, first),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
            )
        }
        TeamDialogFooter(
            secondaryLabel = stringResource(R.string.action_cancel),
            primaryLabel = stringResource(R.string.team_remove_confirm),
            secondaryBorderStrong = false,
            onSecondary = { onTeam(team.dismissDialog()) },
            onPrimary = { onTeam(team.confirmRemove()) },
        )
    }
}

@Composable
private fun TeamFigmaDialog(onDismiss: () -> Unit, width: Dp, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            Modifier
                .width(width)
                .shadow(elevation = AppTheme.spacing.xl, shape = shape)
                .clip(shape)
                .background(AppTheme.colors.card)
                .padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
            content = content,
        )
    }
}

@Composable
private fun TeamDialogFooter(
    secondaryLabel: String,
    primaryLabel: String,
    secondaryBorderStrong: Boolean,
    onSecondary: () -> Unit,
    onPrimary: () -> Unit,
) {
    val pill = RoundedCornerShape(AppTheme.radius.pill)
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Box(
            Modifier
                .weight(1f)
                .heightIn(min = AppTheme.sizes.buttonMedium)
                .clip(pill)
                .background(AppTheme.colors.card)
                .border(
                    if (secondaryBorderStrong) AppTheme.border.medium else AppTheme.border.thin,
                    if (secondaryBorderStrong) {
                        AppTheme.colors.textPrimary
                    } else {
                        AppTheme.colors.textPrimary.copy(alpha = 0.14f)
                    },
                    pill,
                )
                .clickable(onClick = onSecondary)
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                secondaryLabel,
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            Modifier
                .weight(1f)
                .heightIn(min = AppTheme.sizes.buttonMedium)
                .clip(pill)
                .background(AppTheme.colors.primary)
                .clickable(onClick = onPrimary)
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                primaryLabel,
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.card,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TeamOverflowMenu(member: TeamMember, onEdit: () -> Unit, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        Modifier
            .width(AppTheme.sizes.minTouchTarget * 3)
            .shadow(elevation = AppTheme.spacing.sm, shape = shape)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape),
    ) {
        TeamMenuRow(
            icon = Icons.Outlined.Edit,
            label = stringResource(R.string.team_menu_edit),
            onClick = onEdit,
        )
        if (member.role != TeamRole.Owner) {
            TeamRowDivider()
            TeamMenuRow(
                icon = Icons.Outlined.Delete,
                label = stringResource(R.string.team_menu_remove),
                onClick = onRemove,
            )
        }
    }
}

@Composable
private fun TeamMenuRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppTheme.colors.textMuted,
            modifier = Modifier.size(AppTheme.sizes.iconSm),
        )
        Text(label, style = AppTheme.typography.labelMedium, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun TeamRow(
    member: TeamMember,
    menuOpen: Boolean,
    onMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val density = LocalDensity.current
    val menuOffsetY = with(density) { AppTheme.sizes.minTouchTarget.roundToPx() }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Box(
            Modifier
                .size(AppTheme.sizes.iconLg)
                .clip(CircleShape)
                .background(avatarColor(member.avatarIndex)),
            contentAlignment = Alignment.Center,
        ) {
            Text(member.initials, style = AppTheme.typography.labelMedium, color = AppTheme.colors.textPrimary)
        }
        Column(Modifier.weight(1f)) {
            Text(
                member.name,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                member.email,
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            Modifier
                .widthIn(min = AppTheme.sizes.minTouchTarget * 2)
                .clip(RoundedCornerShape(AppTheme.radius.xs))
                .background(if (member.role == TeamRole.Owner) AppTheme.colors.primary else AppTheme.colors.surface)
                .padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(member.role.badgeRes()),
                style = AppTheme.typography.labelSmall,
                color = if (member.role == TeamRole.Owner) AppTheme.colors.card else AppTheme.colors.textPrimary,
                maxLines = 1,
                softWrap = false,
            )
        }
        Text(
            if (member.inviteSent) stringResource(R.string.team_invite_sent) else member.lastActive,
            style = AppTheme.typography.labelSmall,
            color = if (member.inviteSent) AppTheme.colors.primary else AppTheme.colors.textMuted,
            modifier = Modifier.widthIn(min = AppTheme.sizes.minTouchTarget * 2),
            textAlign = TextAlign.Start,
            maxLines = 1,
            softWrap = false,
        )
        Box {
            Text(
                "⋯",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textMuted,
                modifier = Modifier
                    .clickable(onClick = onMenu)
                    .padding(AppTheme.spacing.sm),
            )
            if (menuOpen) {
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = IntOffset(0, menuOffsetY),
                    onDismissRequest = onDismissMenu,
                    properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                ) {
                    TeamOverflowMenu(
                        member = member,
                        onEdit = onEdit,
                        onRemove = onRemove,
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamRoleField(
    label: String,
    selected: TeamRole?,
    placeholder: String,
    menuOpen: Boolean,
    allowOwner: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (TeamRole) -> Unit,
    enabled: Boolean = true,
    useBadgeLabel: Boolean = false,
) {
    val roles = if (allowOwner) listOf(TeamRole.Owner) + InviteRoles else InviteRoles
    val density = LocalDensity.current
    val menuOffsetY = with(density) { (AppTheme.sizes.inputHeight + AppTheme.spacing.xs).roundToPx() }
    val selectedLabel =
        selected?.let { role ->
            if (useBadgeLabel) role.badgeRes() else role.pickerRes()
        }
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted, maxLines = 1)
        BoxWithConstraints {
            val shape = RoundedCornerShape(AppTheme.radius.sm)
            val menuWidth = maxWidth
            val roleBorder =
                if (menuOpen) {
                    AppTheme.colors.primary
                } else {
                    AppTheme.colors.textPrimary.copy(alpha = 0.14f)
                }
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppTheme.sizes.inputHeight)
                    .clip(shape)
                    .background(AppTheme.colors.background)
                    .border(AppTheme.border.thin, roleBorder, shape)
                    .clickable(enabled = enabled, onClick = onToggle)
                    .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    if (selectedLabel == null) placeholder else stringResource(selectedLabel),
                    style = AppTheme.typography.bodyMedium,
                    color = if (selectedLabel == null) AppTheme.colors.textMuted else AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(AppTheme.sizes.iconMd),
                )
            }
            if (menuOpen) {
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(0, menuOffsetY),
                    onDismissRequest = onDismiss,
                    properties = PopupProperties(focusable = true),
                ) {
                    Column(
                        Modifier
                            .width(menuWidth)
                            .background(AppTheme.colors.card)
                            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
                    ) {
                        roles.forEachIndexed { index, role ->
                            Text(
                                stringResource(role.pickerRes()),
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.textPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(role) }
                                    .padding(
                                        horizontal = AppTheme.spacing.md,
                                        vertical = AppTheme.spacing.md,
                                    ),
                            )
                            if (index < roles.lastIndex) {
                                TeamRowDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamCompactField(
    label: String,
    value: String,
    placeholder: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val view = LocalView.current
    val interaction = remember { MutableInteractionSource() }
    var focused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    val borderColor =
        when {
            !enabled -> AppTheme.colors.textPrimary.copy(alpha = 0.08f)
            focused -> AppTheme.colors.primary
            else -> AppTheme.colors.textPrimary.copy(alpha = 0.14f)
        }
    val textColor = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textMuted
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted, maxLines = 1)
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = AppTheme.sizes.inputHeight)
                .clip(shape)
                .background(AppTheme.colors.background)
                .border(AppTheme.border.thin, borderColor, shape)
                .alpha(if (enabled) 1f else 0.4f)
                .clickable(
                    enabled = enabled,
                    interactionSource = interaction,
                    indication = null,
                    onClick = {
                        focusRequester.requestFocus()
                        showTeamIme(view, keyboard)
                    },
                )
                .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValue,
                enabled = enabled,
                singleLine = true,
                textStyle = AppTheme.typography.bodyMedium.copy(color = textColor),
                cursorBrush = SolidColor(AppTheme.colors.primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { state ->
                        focused = state.isFocused
                        if (state.isFocused && enabled) {
                            showTeamIme(view, keyboard)
                        }
                    },
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    inner()
                },
            )
        }
    }
}

private fun showTeamIme(view: View, keyboard: SoftwareKeyboardController?) {
    keyboard?.show()
    view.post {
        val window = (view.context as? Activity)?.window ?: return@post
        WindowCompat.getInsetsController(window, view).show(WindowInsetsCompat.Type.ime())
        val imm = view.context.getSystemService(InputMethodManager::class.java) ?: return@post
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

@Composable
private fun TeamFilledButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Box(
        modifier
            .heightIn(min = AppTheme.sizes.inputHeight)
            .clip(shape)
            .background(AppTheme.colors.primary)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.onPrimary.copy(alpha = if (enabled) 1f else 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TeamRowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(AppTheme.border.thin)
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
    )
}

@Composable
private fun avatarColor(index: Int): Color {
    val colors = listOf(
        AppTheme.colors.primary,
        AppTheme.colors.warning,
        AppTheme.colors.info,
        AppTheme.colors.surface,
        AppTheme.colors.warning.copy(alpha = 0.7f),
        AppTheme.colors.info.copy(alpha = 0.7f),
    )
    return colors[index.mod(colors.size)]
}

@ScreenPreviews
@Composable
private fun TeamContentPreview() {
    PreviewSurface {
        TeamContent(state = CheckoutUiState(section = KitchenSection.Team), onTeam = {})
    }
}

@ScreenPreviews
@Composable
private fun TeamInvitePreview() {
    PreviewSurface {
        TeamContent(
            state = CheckoutUiState(
                section = KitchenSection.Team,
                team = TeamState(inviteOpen = true),
            ),
            onTeam = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun TeamEditDialogPreview() {
    PreviewSurface {
        Box(Modifier.fillMaxSize()) {
            TeamContent(
                state = CheckoutUiState(section = KitchenSection.Team, team = TeamState().openEdit("luca")),
                onTeam = {},
            )
            TeamDialogHost(
                state = CheckoutUiState(section = KitchenSection.Team, team = TeamState().openEdit("luca")),
                onTeam = {},
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun TeamRemoveDialogPreview() {
    PreviewSurface {
        Box(Modifier.fillMaxSize()) {
            TeamDialogHost(
                state = CheckoutUiState(section = KitchenSection.Team, team = TeamState().openRemove("luca")),
                onTeam = {},
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun PosRailTeamPreview() {
    PreviewSurface {
        Row(Modifier.fillMaxSize()) {
            KitchenRail(section = KitchenSection.Team, deviceRole = DeviceRole.Pos)
            TeamContent(
                state = CheckoutUiState(section = KitchenSection.Team, deviceRole = DeviceRole.Pos),
                onTeam = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
