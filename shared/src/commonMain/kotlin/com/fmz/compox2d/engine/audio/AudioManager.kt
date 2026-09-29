package com.fmz.compox2d.engine.audio

import com.fmz.compox2d.engine.core.GameObject

interface AudioManager {
    fun registerListener(listener: GameObject)
    fun unregisterListener(listener: GameObject)
}