package com.fmz.compox2d.render

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.fmz.compox2d.compose.ComposeGpuImage
import com.fmz.compox2d.engine.graphics.GpuImage
import com.fmz.compox2d.engine.math.Vec2
import com.fmz.compox2d.engine.render.ImageLoader
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class JvmImage(private val imageBitmap: ImageBitmap) : ComposeGpuImage {
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
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("drawable/$resId.png")
            ?: ImageLoaderImpl::class.java.getResourceAsStream("/drawable/$resId.png")
            ?: ImageLoaderImpl::class.java.getResourceAsStream("drawable/$resId.png")
        if (stream != null) {
            val bufferedImage = ImageIO.read(stream)
            if (bufferedImage != null) {
                val cropped = cropBufferedImage(bufferedImage, srcOffset, srcSize)
                return JvmImage(cropped.toComposeImageBitmap())
            }
        }
        val emptyBitmap = ImageBitmap(
            width = srcSize?.x?.toInt()?.coerceAtLeast(1) ?: 1,
            height = srcSize?.y?.toInt()?.coerceAtLeast(1) ?: 1
        )
        return JvmImage(emptyBitmap)
    }

    actual override fun loadPath(path: String): GpuImage {
        val file = File(path)
        if (file.exists()) {
            val bufferedImage = ImageIO.read(file)
            if (bufferedImage != null) {
                return JvmImage(bufferedImage.toComposeImageBitmap())
            }
        }
        return JvmImage(ImageBitmap(1, 1))
    }

    private fun cropBufferedImage(
        source: BufferedImage,
        srcOffset: Vec2?,
        srcSize: Vec2?
    ): BufferedImage {
        val x = srcOffset?.x?.toInt() ?: 0
        val y = srcOffset?.y?.toInt() ?: 0
        val w = srcSize?.x?.toInt() ?: (source.width - x)
        val h = srcSize?.y?.toInt() ?: (source.height - y)
        if (x <= 0 && y <= 0 && w >= source.width && h >= source.height) {
            return source
        }
        val safeX = x.coerceIn(0, (source.width - 1).coerceAtLeast(0))
        val safeY = y.coerceIn(0, (source.height - 1).coerceAtLeast(0))
        val safeW = w.coerceIn(1, (source.width - safeX).coerceAtLeast(1))
        val safeH = h.coerceIn(1, (source.height - safeY).coerceAtLeast(1))
        return source.getSubimage(safeX, safeY, safeW, safeH)
    }
}
