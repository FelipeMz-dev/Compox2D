package com.fmz.compox2d.render

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.fmz.compox2d.compose.ComposeGpuImage
import com.fmz.compox2d.engine.graphics.GpuImage
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.ImageLoader

class AndroidImage(private val imageBitmap: ImageBitmap) : ComposeGpuImage {
    override val width: Int = imageBitmap.width
    override val height: Int = imageBitmap.height

    override fun toImageBitmap(): ImageBitmap = imageBitmap
}

actual class ImageLoaderImpl(private val resources: android.content.res.Resources) : ImageLoader {
    actual override fun loadRes(
        resId: Int,
        hasAlpha: Boolean,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): GpuImage {
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inPreferredConfig = if (hasAlpha) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val source = BitmapFactory.decodeResource(resources, resId, options)
        val bitmap = when {
            srcOffset != null && srcSize != null -> Bitmap.createBitmap(
                source,
                srcOffset.x.toInt(),
                srcOffset.y.toInt(),
                srcSize.x.toInt().coerceAtMost(source.width - srcOffset.x.toInt()),
                srcSize.y.toInt().coerceAtMost(source.height - srcOffset.y.toInt())
            )
            srcOffset != null -> Bitmap.createBitmap(
                source,
                srcOffset.x.toInt(),
                srcOffset.y.toInt(),
                source.width - srcOffset.x.toInt(),
                source.height - srcOffset.y.toInt()
            )
            srcSize != null -> Bitmap.createBitmap(
                source,
                0,
                0,
                srcSize.x.toInt(),
                srcSize.y.toInt()
            )
            else -> source
        }
        return AndroidImage(bitmap.asImageBitmap())
    }

    actual override fun loadPath(path: String): com.fmz.compox2d.engine.graphics.GpuImage {
        val bitmap = BitmapFactory.decodeFile(path)
        return AndroidImage(bitmap.asImageBitmap())
    }
}