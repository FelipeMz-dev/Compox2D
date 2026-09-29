package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.TransformState

/**
 * Lightweight trigger configuration backed by a [Shape].
 *
 * Trigger colliders are intended for gameplay overlap notifications without the
 * extra SAT/mask checks performed by the legacy collision system.
 */
data class TriggerConfig(
    val shape: Shape,
    val state: TransformState = TransformState(),
    val layer: Int = CollisionLayers.Default,
    val mask: Int = CollisionLayers.All,
    val physicState: PhysicState = PhysicState(),
)
