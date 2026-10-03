package com.fmz.compox2d.engine.input.keyboard

import com.fmz.compox2d.engine.input.KeyButton

sealed interface KeyboardEvent {

    val key: KeyButton

    data class KeyDown(override val key: KeyButton) :
        KeyboardEvent

    data class KeyUp(override val key: KeyButton) :
        KeyboardEvent

    data class KeyHeld(override val key: KeyButton, val dt: Float) :
        KeyboardEvent
}