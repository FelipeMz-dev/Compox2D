package com.fmz.compox2d.render

import androidx.compose.ui.graphics.ImageBitmap
import com.fmz.compox2d.engine.graphics.GpuImage
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.ImageLoader

class JsImage(private val imageBitmap: ImageBitmap) : GpuImage {
    override val width: Int = imageBitmap.width
    override val height: Int = imageBitmap.height
    override fun toImageBitmap(): ImageBitmap = imageBitmap
}

actual class ImageLoaderImpl : ImageLoader {
    actual override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): GpuImage {
        return JsImage(ImageBitmap(1, 1))
    }

    actual override fun loadPath(path: String): GpuImage {
        return JsImage(ImageBitmap(1, 1))
    }
}
