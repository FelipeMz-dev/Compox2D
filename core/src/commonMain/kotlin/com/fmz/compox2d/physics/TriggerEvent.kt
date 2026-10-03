package com.fmz.compox2d.physics

data class TriggerEvent(
    val self: PhysicsContactSource,
    val other: PhysicsContactSource,
    val phase: CollisionPhase,
)
