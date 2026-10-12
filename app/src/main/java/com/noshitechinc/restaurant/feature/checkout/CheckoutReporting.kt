package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.role.DeviceRole

private data class ReportMetric(val titleRes: Int, val valueRes: Int, val deltaRes: Int, val metaRes: Int)

private data class ItemMover(
    val rank: Int,
    val name: String,
    val sold: Int,
    val revenue: String,
    val growthPercent: String,
    val spark: List<Float>,
    val positive: Boolean,
)

private data class CohortRow(val label: String, val size: String, val weeks: List<Int?>)

@Composable
fun ReportingContent(state: CheckoutUiState, onReporting: (ReportingState) -> Unit, onExport: () -> Unit, modifier: Modifier = Modifier) {
    val metrics = listOf(
        ReportMetric(
            R.string.reporting_metric_revenue,
            R.string.reporting_revenue_value,
            R.string.reporting_delta_revenue,
            R.string.reporting_meta_revenue,
        ),
        ReportMetric(
            R.string.reporting_metric_orders,
            R.string.reporting_orders_value,
            R.string.reporting_delta_orders,
            R.string.reporting_meta_orders,
        ),
        ReportMetric(
            R.string.reporting_metric_avg_ticket,
            R.string.reporting_avg_ticket_value,
            R.string.reporting_delta_avg,
            R.string.reporting_meta_avg,
        ),
        ReportMetric(
            R.string.reporting_metric_repeat,
            R.string.reporting_repeat_value,
            R.string.reporting_delta_repeat,
            R.string.reporting_meta_repeat,
        ),
    )
    val movers = listOf(
        ItemMover(1, "Pepperoni Large", 247, "$5,434", "47%", listOf(0.3f, 0.45f, 0.4f, 0.7f, 0.85f, 1f), true),
        ItemMover(2, "Margherita", 284, "$5,112", "12%", listOf(0.5f, 0.55f, 0.5f, 0.6f, 0.65f, 0.7f), true),
        ItemMover(3, "Diavola", 142, "$3,408", "24%", listOf(0.35f, 0.5f, 0.55f, 0.6f, 0.75f, 0.9f), true),
        ItemMover(4, "Caesar Salad", 198, "$2,871", "8%", listOf(0.55f, 0.5f, 0.48f, 0.45f, 0.42f, 0.4f), false),
        ItemMover(5, "Funghi", 64, "$1,600", "34%", listOf(0.7f, 0.6f, 0.55f, 0.45f, 0.35f, 0.3f), false),
    )
    val cohorts = listOf(
        CohortRow("Feb 1 — Feb 7", "184", listOf(86, 62, 48, 38)),
        CohortRow("Feb 8 — Feb 14", "201", listOf(81, 58, 44, 31)),
        CohortRow("Feb 15 — Feb 21", "176", listOf(78, 54, 41, null)),
        CohortRow("Feb 22 — Feb 28", "192", listOf(84, 59, null, null)),
        CohortRow("Mar 1 — Mar 7", "210", listOf(79, null, null, null)),
        CohortRow("Mar 8 — Mar 14", "188", listOf(82, null, null, null)),
        CohortRow("Mar 15 — Mar 21", "195", listOf(76, null, null, null)),
    )
    val days = listOf(
        R.string.reporting_day_mon,
        R.string.reporting_day_tue,
        R.string.reporting_day_wed,
        R.string.reporting_day_thu,
        R.string.reporting_day_fri,
        R.string.reporting_day_sat,
        R.string.reporting_day_sun,
    )
    val hours = listOf("11", "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    val busy = listOf(
        listOf(0.2f, 0.25f, 0.35f, 0.3f, 0.25f, 0.3f, 0.45f, 0.55f, 0.5f, 0.4f, 0.3f, 0.25f, 0.2f, 0.15f),
        listOf(0.25f, 0.3f, 0.4f, 0.35f, 0.3f, 0.35f, 0.5f, 0.6f, 0.55f, 0.45f, 0.35f, 0.3f, 0.25f, 0.2f),
        listOf(0.3f, 0.35f, 0.45f, 0.4f, 0.35f, 0.4f, 0.55f, 0.65f, 0.6f, 0.5f, 0.4f, 0.35f, 0.3f, 0.25f),
        listOf(0.35f, 0.4f, 0.5f, 0.45f, 0.4f, 0.45f, 0.6f, 0.7f, 0.65f, 0.55f, 0.45f, 0.4f, 0.35f, 0.3f),
        listOf(0.45f, 0.5f, 0.6f, 0.55f, 0.5f, 0.55f, 0.75f, 0.95f, 1f, 0.85f, 0.65f, 0.55f, 0.45f, 0.35f),
        listOf(0.5f, 0.55f, 0.65f, 0.6f, 0.55f, 0.6f, 0.8f, 0.9f, 0.85f, 0.75f, 0.6f, 0.5f, 0.4f, 0.3f),
        listOf(0.4f, 0.45f, 0.55f, 0.5f, 0.45f, 0.5f, 0.7f, 0.8f, 0.75f, 0.65f, 0.5f, 0.4f, 0.35f, 0.25f),
    )
    val adaptive = rememberAdaptiveInfo()
    Box(modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
        ) {
            ReportingHeader(state.reporting, onReporting, onExport)
            ReportingSectionLabel(stringResource(R.string.reporting_core_metrics))
            if (adaptive.usesTwoPane) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                    metrics.forEach { MetricCard(it, Modifier.weight(1f)) }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                    metrics.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                            row.forEach { MetricCard(it, Modifier.weight(1f)) }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            RevenueChartCard()
            if (adaptive.usesTwoPane) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                ) {
                    CohortCard(cohorts, Modifier.weight(1.1f).fillMaxHeight())
                    MoversCard(movers, Modifier.weight(1f).fillMaxHeight())
                }
            } else {
                CohortCard(cohorts, Modifier.fillMaxWidth())
                MoversCard(movers, Modifier.fillMaxWidth())
            }
            BusyHoursCard(days, hours, busy)
        }
        if (state.reporting.pickerOpen) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.scrim)
                        .clickable { onReporting(state.reporting.dismissPicker()) },
                )
                ReportingDatePicker(
                    reporting = state.reporting,
                    onReporting = onReporting,
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .padding(AppTheme.spacing.xl),
                )
            }
        }
    }
}

@Composable
private fun ReportingHeader(reporting: ReportingState, onReporting: (ReportingState) -> Unit, onExport: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
        Text(
            stringResource(reporting.compareRes),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textMuted,
        )
        // Figma Reporting: Fraunces SemiBold 36 / -2%; “doing” SemiBold Italic primary; period textPrimary.
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = AppTheme.colors.textPrimary)) {
                    append(stringResource(R.string.reporting_headline_prefix))
                    append('\u00A0')
                }
                withStyle(
                    SpanStyle(
                        color = AppTheme.colors.primary,
                        fontStyle = FontStyle.Italic,
                    ),
                ) {
                    append(stringResource(R.string.reporting_headline_emphasis))
                }
                withStyle(SpanStyle(color = AppTheme.colors.textPrimary)) {
                    append('.')
                }
            },
            style = AppTheme.typography.headlineMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ReportingActionChip(
                label = stringResource(reporting.rangeLabelRes),
                onClick = { onReporting(reporting.openPicker()) },
            )
            ReportingActionChip(
                label = stringResource(R.string.reporting_export_pdf),
                onClick = onExport,
            )
        }
    }
}

/** Figma Reporting chips: 36h, radius 8, 1px border @ 18% ink, white fill. */
@Composable
private fun ReportingActionChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Box(
        modifier
            .heightIn(min = AppTheme.sizes.buttonSmall)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.18f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Figma section label + trailing hairline rule. */
@Composable
private fun ReportingSectionLabel(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Text(
            label,
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.55f),
            maxLines = 1,
        )
        Box(
            Modifier
                .weight(1f)
                .height(AppTheme.border.thin)
                .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
        )
    }
}

@Composable
private fun MetricCard(metric: ReportMetric, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.md))
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Text(stringResource(metric.titleRes), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
        Text(stringResource(metric.valueRes), style = AppTheme.typography.headlineSmall, color = AppTheme.colors.textPrimary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            GrowthPill(
                label = stringResource(R.string.reporting_growth_up, stringResource(metric.deltaRes)),
                positive = true,
            )
            Text(
                stringResource(R.string.reporting_delta_vs),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
            )
        }
        Text(stringResource(metric.metaRes), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
    }
}

@Composable
private fun RevenueChartCard() {
    val bars = listOf(
        0.45f to 0.25f, 0.5f to 0.28f, 0.42f to 0.22f, 0.55f to 0.3f,
        0.6f to 0.32f, 0.52f to 0.28f, 0.48f to 0.26f, 0.7f to 0.35f,
        0.85f to 0.4f, 0.65f to 0.33f, 0.58f to 0.3f, 0.62f to 0.32f,
        0.55f to 0.28f, 0.5f to 0.26f, 0.72f to 0.36f, 0.68f to 0.34f,
        0.6f to 0.3f, 0.95f to 0.45f, 0.8f to 0.38f, 0.7f to 0.35f,
        0.65f to 0.32f, 0.58f to 0.28f, 0.62f to 0.3f, 0.55f to 0.27f,
    )
    val axis = listOf(
        R.string.reporting_axis_apr1,
        R.string.reporting_axis_apr7,
        R.string.reporting_axis_apr14,
        R.string.reporting_axis_apr21,
    )
    ReportCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(
                    stringResource(R.string.reporting_chart_title),
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    stringResource(R.string.reporting_peak_day),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LegendDot(stringResource(R.string.reporting_legend_revenue), AppTheme.colors.primary)
                LegendDot(stringResource(R.string.reporting_legend_orders), AppTheme.colors.textPrimary)
                GrowthPill(
                    label = stringResource(R.string.reporting_growth_up, stringResource(R.string.reporting_chart_delta_pct)),
                    positive = true,
                )
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(AppTheme.sizes.cardImage * 2),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { (revenue, orders) ->
                val total = (revenue + orders).coerceAtLeast(0.08f)
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(total),
                ) {
                    // Orders (grey) stacked above Revenue (orange), matching Figma.
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(orders.coerceAtLeast(0.04f))
                            .clip(RoundedCornerShape(topStart = AppTheme.radius.xs, topEnd = AppTheme.radius.xs))
                            .background(AppTheme.colors.textPrimary.copy(alpha = 0.85f)),
                    )
                    Spacer(Modifier.height(AppTheme.border.thin))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(revenue.coerceAtLeast(0.04f))
                            .background(AppTheme.colors.primary),
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            axis.forEach {
                Text(stringResource(it), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
            }
        }
    }
}

@Composable
private fun CohortCard(rows: List<CohortRow>, modifier: Modifier = Modifier) {
    ReportCard(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                stringResource(R.string.reporting_cohort_title),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                stringResource(R.string.reporting_cohort_subtitle),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Text(
                stringResource(R.string.reporting_cohort_col),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                modifier = Modifier.weight(1.4f),
            )
            Text(
                stringResource(R.string.reporting_size_col),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                modifier = Modifier.width(AppTheme.sizes.buttonHorizontalPadding * 2),
                textAlign = TextAlign.End,
            )
            listOf(R.string.reporting_w1, R.string.reporting_w2, R.string.reporting_w3, R.string.reporting_w4).forEach {
                Text(
                    stringResource(it),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                    modifier = Modifier.weight(0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        }
        rows.forEach { row ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = AppTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                Text(
                    row.label,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.weight(1.4f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    row.size,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.width(AppTheme.sizes.buttonHorizontalPadding * 2),
                    textAlign = TextAlign.End,
                )
                row.weeks.forEach { value ->
                    Box(Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                        if (value == null) {
                            Text("—", style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
                        } else {
                            val alpha = (value / 100f).coerceIn(0.25f, 1f)
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(AppTheme.radius.xs))
                                    .background(AppTheme.colors.primary.copy(alpha = alpha))
                                    .padding(vertical = AppTheme.spacing.xs),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "$value%",
                                    style = AppTheme.typography.labelSmall,
                                    color = if (alpha > 0.55f) AppTheme.colors.card else AppTheme.colors.textPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoversCard(movers: List<ItemMover>, modifier: Modifier = Modifier) {
    ReportCard(modifier) {
        Text(
            stringResource(R.string.reporting_movers_title),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
        )
        movers.forEach { mover ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                Text(
                    mover.rank.toString(),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.width(AppTheme.sizes.iconMd),
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        mover.name,
                        style = AppTheme.typography.titleSmall,
                        color = AppTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stringResource(R.string.reporting_sold_format, mover.sold, mover.revenue),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                    )
                }
                Sparkline(
                    values = mover.spark,
                    positive = mover.positive,
                    modifier = Modifier.width(AppTheme.sizes.illustration).height(AppTheme.sizes.iconMd),
                )
                GrowthPill(
                    label = stringResource(
                        if (mover.positive) R.string.reporting_growth_up else R.string.reporting_growth_down,
                        mover.growthPercent,
                    ),
                    positive = mover.positive,
                )
            }
        }
    }
}

@Composable
private fun BusyHoursCard(days: List<Int>, hours: List<String>, busy: List<List<Float>>) {
    ReportCard {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                stringResource(R.string.reporting_busy_title),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                stringResource(R.string.reporting_busy_peak),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.primary,
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Spacer(Modifier.width(AppTheme.sizes.buttonHorizontalPadding * 2))
            hours.forEach { hour ->
                Text(
                    hour,
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
        days.forEachIndexed { dayIndex, dayRes ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = AppTheme.spacing.xxs),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(dayRes),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                    modifier = Modifier.width(AppTheme.sizes.buttonHorizontalPadding * 2),
                )
                busy[dayIndex].forEach { intensity ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(AppTheme.sizes.iconMd)
                            .clip(RoundedCornerShape(AppTheme.radius.xs))
                            .background(AppTheme.colors.primary.copy(alpha = intensity.coerceIn(0.2f, 1f))),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportCard(modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.md))
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        content = content,
    )
}

@Composable
private fun GrowthPill(label: String, positive: Boolean) {
    Box(
        Modifier
            .clip(RoundedCornerShape(AppTheme.radius.sm))
            .background(
                if (positive) AppTheme.colors.secondary else AppTheme.colors.surface,
            )
            .padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
    ) {
        Text(
            label,
            style = AppTheme.typography.labelSmall,
            color = if (positive) AppTheme.colors.onSecondary else AppTheme.colors.textPrimary,
            maxLines = 1,
        )
    }
}

@Composable
private fun Sparkline(values: List<Float>, positive: Boolean, modifier: Modifier = Modifier) {
    val barColor = if (positive) AppTheme.colors.primary else AppTheme.colors.textMuted.copy(alpha = 0.55f)
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEach { value ->
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(value.coerceIn(0.15f, 1f))
                    .clip(RoundedCornerShape(AppTheme.radius.xs))
                    .background(barColor),
            )
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
    ) {
        Box(
            Modifier
                .size(AppTheme.spacing.md)
                .background(color, RoundedCornerShape(AppTheme.radius.xs)),
        )
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
    }
}

@ScreenPreviews
@Composable
private fun ReportingContentPreview() {
    PreviewSurface {
        ReportingContent(CheckoutUiState(section = KitchenSection.Reporting), onReporting = {}, onExport = {})
    }
}

@ScreenPreviews
@Composable
private fun ReportingDatePickerPreview() {
    PreviewSurface {
        ReportingContent(
            CheckoutUiState(
                section = KitchenSection.Reporting,
                reporting = ReportingState(pickerOpen = true, preset = ReportRangePreset.Days7),
            ),
            onReporting = {},
            onExport = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun PosRailReportingPreview() {
    PreviewSurface {
        Row(Modifier.fillMaxSize()) {
            KitchenRail(
                section = KitchenSection.Reporting,
                deviceRole = DeviceRole.Pos,
            )
            ReportingContent(
                CheckoutUiState(section = KitchenSection.Reporting, deviceRole = DeviceRole.Pos),
                onReporting = {},
                onExport = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
