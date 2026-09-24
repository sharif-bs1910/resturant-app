package com.noshitechinc.restaurant.core.designsystem.preview

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Tablet landscape", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
annotation class TabletLandscapePreview

@Preview(name = "Tablet portrait", device = "spec:width=800dp,height=1280dp,dpi=240", showBackground = true)
annotation class TabletPortraitPreview

@Preview(name = "Font 1.0", fontScale = 1f, showBackground = true)
@Preview(name = "Font 1.5", fontScale = 1.5f, showBackground = true)
@Preview(name = "Font 2.0", fontScale = 2f, showBackground = true)
annotation class FontScalePreviews

@Preview(name = "Tablet medium width", widthDp = 800, showBackground = true)
@Preview(name = "Tablet expanded width", widthDp = 1280, showBackground = true)
@Preview(name = "Tablet medium width, font 2.0", widthDp = 800, fontScale = 2f, showBackground = true)
annotation class ComponentPreviews

@TabletLandscapePreview
@TabletPortraitPreview
@Preview(
    name = "Tablet landscape, font 2.0",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    fontScale = 2f,
    showBackground = true,
)
annotation class ScreenPreviews
