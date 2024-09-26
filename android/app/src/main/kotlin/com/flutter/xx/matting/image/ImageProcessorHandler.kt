package com.flutter.xx.matting.image

import android.app.Activity
import android.util.Log
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class ImageProcessorHandler(private val activity: Activity) : MethodCallHandler {
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            METHOD -> {
                imageMatting(call, result)
            }
            else -> {
                result.error("-1", "method not existed", null)
            }
        }
    }

    private fun imageMatting(call: MethodCall, result: MethodChannel.Result) {
        Log.i(TAG, "arguments = ${call.arguments}")

        try {
            val originPath = call.argument<String>(ORIGIN_PATH)
            val maskPath = call.argument<String>(MASK_PATH)
            val output = ImageProcessor.getInstance(activity).imageMatting(activity, originPath, maskPath)
            result.success(output)
            return
        } catch (_: Exception) {
        }

        result.error("-1", "image matting error", null)
    }

    companion object {
        private const val TAG = "ImageProcessorHandler"
        const val CHANNEL = "image_processor_channel"
        private const val METHOD = "imageMatting"
        private const val ORIGIN_PATH = "originPath"
        private const val MASK_PATH = "maskPath"
    }
}