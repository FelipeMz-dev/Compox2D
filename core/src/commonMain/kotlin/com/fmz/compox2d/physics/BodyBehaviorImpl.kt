package com.fmz.compox2d.physics

import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.math.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.forEachFixture

open class BodyBehaviorImpl(
    private val body: Body,
    private val manager: PhysicsManager
) : TriggerBehavior, RigidBodyBehavior {

    override fun transformState(): TransformState {
        return manager.getTransformState(body)
    }

    override fun physicState(): PhysicState {
        return manager.getPhysicState(body)
    }

    override fun material(): PhysicsMaterial? {
        return manager.getMaterial(body)
    }

    override fun shape(): Shape? {
        return manager.getShape(body)
    }

    override fun type(): CollisionBodyType {
        return manager.getType(body)
    }

    override fun applyImpulseTowards(
        target: Vec2,
        force: Float,
        point: Vec2?
    ) {
        manager.applyImpulseTowards(body, target, force, point)
    }

    override fun applyImpulse(
        impulse: Vec2,
        point: Vec2?
    ) {
        manager.applyImpulse(body, impulse, point)
    }

    override fun applyForce(
        force: Vec2,
        point: Vec2?
    ) {
        manager.applyForce(body, force, point)
    }

    override fun applyTorque(torque: Float) {
        manager.applyTorque(body, torque)
    }

    override fun updateMaterial(block: (PhysicsMaterial?) -> PhysicsMaterial) {
        val currentState = material()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyMaterial(body, newState)
    }

    override fun updateType(block: (CollisionBodyType) -> CollisionBodyType) {
        val currentState = type()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyType(body, newState)
    }

    override fun remove() {
        manager.removeBody(body)
    }

    override fun updateShape(block: (Shape?) -> Shape) {
        val currentShape = shape()
        val newShape = block(currentShape)
        if (currentShape == newShape) return
        manager.updateBodyShape(body, transformState(), newShape)
        material()?.let { manager.updateBodyMaterial(body, it) }
    }

    override fun updateTransform(block: (TransformState) -> TransformState) {
        val currentState = transformState()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyTransform(body, newState)
    }

    override fun updatePhysic(block: (PhysicState) -> PhysicState) {
        val currentState = physicState()
        val newState = block(currentState)
        if (currentState == newState) return
        manager.updateBodyPhysicState(body, newState)
    }

    override fun stop() {
        manager.stopBody(body)
    }

    override fun setCollisionFilter(layer: Int, mask: Int) {
        body.forEachFixture { fixture ->
            fixture.filterData.maskBits = mask
            fixture.filterData.categoryBits = layer
        }
    }
}
