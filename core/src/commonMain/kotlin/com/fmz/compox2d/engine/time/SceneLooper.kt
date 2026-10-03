package com.fmz.compox2d.engine.time

interface SceneLooper {
    fun update(deltaTime: Float)
    fun fixedUpdate(deltaTime: Float)
}