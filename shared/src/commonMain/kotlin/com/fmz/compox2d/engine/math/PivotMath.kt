package com.fmz.compox2d.engine.math

import com.fmz.compox2d.engine.render.RenderPivot

fun RenderPivot.resolve(size: Vec2) = this.resolve(size, flipX = false, flipY = false)

fun RenderPivot.resolve(
    size: Vec2,
    flipX: Boolean,
    flipY: Boolean
): Vec2 {
    val width = size.x
    val height = size.y

    return when (this) {
        RenderPivot.Center -> Vec2(width / 2f, height / 2f)

        RenderPivot.Top -> Vec2(
            x = width / 2f,
            y = if (flipY) height else 0f
        )

        RenderPivot.Bottom -> Vec2(
            x = width / 2f,
            y = if (flipY) 0f else height
        )

        RenderPivot.Left -> Vec2(
            x = if (flipX) width else 0f,
            y = height / 2f
        )

        RenderPivot.Right -> Vec2(
            x = if (flipX) 0f else width,
            y = height / 2f
        )

        RenderPivot.TopLeft -> Vec2(
            x = if (flipX) width else 0f,
            y = if (flipY) height else 0f
        )

        RenderPivot.TopRight -> Vec2(
            x = if (flipX) 0f else width,
            y = if (flipY) height else 0f
        )

        RenderPivot.BottomLeft -> Vec2(
            x = if (flipX) width else 0f,
            y = if (flipY) 0f else height
        )

        RenderPivot.BottomRight -> Vec2(
            x = if (flipX) 0f else width,
            y = if (flipY) 0f else height
        )

        is RenderPivot.Custom -> Vec2(
            x = if (flipX) width - x else x,
            y = if (flipY) height - y else y
        )
    }
}