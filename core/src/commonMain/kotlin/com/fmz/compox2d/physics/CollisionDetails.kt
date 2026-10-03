package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.math.Vec2
import kotlin.math.sqrt

data class CollisionDetails(
    val point: Vec2? = null,
    val normal: Vec2 = Vec2.Zero,
    val depth: Float = 0f,
    val relativeVelocity: Vec2 = Vec2.Zero,
    val relativeSpeed: Float = 0f,
    val normalSpeed: Float = 0f,
    val normalImpulse: Float = 0f,
    val tangentImpulse: Float = 0f,
    val estimatedForce: Float = 0f
) {
    val impulse: Float
        get() = sqrt(normalImpulse * normalImpulse + tangentImpulse * tangentImpulse)
}
