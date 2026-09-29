package com.fmz.compox2d.sample

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.graphics.GpuColor
import com.fmz.compox2d.engine.input.KeyButton
import com.fmz.compox2d.engine.input.keyboard.KeyboardEvent
import com.fmz.compox2d.engine.input.keyboard.KeyboardListener
import com.fmz.compox2d.engine.input.sensor.SensorEvent
import com.fmz.compox2d.engine.input.sensor.SensorListener
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.Renderer
import com.fmz.compox2d.physics.CollisionBodyType
import com.fmz.compox2d.physics.PhysicsMaterial
import com.fmz.compox2d.physics.RigidBody
import com.fmz.compox2d.physics.Shape

class Paddle(val scene: SceneEventListener, val startPosition: Vec2)
    : GameObject(), SensorListener, KeyboardListener {
    val size = Vec2(120f, 20f)
    val half = size.x / 2
    private val speed = 300f
    private lateinit var body: RigidBody

    override fun onEnterScene() {
        body = createRigidBody(
            shape = Shape.BoxShape(size),
            type = CollisionBodyType.Kinematic,
            material = PhysicsMaterial(density = 1f, friction = 0f, restitution = 1f),
            state = TransformState(startPosition)
        )
    }

    override fun onSensorEvent(event: SensorEvent) {
        when (event) {
            is SensorEvent.AccelerometerEvent -> {
                if (!scene.isPlaying()) return
                val direction = (-event.value.x * speed)
                val newAngle = (6.3f * event.value.x)
                body.updateTransform {
                    val currentX = it.position.x
                    val delta = direction * 0.016f
                    val newX = (currentX + delta).coerceIn(half, viewport().size.x - half)
                    it.copy(position = it.position.copy(x = newX), angle = newAngle)
                }
            }

            else -> Unit
        }
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        if (event is KeyboardEvent.KeyHeld && scene.isPlaying()) {
            when (event.key) {
                KeyButton.ArrowLeft -> {
                    body.updateTransform {
                        it.copy(position = it.position.copy(x = it.position.x - speed * event.dt))
                    }
                }
                KeyButton.ArrowRight -> {
                    body.updateTransform {
                        it.copy(position = it.position.copy(x = it.position.x + speed * event.dt))
                    }
                }
                else -> Unit
            }
        }
    }

    override fun fixedUpdate(dt: Float) {
        updatePosition { body.transformState.position }
        updateAngle { body.transformState.angle }
    }

    fun returnPosition() {
        body.updateTransform { it.copy(position = startPosition, angle = 0f) }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawRect(size = size, color = GpuColor.Blue, state = state)
    }
}