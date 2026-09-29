package com.fmz.compox2d.engine.graphics

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Interfaz agnóstica de plataforma para representar una imagen.
 * Las implementaciones específicas de plataforma (Android, Desktop, etc.)
 * proporcionarán la funcionalidad concreta.
 */
interface GpuImage {
    val width: Int
    val height: Int
    fun toImageBitmap(): ImageBitmap
}
