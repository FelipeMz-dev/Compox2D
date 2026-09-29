package com.fmz.compox2d.engine.core

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.RenderPivot

data class TransformState(
    var position: Vec2 = Vec2(0f, 0f),
    var angle: Float = 0f,
    var scale: Vec2 = Vec2(1f, 1f),
    var pivot: RenderPivot = RenderPivot.Center,
    var flipX: Boolean = false,
    var flipY: Boolean = false
)