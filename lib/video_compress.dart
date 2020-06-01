import 'dart:async';

import 'package:flutter/services.dart';

class VideoCompress {
  static const MethodChannel _channel = const MethodChannel('video_compress');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /*
  * 视频压缩
  * */
  static videoCompress(path) {
    _channel.invokeMethod('videoCompress', {"path": path});
  }
}
