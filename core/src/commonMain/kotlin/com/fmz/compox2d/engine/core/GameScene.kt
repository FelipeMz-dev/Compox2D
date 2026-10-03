package com.fmz.compox2d.engine.core

import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioListener
import com.fmz.compox2d.engine.audio.AudioManager
import com.fmz.compox2d.engine.audio.AudioPlayer
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.input.GameInput
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

    private var spriteManager: SpriteManager? = null
    private var audioSystem: AudioSystem? = null
    private var gameInput: GameInput? = null

    internal var camera2D: Camera2D = Camera2D()
    private var _viewport: Viewport? = null
    private var isStarted = false

    val isAttached: Boolean
        get() = spriteManager != null && audioSystem != null && gameInput != null

    val isStartedScene: Boolean
        get() = isStarted

    override fun viewport(): Viewport = _viewport ?: Viewport(Vec2.Zero, Vec2(1f, 1f))
    override fun physicsManager() = physicsManager
    override fun audioPlayer() = audioSystem as? AudioPlayer
    override fun camera2D() = camera2D

    override fun spriteSize(id: SpriteId): Vec2 = spriteManager?.getSize(id) ?: Vec2.Zero

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
        tryStartScene()
    }

    fun attachSpriteManager(spriteManager: SpriteManager) {
        this.spriteManager = spriteManager
        tryStartScene()
    }

    fun attachAudioManager(audioSystem: AudioSystem) {
        this.audioSystem = audioSystem
        lifecycleDispatcher.register(
            key = AudioListener::class,
            onAdded = (this.audioSystem as AudioManager)::registerListener,
            onRemoved = (this.audioSystem as AudioManager)::unregisterListener
        )
        tryStartScene()
    }

    fun attachInput(gameInput: GameInput) {
        this.gameInput = gameInput
        gameInput.registerDispatcher(lifecycleDispatcher)
        gameInput.registerStepDispatcher(fixedStepDispatcher)
        tryStartScene()
    }

    fun updateViewport(viewport: Viewport) {
        val oldViewport = _viewport
        this._viewport = viewport
        camera2D.viewportSize = viewport.size

        if (!isStarted) {
            tryStartScene()
        } else if (oldViewport != null && oldViewport != viewport) {
            onViewportChanged(viewport)
        }
    }

    fun startScene() {
        tryStartScene()
    }

    private fun tryStartScene() {
        if (!isStarted && isAttached && _viewport != null) {
            isStarted = true
            onStart()
        }
    }

    override fun calculateFromViewport(position: Vec2): Vec2 {
        val currentViewport = viewport()
        if (currentViewport.scale.x == 0f || currentViewport.scale.y == 0f) return position
        return position / currentViewport.scale
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
        if (!isStarted) return
        onUpdate(deltaTime)
        entities.forEach { it.onUpdate(deltaTime) }
    }

    final override fun fixedUpdate(deltaTime: Float) {
        if (!isStarted) return
        syncGameObjects()
        // Dispatch input and other fixed-step processors before physics update so
        // changes (e.g. updateTransform from input) are applied to the physics world
        // in the same simulation step.
        fixedStepDispatcher.dispatch(deltaTime)
        physicsManager.update(deltaTime)
        onFixedUpdate(deltaTime)
        entities.forEach { it.onFixedUpdate(deltaTime) }
    }

    fun render(
        renderer: Renderer,
        alpha: Float
    ) {
        if (!isStarted) return
        renderer.onRender()
        entities.forEach {
            it.apply { renderer.render(alpha) }
            //renderer.debug(it)
        }
        renderer.flush()
    }

    open fun onStart() = Unit

    open fun onStop() = Unit

    open fun onViewportChanged(viewport: Viewport) = Unit

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
