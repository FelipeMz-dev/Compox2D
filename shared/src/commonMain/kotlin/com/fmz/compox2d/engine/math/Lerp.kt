package com.fmz.compox2d.engine.math

import com.fmz.compox2d.engine.core.TransformState
import kotlin.math.PI

fun TransformState.lerp(
    to: TransformState,
    alpha: Float
) = TransformState(
    position = position.lerp(to.position, alpha),
    angle = lerpAngle(angle, to.angle, alpha),
    scale = scale.lerp(to.scale, alpha),
)

private fun lerpAngle(a: Float, b: Float, t: Float): Float {
    val diff = ((b - a + PI).mod(2 * PI) - PI).toFloat()
    return a + diff * t
}