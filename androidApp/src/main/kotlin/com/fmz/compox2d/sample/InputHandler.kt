package com.fmz.compox2d.sample

import com.fmz.compox2d.engine.core.GameObject
import com.fmz.compox2d.engine.input.keyboard.KeyboardEvent
import com.fmz.compox2d.engine.input.keyboard.KeyboardListener
import com.fmz.compox2d.engine.input.touch.TouchEvent
import com.fmz.compox2d.engine.input.touch.TouchListener

class InputHandler(val scene: SceneEventListener) : GameObject(), KeyboardListener, TouchListener {
    override fun onKeyEvent(event: KeyboardEvent) {
        scene.onKeyEvent(event)
    }

    override fun onTouchEvent(event: TouchEvent) {
        when(event) {
            is TouchEvent.TapEvent -> scene.onTapEvent()
            is TouchEvent.DragEvent -> scene.onDragEvent(event)
            is TouchEvent.StopDragEvent -> scene.onDragEvent(null)
            else -> Unit
        }
    }
}