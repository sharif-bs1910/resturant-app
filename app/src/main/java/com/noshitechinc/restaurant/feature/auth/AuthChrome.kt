package com.noshitechinc.restaurant.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.WidthClass
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AuthSplit(paired: Boolean, body: String, modifier: Modifier = Modifier, form: @Composable () -> Unit) {
    val adaptive = rememberAdaptiveInfo()
    if (adaptive.usesTwoPane) {
        val expanded = adaptive.widthClass == WidthClass.Expanded
        Row(modifier = modifier.fillMaxSize()) {
            val brand = if (expanded) {
                Modifier.width(AppTheme.sizes.authBrandWidth)
            } else {
                Modifier.weight(1f)
            }
            AuthBrandPanel(paired = paired, body = body, modifier = brand.fillMaxHeight())
            Box(
                modifier = Modifier.weight(if (expanded) 1f else 1.4f).fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.widthIn(max = AppTheme.sizes.formMaxWidth).fillMaxHeight()) { form() }
            }
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            AuthBrandPanel(paired = paired, body = body, modifier = Modifier.weight(1f).fillMaxWidth())
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.widthIn(max = AppTheme.sizes.formMaxWidth).fillMaxSize()) { form() }
            }
        }
    }
}

@Composable
private fun AuthBrandPanel(paired: Boolean, body: String, modifier: Modifier = Modifier) {
    val padding = authPanelPadding()
    val top = if (rememberAdaptiveInfo().widthClass == WidthClass.Expanded) AppTheme.sizes.authPanelTop else padding
    BoxWithConstraints(modifier = modifier.background(AppTheme.colors.textPrimary)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(start = padding, end = padding, top = top, bottom = padding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)) {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                    Text(
                        text = stringResource(R.string.auth_brand_name),
                        style = AppTheme.typography.headlineMedium,
                        color = AppTheme.colors.background,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.auth_product_label).uppercase(),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = stringResource(R.string.auth_headline),
                    style = AppTheme.typography.headlineMedium,
                    color = AppTheme.colors.background,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = body,
                    style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                    color = AppTheme.colors.background,
                    modifier = Modifier.alpha(0.72f),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                Prop(stringResource(R.string.auth_prop_orders))
                Prop(stringResource(R.string.auth_prop_busy))
                Prop(stringResource(R.string.auth_prop_printer))
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                Box(
                    modifier = Modifier
                        .width(AppTheme.sizes.illustration)
                        .height(AppTheme.border.thin)
                        .background(AppTheme.colors.background.copy(alpha = 0.16f)),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(AppTheme.spacing.sm)
                            .background(if (paired) AppTheme.colors.success else AppTheme.colors.primary, CircleShape),
                    )
                    Text(
                        text = stringResource(if (paired) R.string.auth_status_connected else R.string.auth_status_not_paired).uppercase(),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.background,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(
                            R.string.auth_device_line,
                            stringResource(if (paired) R.string.auth_device_paired else R.string.auth_device_unpaired),
                        ),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.background,
                        modifier = Modifier.weight(1f).alpha(0.55f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun Prop(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(AppTheme.spacing.sm).background(AppTheme.colors.primary, CircleShape))
        Text(
            text = text,
            style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = AppTheme.colors.background,
            modifier = Modifier.weight(1f).alpha(0.92f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun AuthFormColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(authPanelPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl, Alignment.CenterVertically),
        ) {
            content()
        }
    }
}

@Composable
fun AuthHeading(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(
            text = title,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun AuthHint(label: String, body: String) {
    Column(
        modifier = Modifier
            .widthIn(max = AppTheme.sizes.keypadMaxWidth)
            .background(AppTheme.colors.surface, RoundedCornerShape(AppTheme.radius.sm))
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
    ) {
        Text(
            text = label.uppercase(),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = body,
            style = AppTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
            color = AppTheme.colors.textMuted,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun authPanelPadding(): Dp = when (rememberAdaptiveInfo().widthClass) {
    WidthClass.Expanded -> AppTheme.sizes.authPanelPadding
    WidthClass.Medium -> AppTheme.spacing.xxl
    WidthClass.Compact -> AppTheme.spacing.lg
}

@ComponentPreviews
@Composable
private fun AuthSplitPreview() {
    PreviewSurface {
        AuthSplit(paired = false, body = stringResource(R.string.auth_pairing_body)) {
            AuthHeading(title = stringResource(R.string.auth_pair_title), subtitle = stringResource(R.string.auth_pair_subtitle))
        }
    }
}

@ComponentPreviews
@Composable
private fun AuthSplitLongContentPreview() {
    PreviewSurface {
        AuthSplit(paired = true, body = PreviewData.LONG_DESCRIPTION) {
            AuthHeading(title = PreviewData.LONG_NAME, subtitle = PreviewData.ADDRESS_LONG)
            AuthHint(label = PreviewData.TRANSLATED_LABEL, body = PreviewData.LONG_DESCRIPTION)
        }
    }
}
