import 'dart:io';

import 'package:easy_video_compress/easy_video_compress.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:image_picker/image_picker.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  EasyVideoCompress().platformVersion;
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _chargingStatus = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
    print('Flutter:::aaaaaa');

    EasyVideoCompress().onMessage.listen((dynamic onData) {
      print('Flutter:::${onData.toString()}');
      setState(() {
        _chargingStatus = onData.toString();
      });
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await EasyVideoCompress().platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _chargingStatus = platformVersion;
    });
  }

  selectVideo() async {
    File video = await ImagePicker.pickVideo(source: ImageSource.gallery);
    if (video == null) return;

    DateTime d = DateTime.now();
    String download = await EasyVideoCompress().getPath;
    String dateStr =
        'video_${d.year}_${d.month}_${d.day}_${d.hour}_${d.minute}_${d.second}.mp4';
    print('完整路径：：$download$dateStr');
    bool result = await EasyVideoCompress()
        .easyVideoCompress(video.path, '$download$dateStr');
    print('压缩结果::$result');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: <Widget>[
            new FlatButton(
              onPressed: () => selectVideo(),
              child: new Text('选择'),
            )
          ],
        ),
        body: Center(
          child: Text('Running on: $_chargingStatus\n'),
        ),
      ),
    );
  }
}
