package com.fmz.compox2d.physics

data class CollisionEvent(
    val self: PhysicsContactSource,
    val other: PhysicsContactSource,
    val phase: CollisionPhase,
    val details: CollisionDetails? = null,
)