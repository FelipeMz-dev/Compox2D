package com.fmz.compox2d.engine.render

import com.fmz.compox2d.engine.math.Vec2

data class Viewport(
    val size: Vec2,
    val scale: Vec2,
) {
    val aspectRatio: Float
        get() = size.x / size.y
}