package com.fmz.compox2d.samples.breackout_sensor

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.graphics.GpuColor
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.dot
import com.fmz.compox2d.engine.math.length
import com.fmz.compox2d.engine.math.normalized
import com.fmz.compox2d.engine.math.reflect
import com.fmz.compox2d.engine.math.times
import com.fmz.compox2d.engine.render.RenderStyle
import com.fmz.compox2d.engine.render.Renderer
import com.fmz.compox2d.physics.CollisionBodyType
import com.fmz.compox2d.physics.CollisionEvent
import com.fmz.compox2d.physics.CollisionListener
import com.fmz.compox2d.physics.CollisionPhase
import com.fmz.compox2d.physics.PhysicsMaterial
import com.fmz.compox2d.physics.RigidBody
import com.fmz.compox2d.physics.Shape
import kotlin.math.abs

class Ball(private var startPosition: Vec2) : GameObject(), CollisionListener {
    private val radius = 20f
    private lateinit var body: RigidBody
    private var savedVelocity: Vec2 = Vec2.Zero

    override fun onEnterScene() {
        body = createRigidBody(
            shape = Shape.CircleShape(radius),
            type = CollisionBodyType.Dynamic,
            material = PhysicsMaterial(density = 1f, friction = 0f, restitution = 1.5f),
            state = TransformState(startPosition)
        )
    }

    override fun onCollision(event: CollisionEvent) {
        when (event.phase) {
            CollisionPhase.Enter -> {
                val other = event.other
                val details = event.details

                if (other.owner is Paddle) return
                (other.owner as? Brick)?.takeDamage()

                body.updatePhysic { state ->
                    var velocity = state.linearVelocity
                    val normal = details?.normal ?: Vec2.Zero

                    if (normal != Vec2.Zero && velocity.dot(normal) > 0) {
                        velocity = velocity.reflect(normal)
                    }

                    val speed = 500f
                    var newVelocity = if (velocity.length() > 0) velocity.normalized() * speed else velocity

                    val minX = 150f
                    if (abs(newVelocity.x) < minX) {
                        val sign = if (newVelocity.x >= 0) 1f else -1f
                        newVelocity = Vec2(minX * sign, newVelocity.y).normalized() * speed
                    }

                    state.copy(linearVelocity = newVelocity)
                }
            }

            CollisionPhase.Exit -> {
                (event.self as? RigidBody)?.apply {
                    val velocity = physicState.linearVelocity
                    if (velocity.y in -100f..100f) {
                        updatePhysic { state ->
                            val speed = state.linearVelocity.length()
                            val signY = if (state.linearVelocity.y >= 0) 1f else -1f
                            val newVelocity = Vec2(state.linearVelocity.x, 150f * signY).normalized() * speed
                            state.copy(linearVelocity = newVelocity)
                        }
                    }
                }
            }

            else -> Unit
        }
    }

    override fun fixedUpdate(dt: Float) {
        updatePosition { body.transformState.position }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawCircle(
            radius = radius,
            color = GpuColor.Black,
            state = state,
            style = RenderStyle.Fill
        )
    }

    fun moveStartPosition(position: Vec2){
        startPosition = position
        returnPosition()
    }

    fun returnPosition() {
        body.updateTransform {
            it.copy(
                position = startPosition,
                angle = 0f
            )
        }
    }

    fun start(velocity: Vec2) {
        body.updatePhysic { it.copy(linearVelocity = velocity) }
    }

    fun stop() {
        savedVelocity = body.physicState.linearVelocity
        body.updatePhysic { it.copy(linearVelocity = Vec2.Zero) }
    }

    fun resume() {
        body.updatePhysic { it.copy(linearVelocity = savedVelocity) }
    }
}