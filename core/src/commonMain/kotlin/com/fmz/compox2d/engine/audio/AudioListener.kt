package com.fmz.compox2d.engine.audio

import com.fmz.compox2d.engine.math.Vec2

interface AudioListener {
    fun onRequireListenPosition(): Vec2
}