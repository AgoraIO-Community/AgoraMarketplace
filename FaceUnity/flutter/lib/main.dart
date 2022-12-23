import 'dart:convert';
import 'dart:ffi';
import 'dart:io';

import 'package:agora_rtc_engine/agora_rtc_engine.dart';
import 'authpack.dart' as authpack;
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as path;
import 'package:permission_handler/permission_handler.dart';

const rtcAppId = '<YOUR_APP_ID>';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Demo Home Page'),
        ),
        body: const MyHomePage(),
      ),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({
    super.key,
  });

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late final RtcEngine _rtcEngine;
  late final RtcEngineEventHandler _rtcEngineEventHandler;
  bool _isReadyPreview = false;

  Future<String> _copyAsset(String assetPath) async {
    ByteData data = await rootBundle.load(assetPath);
    List<int> bytes =
        data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);

    Directory appDocDir = await getApplicationDocumentsDirectory();

    final dirname = path.dirname(assetPath);

    Directory dstDir = Directory(path.join(appDocDir.path, dirname));
    if (!(await dstDir.exists())) {
      await dstDir.create(recursive: true);
    }

    String p = path.join(appDocDir.path, path.basename(assetPath));
    final file = File(p);
    if (!(await file.exists())) {
      await file.create();
      await file.writeAsBytes(bytes);
    }

    return file.absolute.path;
  }

  Future<void> _requestPermissionIfNeed() async {
    if (defaultTargetPlatform == TargetPlatform.android) {
      await [Permission.microphone, Permission.camera].request();
    }
  }

  Future<void> _init() async {
    await _requestPermissionIfNeed();
    _rtcEngine = createAgoraRtcEngine();
    await _rtcEngine.initialize(const RtcEngineContext(
      appId: rtcAppId,
      logConfig: LogConfig(level: LogLevel.logLevelNone),
      channelProfile: ChannelProfileType.channelProfileLiveBroadcasting,
    ));

    _rtcEngineEventHandler = RtcEngineEventHandler(
      onExtensionEvent: (provider, extension, key, value) {
        debugPrint(
            '[onExtensionEvent] provider: $provider, extension: $extension, key: $key, value: $value');
      },
      onExtensionStarted: (provider, extension) {
        debugPrint(
            '[onExtensionStarted] provider: $provider, extension: $extension');
        if (provider == 'FaceUnity' && extension == 'Effect') {
          _initFUExtension();
        }
      },
      onExtensionError: (provider, extension, error, message) {
        debugPrint(
            '[onExtensionError] provider: $provider, extension: $extension, error: $error, message: $message');
      },
    );
    _rtcEngine.registerEventHandler(_rtcEngineEventHandler);

    // On Android, you should load libAgoraFaceUnityExtension.so explicitly
    if (Platform.isAndroid) {
      await _rtcEngine.loadExtensionProvider(path: 'AgoraFaceUnityExtension');
    }
    await _rtcEngine.enableExtension(
        provider: "FaceUnity", extension: "Effect", enable: true);

    await _rtcEngine.enableVideo();
    await _rtcEngine.startPreview();

    setState(() {
      _isReadyPreview = true;
    });
  }

  Future<void> _initFUExtension() async {
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuSetup',
        value: jsonEncode({'authdata': authpack.gAuthPackage}));

    final aiFaceProcessorPath =
        await _copyAsset('Resource/model/ai_face_processor.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuLoadAIModelFromPackage',
        value: jsonEncode({'data': aiFaceProcessorPath, 'type': 1 << 10}));

    final catSparksPath =
        await _copyAsset('Resource/items/ItemSticker/CatSparks.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuCreateItemFromPackage',
        value: jsonEncode({'data': catSparksPath}));
  }

  Future<void> _dispose() async {
    _rtcEngine.unregisterEventHandler(_rtcEngineEventHandler);
    await _rtcEngine.release();
  }

  @override
  void initState() {
    super.initState();

    _init();
  }

  @override
  void dispose() {
    _dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_isReadyPreview) {
      return Container();
    }

    return Stack(
      children: [
        AgoraVideoView(
            controller: VideoViewController(
          rtcEngine: _rtcEngine,
          canvas: const VideoCanvas(uid: 0),
        )),
      ],
    );
  }
}
