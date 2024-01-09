package com.jojo.flutter_recorder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat.startForegroundService
import com.jojo.flutter_recorder.recoder.RecorderService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterRecorderPlugin */
class FlutterRecorderPlugin: FlutterPlugin, MethodCallHandler{
  private lateinit var channel : MethodChannel
  private lateinit var recorderService: RecorderService
  private lateinit var pluginBinding: FlutterPluginBinding
  private lateinit var intent: Intent
  private val connection = object : ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        val binder = service as RecorderService.RecorderBinder
        recorderService = binder.getService()
//        recorderService.startRecorder()
    }

    override fun onServiceDisconnected(arg0: ComponentName) {
    }
  }


  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    pluginBinding = flutterPluginBinding
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_recorder")
    channel.setMethodCallHandler(this)
    intent = Intent(pluginBinding.applicationContext, RecorderService::class.java).also { intent ->
      pluginBinding.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
//    flutterPluginBinding.applicationContext.startService(intent)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "startRecord"){
       recorderService.startRecorder(pluginBinding.applicationContext)
//        startForegroundService(pluginBinding.applicationContext ,intent)

    }
    else if (call.method == "stopRecord") {
      recorderService.stopRecorder()

    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)

    binding.applicationContext.unbindService(connection)
  }

}
