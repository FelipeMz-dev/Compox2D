package com.fmz.compox2d.engine.input.keyboard

import com.fmz.compox2d.engine.input.KeyButton
import kotlin.test.Test
import kotlin.test.assertEquals

class KeyboardProcessorTest {
    @Test
    fun dispatchesKeyTransitionsAndHeldEvents() {
        val events = mutableListOf<KeyboardEvent>()
        val manager = KeyboardManager().apply {
            register(object : KeyboardListener {
                override fun onKeyEvent(event: KeyboardEvent) {
                    events += event
                }
            })
        }
        val processor = KeyboardProcessor(manager)

        processor.onKeyDown(KeyButton.ArrowLeft)
        processor.onKeyDown(KeyButton.ArrowLeft)
        processor.update(0.25f)
        processor.onKeyUp(KeyButton.ArrowLeft)
        processor.update(0.25f)

        assertEquals(
            listOf(
                KeyboardEvent.KeyDown(KeyButton.ArrowLeft),
                KeyboardEvent.KeyHeld(KeyButton.ArrowLeft, 0.25f),
                KeyboardEvent.KeyUp(KeyButton.ArrowLeft)
            ),
            events
        )
    }
}
