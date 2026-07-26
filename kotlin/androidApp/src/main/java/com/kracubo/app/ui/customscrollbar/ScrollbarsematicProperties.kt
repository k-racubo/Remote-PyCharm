package com.kracubo.app.ui.customscrollbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.SemanticsPropertyKey

object ScrollbarSemanticProperties {
    object Keys {
        const val BAR_BOUNDS: String = "barBounds"
        const val INDICATOR_BOUNDS: String = "indicatorBounds"
        const val INDICATOR_OFFSET: String = "indicatorOffset"
        const val SHOW_ALWAYS: String = "showAlways"
        const val IS_DRAGGING: String = "isDragging"
        const val IS_VISIBLE: String = "isVisible"
    }

    val BarBounds = SemanticsPropertyKey<Rect>(Keys.BAR_BOUNDS)
    val IndicatorBounds = SemanticsPropertyKey<Rect>(Keys.INDICATOR_BOUNDS)
    val IndicatorOffset = SemanticsPropertyKey<Float>(Keys.INDICATOR_OFFSET)
    val Direction = SemanticsPropertyKey<Orientation>(Keys.INDICATOR_OFFSET)
    val ShowAlways = SemanticsPropertyKey<Boolean>(Keys.SHOW_ALWAYS)
    val IsDragging = SemanticsPropertyKey<Boolean>(Keys.IS_DRAGGING)
    val IsVisible = SemanticsPropertyKey<Boolean>(Keys.IS_VISIBLE)
    val State = SemanticsPropertyKey<ScrollbarState>("state")
}
