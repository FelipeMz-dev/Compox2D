package com.fmz.compox2d.engine.input

import com.fmz.compox2d.engine.core.SceneFixedStepDispatcher
import com.fmz.compox2d.engine.core.SceneLifecycleDispatcher
import com.fmz.compox2d.engine.input.keyboard.KeyboardListener
import com.fmz.compox2d.engine.input.keyboard.KeyboardProcessor
import com.fmz.compox2d.engine.input.mouse.MouseListener
import com.fmz.compox2d.engine.input.mouse.MouseProcessor
import com.fmz.compox2d.engine.input.sensor.SensorListener
import com.fmz.compox2d.engine.input.touch.TouchListener
import com.fmz.compox2d.engine.input.touch.TouchProcessor
import com.fmz.compox2d.input.sensor.SensorProcessor

class GameInput private constructor(
    val touchProcessor: TouchProcessor?,
    val sensorProcessor: SensorProcessor?,
    val keyboardProcessor: KeyboardProcessor?,
    val mouseProcessor: MouseProcessor?,
) {
    class Builder {
        private var touchProcessor: TouchProcessor? = null
        private var sensorProcessor: SensorProcessor? = null
        private var keyboardProcessor: KeyboardProcessor? = null
        private var mouseProcessor: MouseProcessor? = null

        fun withTouch(processor: TouchProcessor) = apply {
            this.touchProcessor = processor
        }

        fun withSensor(processor: SensorProcessor) = apply {
            this.sensorProcessor = processor
        }

        fun withKeyboard(processor: KeyboardProcessor) = apply {
            this.keyboardProcessor = processor
        }

        fun withMouse(processor: MouseProcessor) = apply {
            this.mouseProcessor = processor
        }

        fun build() = GameInput(
            touchProcessor = touchProcessor,
            sensorProcessor = sensorProcessor,
            keyboardProcessor = keyboardProcessor,
            mouseProcessor = mouseProcessor
        )
    }

    fun registerDispatcher(lifecycleDispatcher: SceneLifecycleDispatcher) {
        touchProcessor?.touchManager?.apply {
            lifecycleDispatcher.register(
                key = TouchListener::class,
                onAdded = { register(it as TouchListener) },
                onRemoved = { unregister(it as TouchListener) }
            )
        }
        sensorProcessor?.sensorManager?.apply {
            lifecycleDispatcher.register(
                key = SensorListener::class,
                onAdded = { register(it as SensorListener) },
                onRemoved = { unregister(it as SensorListener) }
            )
        }
        keyboardProcessor?.keyboardManager?.apply {
            lifecycleDispatcher.register(
                key = KeyboardListener::class,
                onAdded = { register(it as KeyboardListener) },
                onRemoved = { unregister(it as KeyboardListener) }
            )
        }
        mouseProcessor?.mouseManager?.apply {
            lifecycleDispatcher.register(
                key = MouseListener::class,
                onAdded = { register(it as MouseListener) },
                onRemoved = { unregister(it as MouseListener) }
            )
        }
    }

    fun registerStepDispatcher(fixedStepDispatcher: SceneFixedStepDispatcher) {
        keyboardProcessor?.apply {
            fixedStepDispatcher.register(KeyboardProcessor::class) { dt ->
                update(dt)
            }
        }
        touchProcessor?.apply {
            fixedStepDispatcher.register(TouchProcessor::class) { dt ->
                update(dt)
            }
        }
    }
}
