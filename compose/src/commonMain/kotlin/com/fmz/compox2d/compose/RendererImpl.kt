package com.fmz.compox2d.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.fmz.compox2d.engine.assets.Sprite
import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.core.Camera2D
import com.fmz.compox2d.engine.core.SpriteId
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.div
import com.fmz.compox2d.engine.math.minus
import com.fmz.compox2d.engine.math.plus
import com.fmz.compox2d.engine.math.rem
import com.fmz.compox2d.engine.math.resolve
import com.fmz.compox2d.engine.math.times
import com.fmz.compox2d.engine.render.RenderBlendMode
import com.fmz.compox2d.engine.render.RenderCommand
import com.fmz.compox2d.engine.render.RenderContentScale
import com.fmz.compox2d.engine.render.RenderDepth
import com.fmz.compox2d.engine.render.RenderStyle
import com.fmz.compox2d.engine.render.RenderTextOverflow
import com.fmz.compox2d.engine.render.RenderTextStyle
import com.fmz.compox2d.engine.render.Renderer

internal class RendererImpl(
    private val spriteManager: SpriteManager,
    private val textMeasurer: TextMeasurer,
    private var camera: Camera2D,
    private val density: Density
) : Renderer {

    private var drawScope: DrawScope? = null
    private val commands = mutableListOf<RenderCommand>()

    internal fun bind(drawScope: DrawScope) {
        this.drawScope = drawScope
    }

    override fun flush() {
        val activeDrawScope = drawScope ?: return
        val cameraScale = camera.zoom.value
        val cameraScaleFrom = camera.zoom.from.toOffsetCompose()
        val cameraRotationFrom = camera.rotation.point.toOffsetCompose()

        commands.sortBy { it.order }
        with(activeDrawScope) {
            withTransform(
                transformBlock = {
                    scale(cameraScale, cameraScale, cameraScaleFrom)
                    rotate(camera.rotation.angle, cameraRotationFrom)
                    translate(camera.position.x, camera.position.y)
                }
            ) { commands.forEach { it.drawer.apply { draw() } } }
        }
        commands.clear()
    }

    override fun clear(color: com.fmz.compox2d.engine.graphics.GpuColor) {
        commands += RenderCommand(RenderDepth.BACKGROUND) {
            val size = this.size
            drawRect(
                topLeft = Offset(-size.width, -size.height),
                color = color.toCompose()
            )
        }
    }

    private fun DrawScope.drawGird() {
        val activeDrawScope = drawScope ?: return
        for (i in 1 until activeDrawScope.size.width.toInt()) {
            val pos = i * 32f
            drawLine(
                start = Offset(pos, 0f),
                end = Offset(pos, activeDrawScope.size.height),
                strokeWidth = 1f,
                color = Color.DarkGray
            )
        }
        for (i in 1 until activeDrawScope.size.height.toInt()) {
            val pos = i * 32f
            drawLine(
                start = Offset(0f, pos),
                end = Offset(activeDrawScope.size.width, pos),
                strokeWidth = 1f,
                color = Color.DarkGray
            )
        }
    }

    override fun drawPoints(
        points: List<Vec2>,
        state: TransformState,
        strokeWidth: Float,
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor
    ) {
        val sizeX = points.minOf { it.x } - points.minOf { it.x }
        val sizeY = points.minOf { it.y } - points.maxOf { it.y }
        val size = Vec2(sizeX, sizeY)
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        commands += RenderCommand(RenderDepth.DEBUG) {
            withTransform(
                {
                    scale(state.scale.x, state.scale.y, pivotOffset.toOffsetCompose())
                    rotate(state.angle, pivotOffset.toOffsetCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                this.drawPoints(
                    points = points.map { it.toOffsetCompose() },
                    pointMode = PointMode.Points,
                    color = color.toCompose(),
                    strokeWidth = strokeWidth
                )
            }
        }
    }

    override fun drawLine(
        start: Vec2,
        end: Vec2,
        strokeWidth: Float,
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor
    ) {
        commands += RenderCommand(RenderDepth.DEBUG) {
            drawLine(
                color = color.toCompose(),
                start = start.toOffsetCompose(),
                end = end.toOffsetCompose(),
                strokeWidth = strokeWidth
            )
        }
    }

    override fun drawRect(
        size: Vec2,
        state: TransformState,
        style: RenderStyle,
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor
    ) {
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        val scaledSize = (size * state.scale).toSizeCompose()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(state.angle, state.position.toOffsetCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                if (color.isGradient) {
                    drawRect(
                        brush = color.toComposeBrush(scaledSize),
                        size = scaledSize,
                        style = style.toCompose()
                    )
                } else {
                    drawRect(
                        color = color.toCompose(),
                        size = scaledSize,
                        style = style.toCompose()
                    )
                }
            }
        }
    }

    override fun drawCircle(
        radius: Float,
        state: TransformState,
        deep: Int,
        style: RenderStyle,
        color: com.fmz.compox2d.engine.graphics.GpuColor
    ) {
        val size = Vec2(radius, radius) * 2f
        drawOval(
            size = size,
            state = state,
            style = style,
            deep = deep,
            color = color
        )
    }

    override fun drawOval(
        size: Vec2,
        state: TransformState,
        style: RenderStyle,
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor
    ) {
        val pivotOffset = state.pivot.resolve(size)
        val topLeft = (state.position - pivotOffset)
        val scaledSize = (size * state.scale).toSizeCompose()
        commands += RenderCommand(deep) {
            withTransform(
                {
                    rotate(state.angle, state.position.toOffsetCompose())
                    translate(topLeft.x, topLeft.y)
                }
            ) {
                if (color.isGradient) {
                    drawOval(
                        brush = color.toComposeBrush(scaledSize),
                        size = scaledSize,
                        style = style.toCompose()
                    )
                } else {
                    drawOval(
                        color = color.toCompose(),
                        size = scaledSize,
                        style = style.toCompose()
                    )
                }
            }
        }
    }

    override fun drawPolygon(
        points: List<Vec2>,
        state: TransformState,
        style: RenderStyle,
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor
    ) {
        val path = Path().apply {
            points.forEachIndexed { index, item ->
                if (index == 0) moveTo(item.x, item.y)
                else lineTo(item.x, item.y)
            }
            close()
        }
        val width = points.maxOf { it.x } - points.minOf { it.x }
        val height = points.maxOf { it.y } - points.minOf { it.y }
        val pivotOffset = state.pivot.resolve(Vec2(width, height))
        commands += RenderCommand(deep) {
            withTransform(
                {
                    translate(state.position.x, state.position.y)
                    translate(-pivotOffset.x, -pivotOffset.y)
                    scale(state.scale.x, state.scale.y, pivotOffset.toOffsetCompose())
                    rotate(state.angle, pivotOffset.toOffsetCompose())
                }
            ) {
                if (color.isGradient) {
                    drawPath(
                        path = path,
                        brush = color.toComposeBrush(Size(width, height)),
                        style = style.toCompose()
                    )
                } else {
                    drawPath(
                        path = path,
                        color = color.toCompose(),
                        style = style.toCompose()
                    )
                }
            }
        }
    }

    override fun drawImage(
        spriteId: SpriteId,
        position: Vec2,
        size: Vec2,
        deep: Int
    ) {
        val spriteSize = size / spriteManager.getSize(spriteId)
        drawSprite(
            spriteId = spriteId,
            frame = 0,
            state = TransformState(
                position = position,
                angle = 0f,
                scale = spriteSize
            ),
            deep = deep
        )
    }

    override fun drawSprite(
        spriteId: SpriteId,
        state: TransformState,
        frame: Int,
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor,
        blendMode: RenderBlendMode
    ) {
        val img = spriteManager.get(spriteId).frameAt(frame)

        img?.also {
            commands += RenderCommand(deep) {
                drawSpriteInternal(
                    image = img,
                    state = state,
                    colorFilter = ColorFilter.tint(color.toCompose(), blendMode.toCompose())
                )
            }
        }
    }

    override fun Sprite.draw(
        deep: Int,
        color: com.fmz.compox2d.engine.graphics.GpuColor,
        blendMode: RenderBlendMode
    ) {
        drawSprite(
            spriteId = spriteId,
            frame = currentFrame,
            state = state,
            deep = deep,
            color = color,
            blendMode = blendMode
        )
    }

    override fun drawText(
        text: String,
        state: TransformState,
        overflow: RenderTextOverflow,
        softWrap: Boolean,
        maxLines: Int,
        size: Vec2,
        style: RenderTextStyle,
        deep: Int
    ) {
        val safeMaxLines = if (maxLines <= 0) Int.MAX_VALUE else maxLines

        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = style.toCompose(density, size),
            overflow = overflow.toCompose(),
            softWrap = softWrap,
            maxLines = safeMaxLines,
            constraints = Constraints(
                maxWidth = if (size.x > 0f) size.x.toInt() else Int.MAX_VALUE,
                maxHeight = if (size.y > 0f) size.y.toInt() else Int.MAX_VALUE
            )
        )

        val finalSize = Vec2(
            x = if (size.x > 0f) size.x else textLayoutResult.size.width.toFloat(),
            y = if (size.y > 0f) size.y else textLayoutResult.size.height.toFloat()
        ) * state.scale

        val pivotOffset = state.pivot.resolve(finalSize)

        commands += RenderCommand(deep) {
            withTransform(
                transformBlock = {
                    translate(state.position.x, state.position.y)
                    rotate(state.angle, pivotOffset.toOffsetCompose())
                    translate(-pivotOffset.x, -pivotOffset.y)
                }
            ) {
                drawText(
                    textLayoutResult,
                    color = style.color.toCompose()
                )
            }
        }
    }

    override fun drawAxis(
        position: Vec2,
        deep: Int
    ) {
        commands += RenderCommand(deep) {
            drawLine(
                start = position.copy(x = position.x - 50f).toOffsetCompose(),
                end = position.copy(x = position.x + 50f).toOffsetCompose(),
                strokeWidth = 2f,
                color = Color.DarkGray
            )
            drawLine(
                start = position.copy(y = position.y - 50f).toOffsetCompose(),
                end = position.copy(y = position.y + 50f).toOffsetCompose(),
                strokeWidth = 2f,
                color = Color.DarkGray
            )
        }
    }

    override fun drawBackground(
        sprite: SpriteId,
        frame: Int,
        state: TransformState,
        contentScale: RenderContentScale
    ) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = contentScale.toCompose().computeScaleFactor(
            srcSize = spriteBaseSize.toSizeCompose(),
            dstSize = camera.viewportSize.toSizeCompose()
        ).let { Vec2(it.scaleX, it.scaleY) }

        drawSprite(
            spriteId = sprite,
            frame = frame,
            state = state.copy(scale = state.scale * contentScale),
            deep = RenderDepth.BACKGROUND
        )
    }

    override fun drawForeground(
        sprite: SpriteId,
        frame: Int,
        state: TransformState,
        contentScale: RenderContentScale
    ) {
        val spriteBaseSize = spriteManager.getSize(sprite)

        val contentScale = contentScale.toCompose().computeScaleFactor(
            srcSize = spriteBaseSize.toSizeCompose(),
            dstSize = camera.viewportSize.toSizeCompose()
        ).let { Vec2(it.scaleX, it.scaleY) }

        drawSprite(
            spriteId = sprite,
            frame = frame,
            state = state.copy(scale = state.scale * contentScale),
            deep = RenderDepth.FOREGROUND
        )
    }

    override fun drawInfiniteImage(
        spriteId: SpriteId,
        parallaxFactor: Float,
        contentScale: RenderContentScale,
        deep: Int
    ) {
        val spriteSize = spriteManager.getSize(spriteId)
        if (spriteSize == Vec2.Zero) return

        val contentScale = contentScale.toCompose().computeScaleFactor(
            srcSize = spriteSize.toSizeCompose(),
            dstSize = camera.viewportSize.toSizeCompose()
        ).let { Vec2(it.scaleX, it.scaleY) }

        val scaledSize = spriteSize * contentScale
        val movement = camera.position * (0f - parallaxFactor)
        val offset = ((movement % scaledSize) + scaledSize) % scaledSize
        val startDraw = Vec2.Zero - camera.position - offset

        val tilesX = (camera.viewportSize.x / scaledSize.x).toInt() + 2
        val tilesY = (camera.viewportSize.y / scaledSize.y).toInt() + 2

        for (i in -1..tilesX) {
            for (j in -1..tilesY) {
                val posX = startDraw.x + (i * scaledSize.x)
                val posY = startDraw.y + (j * scaledSize.y)

                drawSprite(
                    spriteId = spriteId,
                    frame = 1,
                    state = TransformState(
                        position = Vec2(posX, posY),
                        angle = 0f,
                        scale = contentScale
                    ),
                    deep = deep
                )
            }
        }
    }
}
