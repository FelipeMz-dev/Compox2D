package com.fmz.compox2d.engine.core

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.Viewport
import com.fmz.compox2d.engine.audio.AudioPlayer
import com.fmz.compox2d.physics.PhysicsManager

interface WorldContext {
    fun audioPlayer(): AudioPlayer?
    fun physicsManager(): PhysicsManager
    fun camera2D(): Camera2D?
    fun viewport(): Viewport
    fun spriteSize(id: SpriteId): Vec2
    fun removeGameObject(gameObject: GameObject)
    fun spawnGameObject(gameObject: GameObject)
    fun calculateFromViewport(position: Vec2): Vec2
    fun screenToWorld(position: Vec2): Vec2
    fun worldToScreen(position: Vec2): Vec2
}