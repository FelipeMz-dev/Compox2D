package com.fmz.compox2d.engine.graphics

/**
 * Representación agnóstica de plataforma para desplazamientos 2D.
 */
data class GpuOffset(
    val x: Float,
    val y: Float
) {
    companion object {
        val Zero = _root_ide_package_.com.fmz.compox2d.engine.graphics.GpuOffset(0f, 0f)
    }
}

