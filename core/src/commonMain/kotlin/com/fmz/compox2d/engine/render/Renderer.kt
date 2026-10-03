package com.fmz.compox2d.engine.render

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.assets.Sprite
import com.fmz.compox2d.engine.core.SpriteId
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.graphics.GpuColor

/**
 * Interfaz agnóstica de plataforma para renderizado.
 * No depende de Compose ni de Android, permitiendo múltiples implementaciones.
 */
interface Renderer {

    fun flush()

    fun clear(color: GpuColor)

    fun drawPoints(
        points: List<Vec2>,
        state: TransformState,
        strokeWidth: Float = 1f,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawRect(
        size: Vec2,
        state: TransformState,
        style: RenderStyle = RenderStyle.Fill,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawCircle(
        radius: Float,
        state: TransformState,
        deep: Int = 0,
        style: RenderStyle = RenderStyle.Fill,
        color: GpuColor = GpuColor.Gray
    )

    fun drawOval(
        size: Vec2,
        state: TransformState,
        style: RenderStyle = RenderStyle.Fill,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Vec2,
        deep: Int = 0
    )

    fun drawLine(
        start: Vec2,
        end: Vec2,
        strokeWidth: Float,
        deep: Int = 0,
        color: GpuColor
    )

    fun drawPolygon(
        points: List<Vec2>,
        state: TransformState,
        style: RenderStyle = RenderStyle.Fill,
        deep: Int = 0,
        color: GpuColor = GpuColor.Gray
    )

    fun drawSprite(
        spriteId: SpriteId,
        state: TransformState,
        frame: Int = 0,
        deep: Int = 0,
        color: GpuColor = GpuColor.White,
        blendMode: RenderBlendMode = RenderBlendMode.Modulate
    )

    fun Sprite.draw(
        deep: Int = 0,
        color: GpuColor = GpuColor.White,
        blendMode: RenderBlendMode = RenderBlendMode.Modulate
    )

    fun drawText(
        text: String,
        state: TransformState,
        overflow: RenderTextOverflow = RenderTextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        size: Vec2 = Vec2.Zero,
        style: RenderTextStyle = RenderTextStyle.default(),
        deep: Int = 0
    )

    fun drawBackground(
        sprite: SpriteId,
        frame: Int = 0,
        state: TransformState,
        contentScale: RenderContentScale = RenderContentScale.Fit,
    )

    fun drawForeground(
        sprite: SpriteId,
        frame: Int = 0,
        state: TransformState,
        contentScale: RenderContentScale = RenderContentScale.Fit,
    )

    fun drawInfiniteImage(
        spriteId: SpriteId,
        parallaxFactor: Float = 1f,
        contentScale: RenderContentScale = RenderContentScale.Fit,
        deep: Int = RenderDepth.BACKGROUND
    )

    fun drawAxis(
        position: Vec2,
        deep: Int = 0
    )
}