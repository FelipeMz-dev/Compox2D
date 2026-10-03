package com.fmz.compox2d.engine.render

import com.fmz.compox2d.engine.graphics.GpuImage
import com.fmz.compox2d.engine.math.Vec2

interface ImageLoader {
    fun loadRes(
        resId: Int,
        hasAlpha: Boolean = true,
        srcOffset: Vec2? = null,
        srcSize: Vec2? = null
    ): GpuImage
    fun loadPath(path: String): GpuImage
}