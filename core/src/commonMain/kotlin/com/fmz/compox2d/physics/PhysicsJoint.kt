package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.math.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.joints.Joint

class PhysicsJoint internal constructor(
    internal val joint: Joint,
    internal val bodyA: Body,
    internal val bodyB: Body,
    private val manager: PhysicsManager,
) {

    val anchorA: Vec2
        get() = manager.toEngine(bodyA.position)

    val anchorB: Vec2
        get() = manager.toEngine(bodyB.position)

    val collisionAllowed: Boolean
        get() = joint.isActive

    fun remove() {
        manager.removeJoint(this)
    }

    internal fun includes(body: Body): Boolean = bodyA == body || bodyB == body
}
