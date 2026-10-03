package com.fmz.compox2d.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.fmz.compox2d.engine.audio.AudioListener
import com.fmz.compox2d.engine.audio.AudioManager
import com.fmz.compox2d.engine.audio.AudioPlayer
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.core.AudioId
import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.math.length
import com.fmz.compox2d.engine.math.minus

actual class AudioManagerImpl(context: Context) : AudioManager, AudioSystem, AudioPlayer {
    private var listeners = mutableListOf<AudioListener>()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(16)
        .build()

    private val sounds = mutableMapOf<AudioId, Int>()
    private val musics = mutableMapOf<AudioId, Int>()
    private val musicPlayers = mutableMapOf<AudioId, MediaPlayer>()

    private val appContext = context.applicationContext

    actual override fun loadSound(id: AudioId, resourceId: Any) {
        if (resourceId !is Int) return
        val soundId = soundPool.load(appContext, resourceId, 1)
        sounds[id] = soundId
    }

    actual override fun loadMusic(id: AudioId, resourceId: Any) {
        if (resourceId !is Int) return
        musics[id] = resourceId
    }

    actual override fun registerListener(listener: GameObject) {
        (listener as? AudioListener)?.let { listeners += it }
    }

    actual override fun unregisterListener(listener: GameObject) {
        (listener as? AudioListener)?.let { listeners -= it }
    }

    actual override fun playSound(
        id: AudioId,
        volume: Float,
        rate: Float,
        loop: Boolean
    ) {
        val soundId = sounds[id] ?: return

        soundPool.play(
            soundId,
            volume,
            volume,
            1,
            if (loop) -1 else 0,
            rate
        )
    }

    actual override fun playSoundAt(
        id: AudioId,
        position: Vec2,
        maxDistance: Float,
        volume: Float,
        loop: Boolean
    ) {
        listeners.ifEmpty { return }
        val soundId = sounds[id] ?: return
        val dist = listeners.minOf { (position - it.onRequireListenPosition()).length() }
        val volume = (volume - (dist / maxDistance)).coerceIn(0f, 1f)

        soundPool.play(
            soundId,
            volume,
            volume,
            1,
            if (loop) -1 else 0,
            1f
        )
    }

    actual override fun stopSound(id: AudioId) {
        val soundId = sounds[id] ?: return
        soundPool.stop(soundId)
    }

    actual override fun playMusic(
        id: AudioId,
        volume: Float,
        loop: Boolean
    ) {
        musicPlayers[id]?.run { return }

        val resId = musics[id] ?: return
        val player = MediaPlayer.create(appContext, resId)

        player.isLooping = loop
        player.setVolume(volume, volume)
        player.start()

        musicPlayers[id] = player
    }

    actual override fun stopMusic(id: AudioId) {
        val player = musicPlayers[id] ?: return
        player.stop()
        player.release()
        musicPlayers.remove(id)
    }

    actual override fun pauseMusic(id: AudioId) {
        val player = musicPlayers[id] ?: return
        player.pause()
    }

    actual override fun resumeMusic(id: AudioId) {
        val player = musicPlayers[id] ?: return
        player.start()
    }

    actual override fun setMusicVolume(id: AudioId, volume: Float) {
        val player = musicPlayers[id] ?: return
        player.setVolume(volume, volume)
    }

    actual override fun setMusicLooping(id: AudioId, looping: Boolean) {
        val player = musicPlayers[id] ?: return
        player.isLooping = looping
    }

    actual override fun isMusicPlaying(id: AudioId): Boolean {
        val player = musicPlayers[id] ?: return false
        return player.isPlaying
    }

    actual override fun stopMusics() {
        musicPlayers.values.forEach {
            it.stop()
            it.release()
        }
        musicPlayers.clear()
    }
}