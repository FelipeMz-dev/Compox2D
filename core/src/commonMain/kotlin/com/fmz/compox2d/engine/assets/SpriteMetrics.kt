package com.fmz.compox2d.engine.assets

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.RenderPivot

data class SpriteMetrics(
    val position: Vec2 = Vec2.Zero,
    val scale: Vec2 = Vec2(1f, 1f),
    val angle: Float = 0f,
    val pivot: RenderPivot = RenderPivot.TopLeft,
    val flipX: Boolean = false,
    val flipY: Boolean = false,
)