package com.kono.flutter_auto_pip

import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.*
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.Exception

/** FlutterAutoPipPlugin */
class FlutterAutoPipPlugin: FlutterPlugin, MethodCallHandler, StreamHandler, ActivityAware, PluginRegistry.UserLeaveHintListener, OnPictureInPictureModeListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var event : EventChannel
  private var eventSink: EventChannel.EventSink? = null
  private var enterPipOnUserLeaveHint = false
  private var activityPluginBinding: ActivityPluginBinding? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_auto_pip")
    channel.setMethodCallHandler(this)
    event = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_auto_pip/event")
    event.setStreamHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    event.setStreamHandler(null)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "autoPipModeEnable") {
      enterPipOnUserLeaveHint = true
    } else if (call.method == "autoPipModeDisable") {
      enterPipOnUserLeaveHint = false
    } else if (call.method == "enterPipMode") {
      try {
        result.success(enterPipMode())
      } catch (e: PIPException) {
        result.error("PIP_EXCEPTION", e.message, null)
      }
    } else if (call.method == "isPipSupported") {
      result.success(supportsPipMode())
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
      try {
        enterPipMode()
      } catch (e: Exception) {
        //
      }
    }
  }

  private fun supportsPipMode(): Boolean {
    return Build.VERSION.SDK_INT >= 26
  }

  private fun canEnterPiPMode(): Boolean {
    if (!supportsPipMode()) return false

    val context: Context? = activityPluginBinding?.activity?.applicationContext
    if (context != null) {
      val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
      return if (Build.VERSION.SDK_INT < 29) {
        appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
      } else {
        appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), context.packageName) == AppOpsManager.MODE_ALLOWED
      }
    }

    return false
  }

  private fun enterPipMode(): Boolean {
    if (activityPluginBinding != null) {
      if (!supportsPipMode()) {
        throw PIPException("PIP mode not supported.");
      }
      if (!canEnterPiPMode()) {
        throw PIPException("Couldn't enter PIP mode. Check permission.")
      }
      return activityPluginBinding!!.activity.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }
    return false
  }

  private fun setActivityBinding(binding: ActivityPluginBinding) {
    activityPluginBinding = binding
    activityPluginBinding?.addOnUserLeaveHintListener(this)
    if (activityPluginBinding?.activity is FlutterAutoPipActivity) {
      (activityPluginBinding?.activity as FlutterAutoPipActivity).onPictureInPictureModeListener = this
    }
  }

  private fun unSetActivityBinding() {
    activityPluginBinding?.removeOnUserLeaveHintListener(this)
    if (activityPluginBinding?.activity is FlutterAutoPipActivity) {
      (activityPluginBinding?.activity as FlutterAutoPipActivity).onPictureInPictureModeListener = null
    }
    activityPluginBinding = null
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    eventSink = events
  }

  override fun onCancel(arguments: Any?) {
    eventSink = null
  }

  override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
    eventSink?.success(isInPictureInPictureMode)
  }
}

class PIPException(message: String) : Exception(message)
