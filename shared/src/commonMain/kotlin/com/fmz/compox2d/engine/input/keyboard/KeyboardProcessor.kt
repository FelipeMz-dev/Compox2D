package com.fmz.compox2d.engine.input.keyboard

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import com.fmz.compox2d.compose.toCommonKey
import com.fmz.compox2d.engine.input.KeyButton

class KeyboardProcessor(
    var keyboardManager: KeyboardManager
) {

    private val pressedKeys = mutableSetOf<Key>()

    fun onKeyEvent(event: KeyEvent): Boolean {
        val key = event.key

        when (event.type) {
            KeyEventType.KeyDown -> {
                if (pressedKeys.add(key)) {
                    keyboardManager.dispatchKeyEvent(KeyboardEvent.KeyDown(key.toCommonKey()))
                }
            }

            KeyEventType.KeyUp -> {
                if (pressedKeys.remove(key)) {
                    keyboardManager.dispatchKeyEvent(KeyboardEvent.KeyUp(key.toCommonKey()))
                }
            }
        }
        return true
    }

    fun update(dt: Float) {
        pressedKeys.forEach {
            keyboardManager.dispatchKeyEvent(KeyboardEvent.KeyHeld(it.toCommonKey(), dt))
        }
    }
}