package com.fmz.compox2d.render

import com.fmz.compox2d.engine.graphics.GpuImage
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.ImageLoader

expect class ImageLoaderImpl : ImageLoader{
    override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): GpuImage

    override fun loadPath(path: String): GpuImage

}