package com.fmz.compox2d.samples.breackout_sensor

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
import kotlin.math.abs

class Paddle(
    val scene: SceneEventListener,
    var startPosition: Vec2
) : GameObject(), SensorListener, KeyboardListener {
    val size = Vec2(120f, 20f)
    val half = size.x / 2
    private val speed = 300f
    private lateinit var body: RigidBody

    private var isLeftPressed = false
    private var isRightPressed = false
    private var targetAngle = 0f
    private var currentAngle = 0f
    private val maxTiltAngle = 35f
    private val rotationSpeed = 8f

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
                targetAngle = (6.3f * event.value.x)
                val currentX = body.transformState.position.x
                val delta = direction * 0.016f
                val newX = (currentX + delta).coerceIn(half, viewport().size.x - half)
                body.updateTransform {
                    it.copy(position = it.position.copy(x = newX))
                }
            }

            else -> Unit
        }
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        if (!scene.isPlaying()) return
        when (event) {
            is KeyboardEvent.KeyDown -> {
                when (event.key) {
                    KeyButton.ArrowLeft, KeyButton.A -> isLeftPressed = true
                    KeyButton.ArrowRight, KeyButton.D -> isRightPressed = true
                    else -> Unit
                }
            }
            is KeyboardEvent.KeyUp -> {
                when (event.key) {
                    KeyButton.ArrowLeft, KeyButton.A -> isLeftPressed = false
                    KeyButton.ArrowRight, KeyButton.D -> isRightPressed = false
                    else -> Unit
                }
            }
            is KeyboardEvent.KeyHeld -> {
                when (event.key) {
                    KeyButton.ArrowLeft, KeyButton.A -> isLeftPressed = true
                    KeyButton.ArrowRight, KeyButton.D -> isRightPressed = true
                    else -> Unit
                }
            }
        }
    }

    override fun update(dt: Float) {
        if (!scene.isPlaying()) return

        var moveDir = 0f
        if (isLeftPressed) moveDir -= 1f
        if (isRightPressed) moveDir += 1f

        if (moveDir != 0f) {
            targetAngle = moveDir * maxTiltAngle
        } else if (!isLeftPressed && !isRightPressed) {
            targetAngle = 0f
        }

        val lerpFactor = (rotationSpeed * dt).coerceAtMost(1f)
        currentAngle += (targetAngle - currentAngle) * lerpFactor

        val currentX = body.transformState.position.x
        val newX = if (moveDir != 0f) {
            (currentX + moveDir * speed * dt).coerceIn(half, viewport().size.x - half)
        } else {
            currentX
        }

        if (moveDir != 0f || abs(currentAngle - body.transformState.angle) > 0.001f) {
            body.updateTransform {
                it.copy(position = it.position.copy(x = newX), angle = currentAngle)
            }
        }
    }

    override fun fixedUpdate(dt: Float) {
        updatePosition { body.transformState.position }
        updateAngle { body.transformState.angle }
    }

    fun moveStartPosition(position: Vec2){
        startPosition = position
        returnPosition()
    }

    fun returnPosition() {
        isLeftPressed = false
        isRightPressed = false
        targetAngle = 0f
        currentAngle = 0f
        body.updateTransform { it.copy(position = startPosition, angle = 0f) }
    }

    override fun Renderer.onRender(state: TransformState) {
        drawRect(size = size, color = GpuColor.Blue, state = state)
    }
}