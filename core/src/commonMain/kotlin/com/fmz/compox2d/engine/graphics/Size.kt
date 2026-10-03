package com.fmz.compox2d.engine.graphics

import kotlin.isNaN

/**
 * Representación agnóstica de plataforma para dimensiones.
 */
data class GpuSize(
    val width: Float,
    val height: Float
) {
    companion object {
        val Zero = _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuSize(0f, 0f)
        val Unspecified =
            _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuSize(Float.NaN, Float.NaN)
        
        fun square(side: Float) =
            _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuSize(side, side)
    }

    val isSpecified: Boolean
        get() = !width.isNaN() && !height.isNaN()

    val isUnspecified: Boolean
        get() = width.isNaN() || height.isNaN()
}

