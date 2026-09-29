package com.fmz.compox2d.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.fmz.compox2d.engine.input.KeyButton
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.RenderBlendMode
import com.fmz.compox2d.engine.render.RenderContentScale
import com.fmz.compox2d.engine.render.RenderFontSize
import com.fmz.compox2d.engine.render.RenderStyle
import com.fmz.compox2d.engine.render.RenderTextOverflow
import com.fmz.compox2d.engine.render.RenderTextStyle
import com.fmz.compox2d.engine.render.VirtualResolution

fun Offset.toVec2() = Vec2(x, y)

fun Size.toVec2() = Vec2(width, height)

fun Vec2.toOffsetCompose() = Offset(x, y)

fun Vec2.toSizeCompose() = Size(x, y)

fun com.fmz.compox2d.engine.graphics.GpuColor.toCompose(): Color = Color(red, green, blue, alpha)

fun com.fmz.compox2d.engine.graphics.GpuColor.toComposeBrush(
    start: Offset = Offset.Zero,
    end: Offset = Offset.Zero
): Brush {
    if (!isGradient) {
        return Brush.linearGradient(
            0f to toCompose(),
            1f to toCompose(),
            start = start,
            end = end
        )
    }

    val orderedStops = gradientStops.sortedBy { it.stop }
    val gradientMap = orderedStops.map { it.stop to it.color.toCompose() }
    return Brush.linearGradient(
        *gradientMap.toTypedArray(),
        start = start,
        end = end
    )
}

fun com.fmz.compox2d.engine.graphics.GpuColor.toComposeBrush(size: Size): Brush {
    val start = if (gradientDirection == com.fmz.compox2d.engine.graphics.GradientDirection.Horizontal) {
        Offset.Zero
    } else {
        Offset.Zero
    }
    val end = if (gradientDirection == com.fmz.compox2d.engine.graphics.GradientDirection.Horizontal) {
        Offset(size.width, 0f)
    } else {
        Offset(0f, size.height)
    }
    return toComposeBrush(start = start, end = end)
}

fun RenderStyle.toCompose(): DrawStyle = when (this) {
    RenderStyle.Fill -> Fill
    is RenderStyle.Stroke -> androidx.compose.ui.graphics.drawscope.Stroke(width)
}

fun VirtualResolution.toSizeCompose() = Size(width, height)

fun RenderBlendMode.toCompose(): BlendMode = when (this) {
    RenderBlendMode.Modulate -> BlendMode.SrcAtop
    RenderBlendMode.Screen -> BlendMode.Screen
    RenderBlendMode.Multiply -> BlendMode.Multiply
    RenderBlendMode.Plus -> BlendMode.Plus
}

fun RenderTextStyle.toCompose(): TextStyle =
    TextStyle(
        color = color.toCompose(),
        fontSize = when (fontSizeUnit) {
            RenderFontSize.Sp -> TextUnit(fontSize, TextUnitType.Sp)
            RenderFontSize.Px -> TextUnit(fontSize, TextUnitType.Unspecified)
            RenderFontSize.Em -> TextUnit(fontSize, TextUnitType.Em)
            RenderFontSize.Auto -> TextUnit(fontSize, TextUnitType.Unspecified)
        }
    )

fun RenderTextStyle.toCompose(density: Density, containerSize: Vec2 = Vec2.Zero): TextStyle {
    val fontSize = when (fontSizeUnit) {
        RenderFontSize.Sp -> TextUnit(this.fontSize, TextUnitType.Sp)
        RenderFontSize.Px -> with(density) {
            TextUnit(this@toCompose.fontSize / this@with.density, TextUnitType.Sp)
        }
        RenderFontSize.Em -> TextUnit(this.fontSize, TextUnitType.Em)
        RenderFontSize.Auto -> {
            if (containerSize.x > 0f && containerSize.y > 0f) {
                with(density) {
                    val maxWidthSp = containerSize.x / 6f
                    val maxHeightSp = containerSize.y
                    val calculatedSize = minOf(maxWidthSp, maxHeightSp)
                    TextUnit(calculatedSize, TextUnitType.Sp)
                }
            } else {
                TextUnit(this.fontSize, TextUnitType.Unspecified)
            }
        }
    }
    return TextStyle(
        color = color.toCompose(),
        fontSize = fontSize
    )
}

fun RenderTextOverflow.toCompose(): TextOverflow = when (this) {
    RenderTextOverflow.Clip -> TextOverflow.Clip
    RenderTextOverflow.Ellipsis -> TextOverflow.Ellipsis
    RenderTextOverflow.Visible -> TextOverflow.Visible
}

fun RenderContentScale.toCompose(): ContentScale = when (this) {
    RenderContentScale.Fit -> ContentScale.Fit
    RenderContentScale.Crop -> ContentScale.Crop
    RenderContentScale.FillBounds -> ContentScale.FillBounds
    RenderContentScale.FillHeight -> ContentScale.FillHeight
    RenderContentScale.FillWidth -> ContentScale.FillWidth
    RenderContentScale.Inside -> ContentScale.Inside
    RenderContentScale.None -> ContentScale.None
}

fun Key.toCommonKey(): KeyButton = when (this) {
    Key.DirectionUp -> KeyButton.ArrowUp
    Key.DirectionDown -> KeyButton.ArrowDown
    Key.DirectionLeft -> KeyButton.ArrowLeft
    Key.DirectionRight -> KeyButton.ArrowRight
    Key.Spacebar -> KeyButton.Space
    Key.Enter -> KeyButton.Enter
    Key.Escape -> KeyButton.Escape
    Key.Backspace -> KeyButton.Backspace
    Key.Tab -> KeyButton.Tab
    Key.ShiftLeft, Key.ShiftRight -> KeyButton.Shift
    Key.CtrlLeft, Key.CtrlRight -> KeyButton.Control
    Key.AltLeft, Key.AltRight -> KeyButton.Alt
    Key.A -> KeyButton.A
    Key.B -> KeyButton.B
    Key.C -> KeyButton.C
    Key.D -> KeyButton.D
    Key.E -> KeyButton.E
    Key.F -> KeyButton.F
    Key.G -> KeyButton.G
    Key.H -> KeyButton.H
    Key.I -> KeyButton.I
    Key.J -> KeyButton.J
    Key.K -> KeyButton.K
    Key.L -> KeyButton.L
    Key.M -> KeyButton.M
    Key.N -> KeyButton.N
    Key.O -> KeyButton.O
    Key.P -> KeyButton.P
    Key.Q -> KeyButton.Q
    Key.R -> KeyButton.R
    Key.S -> KeyButton.S
    Key.T -> KeyButton.T
    Key.U -> KeyButton.U
    Key.V -> KeyButton.V
    Key.W -> KeyButton.W
    Key.X -> KeyButton.X
    Key.Y -> KeyButton.Y
    Key.Z -> KeyButton.Z
    Key.Zero -> KeyButton.Num0
    Key.One -> KeyButton.Num1
    Key.Two -> KeyButton.Num2
    Key.Three -> KeyButton.Num3
    Key.Four -> KeyButton.Num4
    Key.Five -> KeyButton.Num5
    Key.Six -> KeyButton.Num6
    Key.Seven -> KeyButton.Num7
    Key.Eight -> KeyButton.Num8
    Key.Nine -> KeyButton.Num9
    else -> KeyButton.Space
}