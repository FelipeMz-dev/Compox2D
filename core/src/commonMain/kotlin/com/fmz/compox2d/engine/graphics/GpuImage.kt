package com.fmz.compox2d.engine.graphics

/**
 * Platform-independent image metadata consumed by render backends.
 */
interface GpuImage {
    val width: Int
    val height: Int
}
