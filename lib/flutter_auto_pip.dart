
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAutoPip {
  static const MethodChannel _channel =
      const MethodChannel('flutter_auto_pip');

  static const EventChannel _event = const EventChannel('flutter_auto_pip/event');

  static void autoPipModeEnable() async {
    await _channel.invokeMethod('autoPipModeEnable');
  }

  static void autoPipModeDisable() async {
    await _channel.invokeMethod('autoPipModeDisable');
  }

  static void enterPipMode() async {
    await _channel.invokeMethod('enterPipMode');
  }

  static Stream<bool> get onPipModeChanged => _event
      .receiveBroadcastStream()
      .map((event) => event as bool);
}
