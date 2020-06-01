import 'dart:async';

import 'package:flutter/services.dart';

class VideoCompress {
  static const MethodChannel _channel =
      const MethodChannel('video_compress');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
