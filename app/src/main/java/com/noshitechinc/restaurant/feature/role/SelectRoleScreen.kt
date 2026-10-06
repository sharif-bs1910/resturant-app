package com.noshitechinc.restaurant.feature.role

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.AppScaffold

@Composable
fun SelectRoleRoute(onContinue: () -> Unit, viewModel: SelectRoleViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppScaffold(effects = viewModel.effects, onEffect = { effect -> if (effect is OpenSignIn) onContinue() }) {
        SelectRoleScreen(state = state, onSelect = viewModel::onSelect, onContinue = viewModel::onContinue)
    }
}

@Composable
fun SelectRoleScreen(state: SelectRoleUiState, onSelect: (DeviceRole) -> Unit, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(AppTheme.colors.background)) {
        RoleTopBar()
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(top = AppTheme.sizes.wizardBodyTop, bottom = AppTheme.spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxxl, Alignment.CenterVertically),
            ) {
                RoleHeading()
                Column(
                    modifier = Modifier
                        .widthIn(max = AppTheme.sizes.wizardBodyWidth)
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
                ) {
                    RoleCards(selected = state.selected, onSelect = onSelect)
                    ContinueButton(enabled = state.selected != null, onClick = onContinue)
                }
            }
        }
    }
}

@Composable
private fun RoleTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.sizes.wizardBarPaddingHorizontal,
                vertical = AppTheme.sizes.wizardBarPaddingVertical,
            ),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.role_brand),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.role_location),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.5f),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Surface(
            shape = RoundedCornerShape(AppTheme.radius.sm),
            color = AppTheme.colors.textPrimary,
        ) {
            Text(
                text = stringResource(R.string.role_paired),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.background,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.sm),
            )
        }
    }
}

@Composable
private fun RoleHeading() {
    Column(
        modifier = Modifier.widthIn(max = AppTheme.sizes.wizardHeadingWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
    ) {
        Text(
            text = stringResource(R.string.role_title),
            style = AppTheme.typography.headlineMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = stringResource(R.string.role_subtitle),
            style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            color = AppTheme.colors.textPrimary.copy(alpha = 0.65f),
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RoleCards(selected: DeviceRole?, onSelect: (DeviceRole) -> Unit) {
    val fontScale = LocalDensity.current.fontScale
    val minCard = AppTheme.sizes.wizardHeadingWidth / 2 * fontScale.coerceIn(1f, 2f)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cardWidth = (maxWidth - AppTheme.spacing.lg * (DeviceRole.entries.size - 1)) / DeviceRole.entries.size
        if (cardWidth >= minCard) {
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                DeviceRole.entries.forEach { role ->
                    RoleCard(
                        role = role,
                        selected = role == selected,
                        onClick = { onSelect(role) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                DeviceRole.entries.forEach { role ->
                    RoleCard(
                        role = role,
                        selected = role == selected,
                        onClick = { onSelect(role) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleCard(role: DeviceRole, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier = modifier
            .clip(shape)
            .background(AppTheme.colors.card, shape)
            .then(
                if (selected) {
                    Modifier.border(BorderStroke(AppTheme.border.focus, AppTheme.colors.primary), shape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick)
            .semantics { this.selected = selected }
            .padding(AppTheme.sizes.roleCardPadding),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Text(
                text = stringResource(role.titleRes),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            RoleRadio(selected = selected)
        }
        Text(
            text = stringResource(role.bodyRes),
            style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            color = AppTheme.colors.textPrimary.copy(alpha = 0.55f),
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RoleRadio(selected: Boolean) {
    Box(modifier = Modifier.size(AppTheme.sizes.roleRadio), contentAlignment = Alignment.Center) {
        Surface(
            shape = CircleShape,
            color = AppTheme.colors.card,
            border = BorderStroke(
                AppTheme.border.medium,
                if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.28f),
            ),
            modifier = Modifier.size(AppTheme.sizes.roleRadio),
        ) {}
        if (selected) {
            Box(
                modifier = Modifier
                    .size(AppTheme.spacing.md)
                    .background(AppTheme.colors.primary, CircleShape),
            )
        }
    }
}

@Composable
private fun ContinueButton(enabled: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(AppTheme.radius.pill),
        color = AppTheme.colors.primary,
        modifier = Modifier.alpha(if (enabled) 1f else 0.2f),
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = AppTheme.sizes.buttonMedium)
                .padding(horizontal = AppTheme.spacing.xxl),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.role_continue),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.card,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SelectRolePreview() {
    PreviewSurface {
        SelectRoleScreen(state = SelectRoleUiState(), onSelect = {}, onContinue = {})
    }
}

@ScreenPreviews
@Composable
private fun SelectRoleChosenPreview() {
    PreviewSurface {
        SelectRoleScreen(state = SelectRoleUiState(selected = DeviceRole.SelfService), onSelect = {}, onContinue = {})
    }
}
