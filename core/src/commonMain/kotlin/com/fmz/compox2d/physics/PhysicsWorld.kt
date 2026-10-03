package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.math.Vec2

data class PhysicsWorld(
    val gravity: Vec2 = Vec2(0f, 980f),
    val maxLinearSpeed: Float = 5000f,
    val manager: PhysicsManager = PhysicsManager(gravity)
)
