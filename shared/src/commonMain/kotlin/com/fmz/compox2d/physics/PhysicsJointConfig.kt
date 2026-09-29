package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.length
import com.fmz.compox2d.engine.math.minus

sealed class PhysicsJointConfig {
    abstract val bodyA: RigidBody
    abstract val bodyB: RigidBody
    abstract val collisionAllowed: Boolean

    val length: Float
        get() = (bodyA.transformState.position - bodyB.transformState.position).length()

    val frequencyHz: Float
        get() = 0f

    val dampingRatio: Float
        get() = 0f

    val enableLimit: Boolean
        get() = false

    val lowerAngle: Float
        get() = 0f

    val upperAngle: Float
        get() = 0f

    val enableMotor: Boolean
        get() = false

    val motorSpeed: Float
        get() = 0f

    val maxMotorTorque: Float
        get() = 0f

    data class Distance(
        override val bodyA: RigidBody,
        override val bodyB: RigidBody,
        val anchorA: Vec2 = bodyA.transformState.position,
        val anchorB: Vec2 = bodyB.transformState.position,
        override val collisionAllowed: Boolean = false,
    ) : PhysicsJointConfig()

    data class Revolute(
        override val bodyA: RigidBody,
        override val bodyB: RigidBody,
        val anchor: Vec2 = bodyA.transformState.position,
        override val collisionAllowed: Boolean = false,
    ) : PhysicsJointConfig()

    data class Weld(
        override val bodyA: RigidBody,
        override val bodyB: RigidBody,
        val anchor: Vec2 = bodyA.transformState.position,
        override val collisionAllowed: Boolean = false,
    ) : PhysicsJointConfig()
}
