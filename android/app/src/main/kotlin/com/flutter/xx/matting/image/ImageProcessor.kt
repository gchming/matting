package com.flutter.xx.matting.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.flutter.xx.matting.common.BaseInstance
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.min

class ImageProcessor private constructor(context: Context) {
    fun imageMatting(
        context: Context,
        originPath: String?,
        maskPath: String?,
        threshold: Int = THRESHOLD,
        filename: String = OUTPUT
    ): String? {
        val target = matting(context, originPath, maskPath, threshold)
        return target?.let {
            val path = saveBitmap(context, it, filename)
            recycle(it)
            path
        }
    }

    private fun matting(
        context: Context,
        originPath: String?,
        maskPath: String?,
        threshold: Int
    ): Bitmap? {
        val origin = decodeFile(originPath) ?: return null
        val mask = decodeFile(maskPath) ?: return null

        val diffWidth = origin.width - mask.width
        val diffHeight = origin.height - mask.height

        val oOffsetWidth = if (diffWidth < 0) 0 else diffWidth / 2
        val oOffsetHeight = if (diffHeight < 0) 0 else diffHeight / 2

        val mOffsetWidth = if (diffWidth < 0) abs(diffWidth) / 2 else 0
        val mOffsetHeight = if (diffHeight < 0) abs(diffHeight) else 0

        val width = min(origin.width, mask.width)
        val height = min(origin.height, mask.height)

        val target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            // 可以前后双向遍历优化
            for (x in 0 until width) {
                val pixel = mask.getPixel(mOffsetWidth + x, mOffsetHeight + y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                if (r <= threshold || g <= threshold || b <= threshold) {
                    continue
                }

                target.setPixel(x, y, origin.getPixel(oOffsetWidth + x, oOffsetHeight + y))
            }
        }

        recycle(origin)
        recycle(mask)

        return target
    }

    private fun saveBitmap(context: Context, bitmap: Bitmap, fileName: String): String? {
        val file = File(context.getDir(DIR, Context.MODE_PRIVATE), fileName)

        val result = saveBitmapToFile(bitmap, file)

        Log.i(TAG, "file = $file, result = $result")

        return if (result) file.path else null
    }

    companion object : BaseInstance<Context, ImageProcessor>() {
        private const val TAG = "ImageProcessor"
        private const val OUTPUT = "output.png"
        private const val DIR = "images"
        private const val THRESHOLD = 32
        private const val QUALITY = 100

        override fun creator(param: Context) = ImageProcessor(param)

        private fun decodeFile(filePath: String?): Bitmap? {
            filePath?.let {
                try {
                    return BitmapFactory.decodeFile(filePath)
                } catch (e: Exception) {
                    Log.e(TAG, "decodeFile failed, error = ${e.message}")
                }

                return null
            }

            Log.w(TAG, "filePath is null")

            return null
        }

        private fun recycle(bitmap: Bitmap?) {
            bitmap?.let {
                try {
                    it.recycle()
                } catch (_: Exception) {
                }
            }
        }

        private fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
            try {
                val output = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, output)
                output.close()

                return true
            } catch (e: Exception) {
                Log.e(TAG, "saveBitmapToFile failed, e = ${e.message}")
            }

            return false
        }
    }
}