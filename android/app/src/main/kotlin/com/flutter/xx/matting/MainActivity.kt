package com.flutter.xx.matting

import android.os.Bundle
import com.flutter.xx.matting.image.ImageProcessorHandler
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        val messenger = flutterEngine.dartExecutor.binaryMessenger
        val channel = MethodChannel(messenger, ImageProcessorHandler.CHANNEL)

        channel.setMethodCallHandler(ImageProcessorHandler(this))
    }
}
