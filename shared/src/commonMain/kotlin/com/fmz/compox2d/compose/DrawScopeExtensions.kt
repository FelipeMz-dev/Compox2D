package com.fmz.compox2d.compose

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntSize
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.minus
import com.fmz.compox2d.engine.math.resolve
import com.fmz.compox2d.engine.math.times

internal fun DrawScope.drawSpriteInternal(
    image: com.fmz.compox2d.engine.graphics.GpuImage,
    state: TransformState = TransformState(),
    colorFilter: ColorFilter? = null
) {
    val imageBitmap = image.toImageBitmap()
    val size = Vec2(image.width, image.height)
    val scaledSize = (size * state.scale)
    val pivotOffset = state.pivot.resolve(scaledSize, state.flipX, state.flipY)

    val flipScaleX = if (state.flipX) -1f else 1f
    val flipScaleY = if (state.flipY) -1f else 1f

    val topLeft = (state.position - pivotOffset)

    val dstSize = IntSize(width = scaledSize.x.toInt(), height = scaledSize.y.toInt())

    withTransform(
        transformBlock = {
            translate(left = topLeft.x, top = topLeft.y)
            rotate(state.angle, pivotOffset.toOffsetCompose())
            scale(flipScaleX, flipScaleY, pivotOffset.toOffsetCompose())
        }
    ) {
        drawImage(
            image = imageBitmap,
            dstSize = dstSize,
            colorFilter = colorFilter
        )
    }
}