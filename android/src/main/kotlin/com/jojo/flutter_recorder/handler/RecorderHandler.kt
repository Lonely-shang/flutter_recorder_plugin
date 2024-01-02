package com.jojo.flutter_recorder.handler

import android.content.Intent
import com.jojo.flutter_recorder.manager.RecorderManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/**
 * @calssName RecorderHandler
 * @author Miliky
 * @date 2023/12/29 15:54
 * @description [爱已随风起，风止意难平。]
 */
class RecorderHandler : MethodChannel.MethodCallHandler{

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method){
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            else -> {
                result.notImplemented()
            }
        }
    }

}