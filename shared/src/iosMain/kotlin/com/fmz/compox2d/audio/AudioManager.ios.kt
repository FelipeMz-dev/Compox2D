package com.fmz.compox2d.audio

import com.fmz.compox2d.engine.audio.AudioManager
import com.fmz.compox2d.engine.audio.AudioPlayer
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.core.AudioId
import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.math.Vec2

actual class AudioManagerImpl : AudioManager,
    AudioSystem, AudioPlayer {
    actual override fun registerListener(listener: GameObject) {
    }

    actual override fun unregisterListener(listener: GameObject) {
    }

    actual override fun loadSound(id: AudioId, resourceId: Any) {
    }

    actual override fun loadMusic(id: AudioId, resourceId: Any) {
    }

    actual override fun playSound(
        id: AudioId,
        volume: Float,
        rate: Float,
        loop: Boolean
    ) {
    }

    actual override fun playSoundAt(
        id: AudioId,
        position: Vec2,
        maxDistance: Float,
        volume: Float,
        loop: Boolean
    ) {
    }

    actual override fun stopSound(id: AudioId) {
    }

    actual override fun playMusic(
        id: AudioId,
        volume: Float,
        loop: Boolean
    ) {
    }

    actual override fun stopMusic(id: AudioId) {
    }

    actual override fun pauseMusic(id: AudioId) {
    }

    actual override fun resumeMusic(id: AudioId) {
    }

    actual override fun setMusicVolume(id: AudioId, volume: Float) {
    }

    actual override fun setMusicLooping(
        id: AudioId,
        looping: Boolean
    ) {
    }

    actual override fun isMusicPlaying(id: AudioId): Boolean {
        return false
    }

    actual override fun stopMusics() {
    }
}
