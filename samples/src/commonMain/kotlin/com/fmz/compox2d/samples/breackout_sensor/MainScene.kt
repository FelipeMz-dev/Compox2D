package com.fmz.compox2d.samples.breackout_sensor

import com.fmz.compox2d.engine.core.GameScene
import com.fmz.compox2d.engine.core.TransformState
import com.fmz.compox2d.engine.graphics.GpuColor
import com.fmz.compox2d.engine.graphics.GradientDirection
import com.fmz.compox2d.engine.input.KeyButton
import com.fmz.compox2d.engine.input.keyboard.KeyboardEvent
import com.fmz.compox2d.engine.input.touch.TouchEvent
import com.fmz.compox2d.engine.math.*
import com.fmz.compox2d.engine.render.RenderFontSize
import com.fmz.compox2d.engine.render.RenderStyle
import com.fmz.compox2d.engine.render.RenderTextOverflow
import com.fmz.compox2d.engine.render.RenderTextStyle
import com.fmz.compox2d.engine.render.Renderer
import com.fmz.compox2d.engine.render.Viewport
import com.fmz.compox2d.physics.PhysicsManager

enum class GameState {
    START, PLAYING, PAUSED, GAME_OVER, WON
}

class MainScene : GameScene(PhysicsManager(gravity = Vec2.Zero)), SceneEventListener {
    private var state = GameState.START
    private var score = 0
    private var hits = 0
    private var dragEvent: TouchEvent.DragEvent? = null
    private val bricks = mutableListOf<Brick>()
    private lateinit var ball: Ball
    private lateinit var paddle: Paddle
    val worldWidth: Float
        get() = viewport().size.x
    val worldHeight: Float
        get() = viewport().size.y
    val startBallPosition: Vec2
        get() = Vec2(worldWidth / 2f, worldHeight - worldHeight * 0.2)
    val startPaddlePosition: Vec2
        get() = Vec2(worldWidth / 2f, worldHeight - worldHeight * 0.1)

    override fun onViewportChanged(viewport: Viewport) {
        super.onViewportChanged(viewport)
        ball.moveStartPosition(startBallPosition)
        paddle.moveStartPosition(startPaddlePosition)
        restartWalls()
        setupBricks()
    }

    override fun onStart() {
        ball = Ball(startBallPosition)
        paddle = Paddle(this, startPaddlePosition)
        spawnGameObject(ball)
        spawnGameObject(paddle)
        spawnGameObject(InputHandler(this))
        setupWalls()
        setupBricks()
    }

    private fun setupWalls() {
        spawnGameObject(Wall(Vec2(worldWidth / 2f, 10f), Vec2(worldWidth, 20f)))
        spawnGameObject(Wall(Vec2(10f, worldHeight / 2f), Vec2(20f, worldHeight)))
        spawnGameObject(Wall(Vec2(worldWidth - 10f, worldHeight / 2f), Vec2(20f, worldHeight)))
        spawnGameObject(
            DeathZone(
                this,
                Vec2(worldWidth / 2f, worldHeight - 5f),
                Vec2(worldWidth, 10f)
            )
        )
    }

    private fun restartWalls() {
        removeGameObjectWhere { it is Wall }
        removeGameObjectWhere { it is DeathZone }
        setupWalls()
    }

    private fun setupBricks() {
        removeGameObjectWhere { it is Brick }
        if (bricks.isNotEmpty()) {
            bricks.clear()
        }

        // Bricks
        val columns = 8
        val rows = 5
        val brickWidth = (worldWidth - 40f) / columns
        val brickHeight = 30f
        val startX = 20f + brickWidth / 2f
        val startY = 100f

        val colors = mapOf(
            0 to GpuColor.gradient(
                GpuColor.Red,
                GpuColor(0.95f, 0.45f, 0.2f),
                GpuColor.Yellow,
                direction = GradientDirection.Vertical
            ),
            1 to GpuColor.gradient(
                GpuColor(0.95f, 0.8f, 0.2f),
                GpuColor.Yellow,
                GpuColor(0.9f, 0.55f, 0.15f),
                direction = GradientDirection.Vertical
            ),
            2 to GpuColor.gradient(
                GpuColor(0.2f, 0.85f, 0.4f),
                GpuColor.Green,
                GpuColor(0.1f, 0.6f, 0.9f),
                direction = GradientDirection.Vertical
            ),
            3 to GpuColor.gradient(
                GpuColor(0.5f, 0.5f, 0.0f),
                GpuColor.Magenta,
                GpuColor(0.2f, 0.6f, 0.5f),
                direction = GradientDirection.Vertical
            )
        )

        for (r in 0 until rows) {

            val durability = when (r) {
                0 -> 3
                1, 2 -> 2
                else -> 1
            }
            for (c in 0 until columns) {
                val brickPos = Vec2(startX + c * brickWidth, startY + r * (brickHeight + 5f))
                val brick =
                    Brick(this, brickPos, Vec2(brickWidth - 4f, brickHeight), colors, durability)
                bricks.add(brick)
                spawnGameObject(brick)
            }
        }
    }

    override fun onTapEvent() {
        val isPauseEvent = togglePause()
        if (!isPauseEvent) startGame()
    }

    override fun onKeyEvent(event: KeyboardEvent) {
        if (event is KeyboardEvent.KeyDown) {
            when (event.key) {
                KeyButton.P -> {
                    togglePause()
                }

                KeyButton.R -> {
                    state = GameState.START
                    paddle.returnPosition()
                    ball.returnPosition()
                    ball.stop()
                    setupBricks()
                    score = 0
                    hits = 0
                }

                KeyButton.Enter -> {
                    if (!isPlaying()) startGame()
                }

                else -> Unit
            }
        }
    }

    override fun onDragEvent(event: TouchEvent.DragEvent?) {
        dragEvent = event
    }

    private fun startGame() {
        state = GameState.PLAYING
        ball.start(Vec2(100f, -600f))
    }

    private fun togglePause(): Boolean {
        state = when (state) {
            GameState.PLAYING -> {
                ball.stop()
                GameState.PAUSED
            }

            GameState.PAUSED -> {
                ball.resume()
                GameState.PLAYING
            }

            else -> return false
        }
        return true
    }

    override fun onBrickDestroyed(brick: Brick) {
        score += 100
        bricks.remove(brick)
        if (bricks.isEmpty()) {
            state = GameState.WON
            paddle.returnPosition()
            ball.apply {
                returnPosition()
                stop()
            }
        }
    }

    override fun onBallLost() {
        state = GameState.GAME_OVER
        paddle.returnPosition()
        ball.apply {
            returnPosition()
            stop()
        }
    }

    override fun isPlaying() = state == GameState.PLAYING

    override fun Renderer.onRender() {

        val style = RenderTextStyle(
            fontSize = 70f,
            fontSizeUnit = RenderFontSize.Px,
            color = GpuColor.Black
        )

        dragEvent?.also {
            drawCircle(
                radius = 20f,
                state = TransformState(it.start),
                style = RenderStyle.Stroke(2f),
            )
            drawCircle(
                radius = 50f,
                state = TransformState(it.current),
                style = RenderStyle.Stroke(2f),
            )
            drawLine(
                start = it.start,
                end = it.current,
                strokeWidth = 2f,
                color = GpuColor.Black
            )
        }

        drawText(
            "Score: $score",
            TransformState(Vec2(180, 54)),
            RenderTextOverflow.Visible,
            true,
            1,
            Vec2.Zero,
            style,
            100
        )

        val center = viewport().size.times(0.5f)

        if (state != GameState.PLAYING) {
            drawText(
                state.name,
                TransformState(center),
                RenderTextOverflow.Visible,
                true,
                1,
                Vec2.Zero,
                style.copy(fontSize = 150f),
                101
            )
        }
    }
}

interface SceneEventListener {
    fun onKeyEvent(event: KeyboardEvent)
    fun onTapEvent()
    fun onDragEvent(event: TouchEvent.DragEvent?)
    fun onBrickDestroyed(brick: Brick)
    fun onBallLost()
    fun isPlaying(): Boolean
}