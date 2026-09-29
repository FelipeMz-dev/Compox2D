package com.fmz.compox2d.engine.core

import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioListener
import com.fmz.compox2d.engine.audio.AudioManager
import com.fmz.compox2d.engine.audio.AudioPlayer
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.input.GameInput
import com.fmz.compox2d.engine.input.keyboard.KeyboardProcessor
import com.fmz.compox2d.engine.input.touch.TouchProcessor
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.div
import com.fmz.compox2d.engine.math.minus
import com.fmz.compox2d.engine.math.plus
import com.fmz.compox2d.engine.math.rotate
import com.fmz.compox2d.engine.math.rotateAround
import com.fmz.compox2d.engine.math.times
import com.fmz.compox2d.physics.PhysicsManager
import com.fmz.compox2d.engine.render.RenderDepth
import com.fmz.compox2d.engine.render.Renderer
import com.fmz.compox2d.engine.render.Viewport
import com.fmz.compox2d.engine.time.SceneLooper

abstract class GameScene(
    private val physicsManager: PhysicsManager = PhysicsManager(),
) : WorldContext, SceneLooper {

    private val entities = SceneEntityManager()
    private val lifecycleDispatcher = SceneLifecycleDispatcher()
    private val fixedStepDispatcher = SceneFixedStepDispatcher()

    private lateinit var spriteManager: SpriteManager
    private lateinit var audioSystem: AudioSystem
    private lateinit var gameInput: GameInput

    internal var camera2D: Camera2D = Camera2D()
    private lateinit var viewport: Viewport

    override fun viewport() = viewport
    override fun physicsManager() = physicsManager
    override fun audioPlayer() = audioSystem as? AudioPlayer
    override fun camera2D() = camera2D

    override fun spriteSize(id: SpriteId) = spriteManager.getSize(id)

    override fun spawnGameObject(gameObject: GameObject) {
        entities.enqueueAdd(gameObject)
    }

    override fun removeGameObject(gameObject: GameObject) {
        entities.enqueueRemove(gameObject)
    }

    fun removeGameObjectWhere(predicate: (GameObject) -> Boolean) {
        entities.enqueueRemoveWhere(predicate)
    }

    fun attach(dependencies: SceneDependencies) {
        attachSpriteManager(dependencies.spriteManager)
        attachAudioManager(dependencies.audioManager)
        attachInput(dependencies.gameInput)
    }

    fun attachSpriteManager(spriteManager: SpriteManager) {
        this.spriteManager = spriteManager
    }

    fun attachAudioManager(audioSystem: AudioSystem) {
        this.audioSystem = audioSystem
        lifecycleDispatcher.register(
            key = AudioListener::class,
            onAdded = (this.audioSystem as AudioManager)::registerListener,
            onRemoved = (this.audioSystem as AudioManager)::unregisterListener
        )
    }

    fun attachInput(gameInput: GameInput) {
        this.gameInput = gameInput
        gameInput.registerDispatcher(lifecycleDispatcher)
        gameInput.registerStepDispatcher(fixedStepDispatcher)
    }

    fun updateViewport(viewport: Viewport) {
        this.viewport = viewport
        camera2D.viewportSize = viewport.size
    }

    override fun calculateFromViewport(position: Vec2): Vec2 {
        return position / viewport.scale
    }

    override fun screenToWorld(position: Vec2): Vec2 {
        val scaled = (position - camera2D.zoom.from) / camera2D.zoom.value + camera2D.zoom.from
        val rotated = (scaled - camera2D.rotation.point)
            .rotate(-camera2D.rotation.angle) + camera2D.rotation.point
        return rotated - camera2D.position
    }

    override fun worldToScreen(position: Vec2): Vec2 {
        val rotated = (position + camera2D.position)
            .rotateAround(camera2D.rotation.point, camera2D.rotation.angle)
        val scaled = (rotated - camera2D.zoom.from) * camera2D.zoom.value + camera2D.zoom.from
        return scaled
    }

    final override fun update(deltaTime: Float) {
        onUpdate(deltaTime)
        entities.forEach { it.onUpdate(deltaTime) }
    }

    final override fun fixedUpdate(deltaTime: Float) {
        syncGameObjects()
        // Dispatch input and other fixed-step processors before physics update so
        // changes (e.g. updateTransform from input) are applied to the physics world
        // in the same simulation step.
        fixedStepDispatcher.dispatch(deltaTime)
        physicsManager.update(deltaTime)
        onFixedUpdate(deltaTime)
        entities.forEach { it.onFixedUpdate(deltaTime) }
    }

    internal fun render(
        renderer: Renderer,
        alpha: Float
    ) {
        renderer.onRender()
        entities.forEach {
            it.apply { renderer.render(alpha) }
            //renderer.debug(it)
        }
        renderer.flush()
    }

    internal fun startScene() {
        onStart()
    }

    open fun onStart() = Unit

    open fun onStop() = Unit

    open fun onUpdate(dt: Float) = Unit

    open fun onFixedUpdate(dt: Float) = Unit

    open fun Renderer.onRender() = Unit

    private fun syncGameObjects() {
        entities.sync(
            onRemoved = { gameObject ->
                gameObject.onRemovedFromScene()
                lifecycleDispatcher.notifyRemoved(gameObject)
                physicsManager.removeBody(gameObject)
            },
            onAdded = { gameObject ->
                gameObject.onAddedToScene(this)
                lifecycleDispatcher.notifyAdded(gameObject)
            }
        )
    }

    private fun Renderer.debug(instance: GameObject) {
        drawAxis(
            position = instance.currentState().position,
            deep = RenderDepth.DEBUG
        )
    }
}