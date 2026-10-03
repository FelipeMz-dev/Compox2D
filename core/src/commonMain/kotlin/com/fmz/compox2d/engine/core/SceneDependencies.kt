package com.fmz.compox2d.engine.core

import com.fmz.compox2d.engine.assets.SpriteManager
import com.fmz.compox2d.engine.audio.AudioSystem
import com.fmz.compox2d.engine.input.GameInput

class SceneDependencies(
    val spriteManager: SpriteManager,
    val audioManager: AudioSystem,
    val gameInput: GameInput
)
