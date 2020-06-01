import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:image_picker/image_picker.dart';
import 'package:video_compress/video_compress.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _chargingStatus = 'Unknown';

  @override
  void initState() {
    super.initState();
    VideoCompress().onMessage.listen((dynamic onData) {
      setState(() {
        _chargingStatus = onData.toString();
      });
    });
  }

  selectVideo() async {
    File video = await ImagePicker.pickVideo(source: ImageSource.gallery);
    if (video == null) return;
    String result = await VideoCompress().videoCompress(video.path);
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
