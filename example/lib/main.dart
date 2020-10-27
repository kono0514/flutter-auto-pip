import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_auto_pip/flutter_auto_pip.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            ElevatedButton(
              onPressed: () {
                FlutterAutoPip.autoPipModeEnable();
              },
              child: Text('Enable Auto PIP'),
            ),
            ElevatedButton(
              onPressed: () {
                FlutterAutoPip.autoPipModeDisable();
              },
              child: Text('Disable Auto PIP'),
            ),
            ElevatedButton(
              onPressed: () {
                FlutterAutoPip.enterPipMode();
              },
              child: Text('Enter PIP mode'),
            ),
          ],
        ),
      ),
    );
  }
}
