package com.fmz.compox2d.engine.input.touch

import com.fmz.compox2d.engine.math.Vec2

class TouchProcessor(
    val touchManager: TouchManager
) {
    private var startPos = Vec2.Zero
    private var currentPos: Vec2? = null

    fun onDown(pos: Vec2) {
        startPos = pos
        currentPos = pos
        touchManager.dispatch(TouchEvent.PressEvent(pos))
    }

    fun onMove(pos: Vec2) {
        currentPos = pos
        touchManager.dispatch(TouchEvent.DragEvent(startPos, pos))
    }

    fun onUp(pos: Vec2) {
        currentPos = null
        println("onTapEvent")
        touchManager.dispatch(TouchEvent.TapEvent(pos))
    }

    fun onStopDrag() {
        currentPos = null
        touchManager.dispatch(TouchEvent.StopDragEvent)
    }

    fun update(dt: Float) {
        currentPos?.also {
            touchManager.dispatch(TouchEvent.HeldPressedEvent(it, dt))
        }
    }
}