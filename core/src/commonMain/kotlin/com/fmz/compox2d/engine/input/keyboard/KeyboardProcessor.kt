package com.fmz.compox2d.engine.input.keyboard

import com.fmz.compox2d.engine.input.KeyButton

class KeyboardProcessor(
    var keyboardManager: KeyboardManager
) {

    private val pressedKeys = mutableSetOf<KeyButton>()

    fun onKeyDown(key: KeyButton): Boolean {
        if (pressedKeys.add(key)) {
            keyboardManager.dispatchKeyEvent(KeyboardEvent.KeyDown(key))
        }
        return true
    }

    fun onKeyUp(key: KeyButton): Boolean {
        if (pressedKeys.remove(key)) {
            keyboardManager.dispatchKeyEvent(KeyboardEvent.KeyUp(key))
        }
        return true
    }

    fun update(dt: Float) {
        pressedKeys.forEach { key ->
            keyboardManager.dispatchKeyEvent(KeyboardEvent.KeyHeld(key, dt))
        }
    }
}