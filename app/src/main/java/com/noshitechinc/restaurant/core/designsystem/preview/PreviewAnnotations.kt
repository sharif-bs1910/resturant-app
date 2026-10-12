package com.noshitechinc.restaurant.core.designsystem.preview

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Tablet landscape", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
annotation class TabletLandscapePreview

@Preview(name = "Tablet portrait", device = "spec:width=800dp,height=1280dp,dpi=240", showBackground = true)
annotation class TabletPortraitPreview

@Preview(name = "Tablet medium width", widthDp = 800, showBackground = true)
@Preview(name = "Tablet expanded width", widthDp = 1280, showBackground = true)
annotation class ComponentPreviews

@TabletLandscapePreview
@TabletPortraitPreview
annotation class ScreenPreviews
