package com.fmz.compox2d.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import com.fmz.compox2d.engine.input.GameInput
import com.fmz.compox2d.engine.input.MouseButton
import com.fmz.compox2d.engine.input.keyboard.KeyboardProcessor
import com.fmz.compox2d.engine.input.mouse.MouseProcessor
import com.fmz.compox2d.engine.input.touch.TouchProcessor

fun Modifier.runInputProcessors(gameInput: GameInput, focusRequester: FocusRequester) = this
    .run {
        gameInput.touchProcessor?.let {
            gameTouchInput(it)
        } ?: this
    }
    .run {
        gameInput.keyboardProcessor?.let {
            gameKeyboardInput(it, focusRequester)
        } ?: this
    }
    .run {
        gameInput.mouseProcessor?.let {
            gameMouseInput(it)
        } ?: this
    }

fun Modifier.gameTouchInput(processor: TouchProcessor) = this
    .pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { processor.onMove(it.toVec2()) },
            onDragEnd = { processor.onStopDrag() },
            onDragCancel = { processor.onStopDrag() },
            onDrag = { change, _ ->
                processor.onMove(change.position.toVec2())
            }
        )
    }
    .pointerInput(Unit) {
        detectTapGestures(
            onPress = { processor.onDown(it.toVec2()) },
            onTap = { processor.onUp(it.toVec2()) }
        )
    }

fun Modifier.gameMouseInput(processor: MouseProcessor) = this.pointerInput(Unit) {
    awaitPointerEventScope {

        while (true) {

            val event = awaitPointerEvent()
            val change = event.changes.first()

            if (change.type != PointerType.Mouse) continue

            val scroll = change.scrollDelta
            val pos = change.position

            val button = when {
                event.buttons.isPrimaryPressed -> MouseButton.Left
                event.buttons.isSecondaryPressed -> MouseButton.Right
                event.buttons.isTertiaryPressed -> MouseButton.Middle
                else -> MouseButton.Unknown
            }

            if (event.type == PointerEventType.Scroll) {
                processor.scroll(
                    position = pos.toVec2(),
                    scrollX = scroll.x,
                    scrollY = scroll.y
                )
            } else {
                processor.mouseMove(pos.toVec2())
            }

            if (change.pressed && !change.previousPressed) {
                processor.mouseDown(pos.toVec2(), button)
            }

            if (!change.pressed && change.previousPressed) {
                processor.mouseUp(pos.toVec2())
            }
        }
    }
}

fun Modifier.gameKeyboardInput(processor: KeyboardProcessor, focusRequester: FocusRequester) = this
    .onKeyEvent { processor.onKeyEvent(it) }
    .focusRequester(focusRequester)
    .focusable()
    .onFocusChanged {
        if (!it.isFocused) focusRequester.requestFocus()
    }