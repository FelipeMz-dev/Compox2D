package com.fmz.compox2d.engine.time

class GameLoop(private val scene: SceneLooper) {

    private val time = GameTime()

    fun onFrame(frameTimeNanos: Long) {
        time.fixedUpdate(frameTimeNanos)
        val deltaTime = time.deltaTime
        scene.fixedUpdate(deltaTime)
        scene.update(deltaTime)
    }

    fun alpha(): Float = time.alpha
}