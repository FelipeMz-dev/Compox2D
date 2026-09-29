package com.fmz.compox2d.render

import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.ImageLoader

actual class ImageLoaderImpl : ImageLoader {
    actual override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): com.fmz.compox2d.engine.graphics.GpuImage {
        TODO("Not yet implemented")
    }

    actual override fun loadPath(path: String): com.fmz.compox2d.engine.graphics.GpuImage {
        TODO("Not yet implemented")
    }
}