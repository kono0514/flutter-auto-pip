# flutter_auto_pip

Automatically enter/leave PIP mode on "onUserLeaveHint" lifecycle event.  
Android only.

## Usage

Enable auto PIP (Ideally before playing your video)
```dart
FlutterAutoPip.autoPipModeEnable()
```

Disable auto PIP (Ideally before destroying your player)
```dart
FlutterAutoPip.autoPipModeDisable()
```

Enter PIP mode manually
```dart
FlutterAutoPip.enterPipMode()
```

PIP mode entered listener
```dart
FlutterAutoPip.onPipModeEntered.listen(..)
```

PIP mode exited listener
```
Not implemented.  
Because I couldn't figure out how to override the activity callback "onPictureInPictureModeChanged" method
```
