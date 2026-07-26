package com.kracubo.app.ui.customscrollbar

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultMiter
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.hashCode
import kotlin.math.max

fun DrawScope.drawDefaultScrollbar(
    measurements: ScrollbarMeasurements,
    config: ScrollbarConfig,
) = with(config) {
    val barColor = config.barColor
    val barBorderColor = config.barBorder.color
    val indicatorColor = config.indicatorColor
    val indicatorBorderColor = config.indicatorBorder.color

    // Draw bar
    if (!barColor.isTransparent) {
        val barCornerRadius = barCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) }

        drawRoundRect(
            paint = barColor,
            cornerRadius = barCornerRadius,
            topLeft = measurements.barBounds.topLeft,
            size = measurements.barBounds.size,
            alpha = measurements.alpha,
        )

        if (barBorder.width > 0.dp && !barBorderColor.isTransparent) {
            val borderBounds = measurements.barBounds
            drawRoundRect(
                paint = barBorderColor,
                cornerRadius = barCornerRadius,
                topLeft = borderBounds.topLeft,
                size = borderBounds.size,
                alpha = measurements.alpha,
                style = barBorder.toStroke(this@drawDefaultScrollbar),
            )
        }
    }

    // Draw indicator
    val indicatorCornerRadius = indicatorCornerRadius.let { CornerRadius(it.toPx(), it.toPx()) }

    drawRoundRect(
        paint = indicatorColor,
        cornerRadius = indicatorCornerRadius,
        topLeft = measurements.indicatorBounds.topLeft,
        size = measurements.indicatorBounds.size,
        alpha = measurements.alpha,
    )

    if (indicatorBorder.width > 0.dp && !indicatorBorderColor.isTransparent) {
        val borderBounds = measurements.indicatorBounds
        drawRoundRect(
            paint = indicatorBorderColor,
            cornerRadius = indicatorCornerRadius,
            topLeft = borderBounds.topLeft,
            size = borderBounds.size,
            alpha = measurements.alpha,
            style = indicatorBorder.toStroke(this@drawDefaultScrollbar),
        )
    }
}
val ColorType.isTransparent get() = this is ColorType.Solid && color.alpha == 0f

internal fun BorderStyle.toStroke(density: Density) =
    with(density) {
        Stroke(
            width.toPx(),
            miter?.toPx() ?: DefaultMiter,
            cap,
            join,
            pathEffect,
        )
    }
internal fun DrawScope.drawRoundRect(
    paint: ColorType,
    topLeft: Offset = Offset.Zero,
    size: Size = Size.Zero,
    cornerRadius: CornerRadius = CornerRadius.Zero,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode,
) {
    when (paint) {
        is ColorType.Solid -> {
            drawRoundRect(
                paint.color,
                cornerRadius = cornerRadius,
                topLeft = topLeft,
                size = size,
                alpha = alpha,
                style = style,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        }
        is ColorType.Gradient -> {
            drawRoundRect(
                paint.brush(Rect(topLeft, size)),
                cornerRadius = cornerRadius,
                topLeft = topLeft,
                size = size,
                alpha = alpha,
                style = style,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        }
    }
}

fun Modifier.scrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    showAlways: Boolean = false,
    autoHideAnimationSpec: AnimationSpec<Float>? = null,
    isDragEnabled: Boolean = true,
    onMeasureAndDraw: ScrollbarMeasureAndDraw,
): Modifier =
    this then ScrollbarModifierNodeElement(
        scrollState = scrollState,
        scrollbarState = scrollbarState,
        direction = direction,
        showAlways = showAlways,
        autoHideAnimationSpec = autoHideAnimationSpec,
        onMeasureAndDraw = onMeasureAndDraw,
    ).scrollbarDrag(scrollState, scrollbarState, direction, isDragEnabled)


@SuppressLint("SuspiciousModifierThen")
fun Modifier.scrollbar(
    scrollState: ScrollState,
    scrollbarState: ScrollbarState,
    direction: Orientation,
    config: ScrollbarConfig = ScrollbarConfig(),
    onDraw: DrawScope.(measurements: ScrollbarMeasurements) -> Unit = { measurements ->
        drawDefaultScrollbar(measurements, config)
    },
): Modifier =
    this then
            scrollbar(
                scrollState = scrollState,
                scrollbarState = scrollbarState,
                direction = direction,
                showAlways = config.showAlways,
                autoHideAnimationSpec = config.autoHideAnimationSpec,
                isDragEnabled = config.isDragEnabled,
            ) { layout ->
                val topPadding = config.padding.calculateTopPadding().toPx()
                val bottomPadding = config.padding.calculateBottomPadding().toPx()
                val startPadding = config.padding.calculateStartPadding(layout.layoutDirection).toPx()
                val endPadding = config.padding.calculateEndPadding(layout.layoutDirection).toPx()

                val isLtr = layout.layoutDirection == LayoutDirection.Ltr
                val isVertical = layout.orientation == Orientation.Vertical
                val barThicknessPx = config.barThickness.toPx()

                val scrollbarLength =
                    layout.calculateBarLength(topPadding, startPadding, bottomPadding, endPadding)

                val indicatorThicknessPx = config.indicatorThickness.toPx()

                val indicatorLength =
                    layout.calculateIndicatorLength(
                        scrollbarLength,
                        config.minimumIndicatorLength.toPx(),
                        config.maximumIndicatorLength.toPx(),
                    )

                val indicatorOffset = layout.calculateIndicatorOffset(scrollbarLength, indicatorLength)

                val scrollIndicatorSize =
                    if (isVertical) {
                        Size(indicatorThicknessPx, indicatorLength)
                    } else {
                        Size(indicatorLength, indicatorThicknessPx)
                    }

                val scrollIndicatorPosition =
                    if (isVertical) {
                        Offset(
                            x = (indicatorThicknessPx - barThicknessPx) / 2 +
                                    if (isLtr) {
                                        layout.viewPortCrossAxisLength - indicatorThicknessPx - endPadding
                                    } else {
                                        startPadding
                                    },
                            y = indicatorOffset + topPadding,
                        )
                    } else {
                        Offset(
                            x =
                                if (isLtr) {
                                    indicatorOffset + startPadding
                                } else {
                                    layout.viewPortLength - indicatorOffset - indicatorLength - endPadding
                                },
                            y = layout.viewPortCrossAxisLength - indicatorThicknessPx - bottomPadding +
                                    (indicatorThicknessPx - barThicknessPx) / 2,
                        )
                    }

                val scrollbarPosition =
                    if (isVertical) {
                        Offset(
                            x = layout.viewPortCrossAxisLength - barThicknessPx - endPadding,
                            y = topPadding,
                        )
                    } else {
                        Offset(
                            x = if (isLtr) startPadding else endPadding,
                            y = layout.viewPortCrossAxisLength - barThicknessPx - bottomPadding,
                        )
                    }

                val scrollbarSize =
                    if (isVertical) {
                        Size(barThicknessPx, layout.viewPortLength - topPadding - bottomPadding)
                    } else {
                        Size(layout.viewPortLength - startPadding - endPadding, barThicknessPx)
                    }

                val barBounds = Rect(scrollbarPosition, scrollbarSize)
                val indicatorBounds = Rect(scrollIndicatorPosition, scrollIndicatorSize)
                    .applyPadding(this, config.indicatorPadding, layout.layoutDirection)

                val measurements =
                    ScrollbarMeasurements(barBounds, indicatorBounds, layout.scrollbarAlpha)

                drawWithMeasurements(measurements) {
                    onDraw(this, measurements)
                }
            }

@SuppressLint("ModifierNodeInspectableProperties")
private data class ScrollbarModifierNodeElement(
    val scrollState: ScrollState,
    val scrollbarState: ScrollbarState,
    val direction: Orientation,
    val showAlways: Boolean = false,
    val autoHideAnimationSpec: AnimationSpec<Float>? = null,
    val onMeasureAndDraw: ScrollbarMeasureAndDraw,
) : ModifierNodeElement<ScrollbarModifierNode>() {
    override fun create(): ScrollbarModifierNode =
        ScrollbarModifierNode(
            scrollState = scrollState,
            scrollbarState = scrollbarState,
            direction = direction,
            showAlways = showAlways,
            autoHideAnimationSpec = autoHideAnimationSpec,
            onMeasureAndDraw = onMeasureAndDraw,
        )

    override fun update(node: ScrollbarModifierNode) {
        node.scrollState = scrollState
        node.scrollbarState = scrollbarState
        node.direction = direction
        node.showAlways = showAlways
        node.autoHideAnimationSpec = autoHideAnimationSpec
        node.onMeasureAndDraw = onMeasureAndDraw
    }

    override fun equals(other: Any?): Boolean {
        val otherElement = other as? ScrollbarModifierNodeElement ?: return false
        return scrollState == otherElement.scrollState &&
                scrollbarState == otherElement.scrollbarState &&
                direction == otherElement.direction &&
                showAlways == otherElement.showAlways &&
                autoHideAnimationSpec == otherElement.autoHideAnimationSpec &&
                onMeasureAndDraw == other.onMeasureAndDraw
    }

    override fun hashCode(): Int {
        var result = scrollState.hashCode()
        result = 31 * result + scrollbarState.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + showAlways.hashCode()
        result = 31 * result + autoHideAnimationSpec.hashCode()
        result = 31 * result + onMeasureAndDraw.hashCode()
        return result
    }
}
private class ScrollbarModifierNode(
    var scrollState: ScrollState,
    var scrollbarState: ScrollbarState,
    direction: Orientation,
    var showAlways: Boolean = false,
    var autoHideAnimationSpec: AnimationSpec<Float>? = null,
    var onMeasureAndDraw: ScrollbarMeasureAndDraw,
) : Modifier.Node(),
    DrawModifierNode,
    SemanticsModifierNode,
    ObserverModifierNode {
    var direction: Orientation = direction
        set(value) {
            field = value
            scrollbarState.isVertical = isVertical
        }

    val isScrollingOrPanning get() =
        scrollState.isScrollInProgress || scrollbarState.isScrollbarDragActive

    val isVertical get() = direction == Orientation.Vertical

    private val alpha = Animatable(1f)

    override fun onAttach() {
        animateAlpha()
        observeReads { isScrollingOrPanning }
        observeReads { showAlways }

        scrollbarState.scrollTo = scrollState::scrollTo
        scrollbarState.scrollBy = scrollState::scrollBy
    }

    override fun onObservedReadsChanged() {
        animateAlpha()
    }

    private fun animateAlpha() {
        coroutineScope.launch {
            val animationSpec = autoHideAnimationSpec ?: tween(
                delayMillis = if (isScrollingOrPanning) 0 else 1500,
                durationMillis = if (isScrollingOrPanning) 150 else 500,
            )

            val targetAlpha =
                if (showAlways) {
                    1f
                } else if (isScrollingOrPanning) {
                    1f
                } else {
                    0f
                }

            alpha.animateTo(targetAlpha, animationSpec)
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        val showScrollbar = isScrollingOrPanning || alpha.value > 0.0f

        // Draw scrollbar only if currently scrolling or if scroll animation is ongoing.
        if (showScrollbar) {
            val viewPortLength = if (isVertical) size.height else size.width
            val viewPortCrossAxisLength = if (isVertical) size.width else size.height
            val contentLength =
                max(
                    viewPortLength + scrollState.maxValue,
                    0.001f,
                )
            scrollbarState.isVertical = isVertical
            scrollbarState.contentLength = contentLength
            scrollbarState.viewPortLength = viewPortLength

            val layout = ScrollbarLayout(
                layoutDirection = layoutDirection,
                orientation = direction,
                viewPortLength = viewPortLength,
                viewPortCrossAxisLength = viewPortCrossAxisLength,
                contentLength = contentLength,
                contentOffset = scrollState.value,
                scrollbarAlpha = alpha.value,
                density = density,
                fontScale = fontScale,
            )

            with(DefaultScrollbarLayoutScope(this, scrollbarState, density, fontScale)) {
                onMeasureAndDraw(layout)
            }
        }
    }

    override fun SemanticsPropertyReceiver.applySemantics() {
        val isVisible = (
                showAlways ||
                        scrollState.isScrollInProgress ||
                        scrollbarState.isScrollbarDragActive
                ) &&
                scrollbarState.contentLength > scrollbarState.viewPortLength

        testTag = "scrollbar"
        set(ScrollbarSemanticProperties.BarBounds, scrollbarState.barBounds)
        set(ScrollbarSemanticProperties.IndicatorBounds, scrollbarState.indicatorBounds)
        set(ScrollbarSemanticProperties.IndicatorOffset, scrollbarState.indicatorOffset)
        set(ScrollbarSemanticProperties.Direction, direction)
        set(ScrollbarSemanticProperties.ShowAlways, showAlways)
        set(ScrollbarSemanticProperties.IsDragging, scrollbarState.isScrollbarDragActive)
        set(ScrollbarSemanticProperties.IsVisible, isVisible)
        set(ScrollbarSemanticProperties.State, scrollbarState)
    }
}

fun Rect.applyPadding(
    density: Density,
    paddingValues: PaddingValues,
    layoutDirection: LayoutDirection,
): Rect =
    with(density) {
        val rect = this@applyPadding
        Rect(
            left = rect.left + paddingValues.calculateLeftPadding(layoutDirection).toPx(),
            top = rect.top + paddingValues.calculateTopPadding().toPx(),
            right = rect.right - paddingValues.calculateRightPadding(layoutDirection).toPx(),
            bottom = rect.bottom - paddingValues.calculateBottomPadding().toPx(),
        )
    }
class DefaultScrollbarLayoutScope(
    private val drawScope: DrawScope,
    private val scrollbarState: ScrollbarState,
    override val density: Float,
    override val fontScale: Float,
) : ScrollbarLayoutScope {
    override fun drawWithMeasurements(
        measurements: ScrollbarMeasurements,
        drawScrollbarAndIndicator: DrawScope.() -> Unit,
    ): ScrollbarMeasurementResult {
        scrollbarState.indicatorBounds = measurements.indicatorBounds
        scrollbarState.barBounds = measurements.barBounds

        scrollbarState.indicatorOffset =
            if (scrollbarState.isVertical) {
                measurements.indicatorBounds.topLeft.y - measurements.barBounds.topLeft.y
            } else {
                measurements.indicatorBounds.topLeft.x - measurements.barBounds.topLeft.x
            }

        if (scrollbarState.contentLength > scrollbarState.viewPortLength) {
            drawScrollbarAndIndicator(drawScope)
        }

        return ScrollbarMeasurementResult()
    }
}
