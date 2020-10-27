package com.kono.flutter_auto_pip

import android.app.PictureInPictureParams
import android.os.Build
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterAutoPipPlugin */
class FlutterAutoPipPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.UserLeaveHintListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var enterPipOnUserLeaveHint = false
  private var activityPluginBinding: ActivityPluginBinding? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_auto_pip")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "autoPipModeEnable") {
      enterPipOnUserLeaveHint = true
    } else if (call.method == "autoPipModeDisable") {
      enterPipOnUserLeaveHint = false
    } else if (call.method == "enterPipMode") {
      enterPipMode()
    } else {
      result.notImplemented()
    }
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    setActivityBinding(binding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    unSetActivityBinding()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    setActivityBinding(binding)
  }

  override fun onDetachedFromActivity() {
    unSetActivityBinding()
  }

  override fun onUserLeaveHint() {
    if (enterPipOnUserLeaveHint) {
      enterPipMode()
    }
  }

  private fun enterPipMode() {
    if (activityPluginBinding != null) {
      if (Build.VERSION.SDK_INT > 26) {
        activityPluginBinding!!.activity.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
      } else if (Build.VERSION.SDK_INT > 24) {
        activityPluginBinding!!.activity.enterPictureInPictureMode()
      }
    }
  }

  private fun setActivityBinding(binding: ActivityPluginBinding) {
    activityPluginBinding = binding
    activityPluginBinding?.addOnUserLeaveHintListener(this)
  }

  private fun unSetActivityBinding() {
    activityPluginBinding?.removeOnUserLeaveHintListener(this)
    activityPluginBinding = null
  }
}
