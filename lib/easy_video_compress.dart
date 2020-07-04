import 'dart:async';

import 'package:flutter/services.dart';

class EasyVideoCompress {
  factory EasyVideoCompress() {
    if (_instance == null) {
      final MethodChannel methodChannel = const MethodChannel('easy_video_compress');
      final EventChannel eventChannel =
          const EventChannel('easy_video_compress_event');
      _instance = new EasyVideoCompress.private(methodChannel, eventChannel);
    }
    return _instance;
  }

  EasyVideoCompress.private(this._channel, this._event);

  final MethodChannel _channel;

  final EventChannel _event;

  static EasyVideoCompress _instance;

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

  Future<String> get getPath async {
    final String path = await _channel.invokeMethod('getPath');
    return path;
  }

  /*
  * 视频压缩
  * */
  Future<bool> easyVideoCompress(String path, String toPath) async {
    try {
      return await _channel.invokeMethod(
        'easyVideoCompress',
        {"path": path, "toPath": toPath},
      );
    } on PlatformException {
      return false;
    }
  }

  dynamic _parseBatteryState(event) {
    return event;
  }
}
