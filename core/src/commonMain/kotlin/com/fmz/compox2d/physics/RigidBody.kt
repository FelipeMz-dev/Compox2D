package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.core.TransformState
import org.jbox2d.dynamics.Body
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

class RigidBody internal constructor(
    override val owner: GameObject,
    override val body: Body,
    manager: PhysicsManager,
    layer: Int,
    mask: Int,
) : PhysicsContactSource(), RigidBodyBehavior by BodyBehaviorImpl(body, manager) {

    companion object {
        @OptIn(ExperimentalAtomicApi::class)
        private val idCounter = AtomicInt(0)
    }

    @OptIn(ExperimentalAtomicApi::class)
    val id: Int = idCounter.incrementAndFetch()

    var layer: Int = layer
        private set

    var mask: Int = mask
        private set

    val transformState: TransformState
        get() = transformState()

    val physicState: PhysicState
        get() = physicState()

    val material: PhysicsMaterial?
        get() = material()

    val shape: Shape?
        get() = shape()

    val type: CollisionBodyType
        get() = type()
}
