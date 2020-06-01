import 'dart:async';

import 'package:flutter/services.dart';

class VideoCompress {
  factory VideoCompress() {
    if (_instance == null) {
      final MethodChannel methodChannel = const MethodChannel('video_compress');
      final EventChannel eventChannel =
          const EventChannel('video_compress_event');
      _instance = new VideoCompress.private(methodChannel, eventChannel);
    }
    return _instance;
  }

  VideoCompress.private(this._channel, this._event);

  final MethodChannel _channel;

  final EventChannel _event;

  static VideoCompress _instance;

  Stream<dynamic> _listener;

  Stream<dynamic> get onMessage {
    if (_listener == null) {
      _listener = _event
          .receiveBroadcastStream()
          .map((dynamic event) => _parseBatteryState(event));
    }
    return _listener;
  }

  Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /*
  * 视频压缩
  * */
  Future<bool> videoCompress(String path, String toPath) {
    try {
      return _channel.invokeMethod(
        'videoCompress',
        {"path": path, "toPath": toPath},
      );
    } catch (e) {
      return Future.value(false);
    }
  }

  dynamic _parseBatteryState(event) {
    return event;
  }
}
