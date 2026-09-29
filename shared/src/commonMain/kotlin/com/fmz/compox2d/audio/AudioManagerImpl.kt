package com.fmz.compox2d.audio

import com.fmz.compox2d.engine.audio.AudioManager
import com.fmz.compox2d.engine.audio.AudioPlayer
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.core.AudioId
import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.math.Vec2

expect class AudioManagerImpl : AudioManager, AudioSystem, AudioPlayer {
    override fun registerListener(listener: GameObject)
    override fun unregisterListener(listener: GameObject)
    override fun loadSound(id: AudioId, resourceId: Any)
    override fun loadMusic(id: AudioId, resourceId: Any)
    override fun playSound(
        id: AudioId,
        volume: Float,
        rate: Float,
        loop: Boolean
    )

    override fun playSoundAt(
        id: AudioId,
        position: Vec2,
        maxDistance: Float,
        volume: Float,
        loop: Boolean
    )

    override fun stopSound(id: AudioId)
    override fun playMusic(
        id: AudioId,
        volume: Float,
        loop: Boolean
    )

    override fun stopMusic(id: AudioId)
    override fun pauseMusic(id: AudioId)
    override fun resumeMusic(id: AudioId)
    override fun setMusicVolume(id: AudioId, volume: Float)
    override fun setMusicLooping(id: AudioId, looping: Boolean)
    override fun isMusicPlaying(id: AudioId): Boolean
    override fun stopMusics()
}