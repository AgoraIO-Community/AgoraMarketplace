import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:agora_rtc_engine/agora_rtc_engine.dart';
import 'package:image/image.dart';
import 'package:tuple/tuple.dart';
import 'authpack.dart' as authpack;
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as path;
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter/services.dart' show rootBundle;
import 'package:image/image.dart' as img;

const rtcAppId = '<YOUR_APP_ID>';


// REMINDER: Update this value for ai_face_processor.bundle if the FaceUnity sdk be updated.
const aiFaceProcessorType = 1 << 8;
const aiHandProcessorType = 1 << 3;
const aiHumanProcessorType = 1 << 9;

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
  bool _enableExtension = false;
  bool _enableAITracking = false;
  bool _enableComposer = false;
  bool _enableLightMarkup = false;
  double _colorLevel = 0.5;
  double _filterLevel = 0.5;

  int _facesNum = 0;
  int _handsNum = 0;
  int _peopleNum = 0;

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

        final jsonObj = jsonDecode(value);

        if (key == 'fuIsTracking') {
          setState(() {
            _facesNum = jsonObj["faces"];
          });
        } else if (key == 'fuHandDetectorGetResultNumHands') {
          setState(() {
            _handsNum = jsonObj["hands"];
          });
        } else if (key == 'fuHumanProcessorGetNumResults') {
          setState(() {
            _peopleNum = jsonObj["people"];
          });
        }
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
    // BugFixed: U should call enableExtension True first;
    await _rtcEngine.enableExtension(
        provider: "FaceUnity", extension: "Effect", enable: true);
    await _rtcEngine.enableExtension(
        provider: "FaceUnity", extension: "Effect", enable: _enableExtension);

    await _rtcEngine.enableVideo();
    await _rtcEngine.startPreview();

    setState(() {
      _isReadyPreview = true;
    });
  }

  Future<void> _loadAIModels() async {
    final aiFaceProcessorPath =
        await _copyAsset('Resource/model/ai_face_processor.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuLoadAIModelFromPackage',
        value: jsonEncode(
            {'data': aiFaceProcessorPath, 'type': aiFaceProcessorType}));

    final aiHandProcessorPath =
        await _copyAsset('Resource/model/ai_hand_processor.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuLoadAIModelFromPackage',
        value: jsonEncode(
            {'data': aiHandProcessorPath, 'type': aiHandProcessorType}));

    final aiHumanProcessorPath =
        await _copyAsset('Resource/model/ai_human_processor_gpu.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuLoadAIModelFromPackage',
        value: jsonEncode(
            {'data': aiHumanProcessorPath, 'type': aiHumanProcessorType}));

    final aitypePath = await _copyAsset('Resource/graphics/aitype.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuCreateItemFromPackage',
        value: jsonEncode({'data': aitypePath}));
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuItemSetParam',
        value: jsonEncode({
          'obj_handle': aitypePath,
          'name': "aitype",
          "value": 1 << 8 | 1 << 30 | 1 << 3,
        }));
  }

  Future<void> _setLightMarkup() async {
    final lightMarkupDir =
        await _copyAsset("Resource/light_makeup/light_makeup.bundle");
    const blusherDir = "Resource/light_makeup/blusher/mu_blush_02.png";
    const eyebrowDir = "Resource/light_makeup/eyebrow/mu_eyebrow_02.png";
    const eyeShadow = "Resource/light_makeup/eyeshadow/mu_eyeshadow_02.png";
    const eyeLiner = "Resource/light_makeup/eyeliner/mu_eyeliner_04.png";
    const eyelash = "Resource/light_makeup/eyelash/mu_eyelash_03.png";
    const lipstickDir = "Resource/light_makeup/lipstick/mu_lip_01.json";

    final texLoads = [blusherDir, eyebrowDir, eyeShadow, eyeLiner, eyelash];

    final texNames = [
      "tex_blusher",
      "tex_brow",
      "tex_eye",
      "tex_eyeLiner",
      "tex_eyeLash"
    ];

    if (_enableLightMarkup) {
      const makeup_intensity_lip = 0.9;
      const makeup_intensity_blusher = 0.9;
      const makeup_intensity_eyeBrow = 1.0;
      const makeup_intensity_eye = 1.0;
      const makeup_intensity_eyeLiner = 1.0;
      const makeup_intensity_eyelash = 1.0;
      const int is_use_fix = 1;

      //off
      final double makeup_intensity_pupil = 0;

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuCreateItemFromPackage',
          value: jsonEncode({
            'data': lightMarkupDir,
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "is_makeup_on",
            'value': 1
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "is_use_fix",
            'value': is_use_fix,
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity",
            'value': 1.0
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_eye",
            'value': makeup_intensity_eye
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_eyeBrow",
            'value': makeup_intensity_eyeBrow
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_lip",
            'value': makeup_intensity_lip
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_pupil",
            'value': makeup_intensity_pupil
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_eyeLiner",
            'value': makeup_intensity_eyeLiner
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_eyelash",
            'value': makeup_intensity_eyelash
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_intensity_blusher",
            'value': makeup_intensity_blusher
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_lip_mask",
            'value': 1.0
          }));

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuItemSetParam',
          value: jsonEncode({
            'obj_handler': lightMarkupDir,
            'name': "makeup_lip_color",
            'value': await _loadJSONFile(lipstickDir),
          }));

      for (var i = 0; i < texLoads.length; ++i) {
        final currentFile = texLoads[i];
        final currentName = texNames[i];

        final img = await _loadPic(currentFile);

        await _rtcEngine.setExtensionProperty(
            provider: 'FaceUnity',
            extension: 'Effect',
            key: 'fuCreateTexForItem',
            value: jsonEncode({
              'item': lightMarkupDir,
              'name': currentName,
              'value': img.item1,
              'width': img.item2,
              'height': img.item3,
            }));
      }
    } else {
      for (var i = 0; i < texLoads.length; ++i) {
        final currentName = texNames[i];

        await _rtcEngine.setExtensionProperty(
            provider: 'FaceUnity',
            extension: 'Effect',
            key: 'fuDeleteTexForItem',
            value: jsonEncode({
              'item': lightMarkupDir,
              'name': currentName,
            }));
      }

      await _rtcEngine.setExtensionProperty(
          provider: 'FaceUnity',
          extension: 'Effect',
          key: 'fuDestroyItem',
          value: jsonEncode({
            'item': lightMarkupDir,
          }));
    }
  }

  FutureOr<Tuple3<String, int, int>> _loadPic(String path) async {
    ByteData bytes = await rootBundle.load(path);
    img.Image? image = PngDecoder().decode(bytes.buffer.asUint8List());

    return Tuple3<String, int, int>(
        base64Encode(image!.toUint8List()), image.width, image.height);
  }

  Future<dynamic> _loadJSONFile(String path) async {
    String response = await rootBundle.loadString(path);
    var ret = await json.decode(response);

    return ret['rgba'];
  }

  Future<void> _setComposerStuff(double colorValue, double filterValue) async {
    final path =
        await _copyAsset('Resource/graphics/face_beautification.bundle');
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuItemSetParam',
        value: jsonEncode({
          'obj_handle': path,
          'name': "filter_name",
          'value': "ziran2",
        }));

    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuItemSetParam',
        value: jsonEncode({
          'obj_handle': path,
          'name': "filter_level",
          'value': filterValue,
        }));

    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuItemSetParam',
        value: jsonEncode({
          'obj_handle': path,
          'name': "color_level",
          'value': colorValue,
        }));
  }

  Future<void> _initFUExtension() async {
    await _rtcEngine.setExtensionProperty(
        provider: 'FaceUnity',
        extension: 'Effect',
        key: 'fuSetup',
        value: jsonEncode({'authdata': authpack.gAuthPackage}));

    _loadAIModels();
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
      alignment: AlignmentDirectional.bottomEnd,
      children: [
        AgoraVideoView(
            controller: VideoViewController(
          rtcEngine: _rtcEngine,
          canvas: const VideoCanvas(uid: 0),
        )),
        Container(
          alignment: Alignment.bottomCenter,
          width: 200,
          height: 550,
          decoration: const BoxDecoration(
            color: Colors.transparent,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            //mainAxisAlignment: MainAxisAlignment.start,

            children: [
              _enableAITracking
                  ? Text(
                      "faces: $_facesNum",
                      textAlign: TextAlign.left,
                      style: const TextStyle(
                          color: Colors.white70, fontWeight: FontWeight.bold),
                    )
                  : Container(),
              _enableAITracking
                  ? Text(
                      "hands: $_handsNum",
                      textAlign: TextAlign.left,
                      style: const TextStyle(
                          color: Colors.white70, fontWeight: FontWeight.bold),
                    )
                  : Container(),
              _enableAITracking
                  ? Text(
                      "people: $_peopleNum",
                      textAlign: TextAlign.left,
                      style: const TextStyle(
                          color: Colors.white70, fontWeight: FontWeight.bold),
                    )
                  : Container(),
              TextButton(
                style: TextButton.styleFrom(foregroundColor: Colors.blue),
                onPressed: () async {
                  setState(() {
                    _enableExtension = !_enableExtension;
                  });

                  await _rtcEngine.enableExtension(
                      provider: "FaceUnity",
                      extension: "Effect",
                      enable: _enableExtension);
                },
                child: Text(
                    _enableExtension ? 'disableExtension' : 'enableExtension'),
              ),
              TextButton(
                style: TextButton.styleFrom(foregroundColor: Colors.blue),
                onPressed: () async {
                  setState(() {
                    _enableAITracking = !_enableAITracking;
                  });

                  if (_enableAITracking) {
                    await _rtcEngine.setExtensionProperty(
                        provider: 'FaceUnity',
                        extension: 'Effect',
                        key: 'fuSetMaxFaces',
                        value: jsonEncode({
                          'n': 5,
                        }));

                    await _rtcEngine.setExtensionProperty(
                        provider: 'FaceUnity',
                        extension: 'Effect',
                        key: 'fuIsTracking',
                        value: jsonEncode({
                          'enable': true,
                        }));

                    await _rtcEngine.setExtensionProperty(
                        provider: 'FaceUnity',
                        extension: 'Effect',
                        key: 'fuHumanProcessorGetNumResults',
                        value: jsonEncode({
                          'enable': true,
                        }));

                    await _rtcEngine.setExtensionProperty(
                        provider: 'FaceUnity',
                        extension: 'Effect',
                        key: 'fuHumanProcessorSetMaxHumans',
                        value: jsonEncode({
                          'max_humans': 5,
                        }));

                    await _rtcEngine.setExtensionProperty(
                        provider: 'FaceUnity',
                        extension: 'Effect',
                        key: 'fuHandDetectorGetResultNumHands',
                        value: jsonEncode({
                          'enable': true,
                        }));
                  }
                },
                child: Text(_enableAITracking
                    ? 'disableAITracking'
                    : 'enableAITracking'),
              ),
              TextButton(
                style: TextButton.styleFrom(foregroundColor: Colors.blue),
                onPressed: () async {
                  final catSparksPath = await _copyAsset(
                      'Resource/items/ItemSticker/CatSparks.bundle');
                  await _rtcEngine.setExtensionProperty(
                      provider: 'FaceUnity',
                      extension: 'Effect',
                      key: 'fuCreateItemFromPackage',
                      value: jsonEncode({'data': catSparksPath}));
                },
                child: const Text('setSticker'),
              ),
              TextButton(
                style: TextButton.styleFrom(foregroundColor: Colors.blue),
                onPressed: () async {
                  final lightMarkup = await _copyAsset(
                      'Resource/light_makeup/light_makeup.bundle');

                  setState(() {
                    _enableLightMarkup = !_enableLightMarkup;
                  });

                  _setLightMarkup();
                },
                child: const Text('setLightMarkup'),
              ),
              TextButton(
                style: TextButton.styleFrom(foregroundColor: Colors.blue),
                onPressed: () async {
                  final aiBeautificationPath = await _copyAsset(
                      'Resource/graphics/face_beautification.bundle');
                  await _rtcEngine.setExtensionProperty(
                      provider: 'FaceUnity',
                      extension: 'Effect',
                      key: 'fuCreateItemFromPackage',
                      value: jsonEncode({'data': aiBeautificationPath}));

                  setState(() {
                    _enableComposer = true;
                  });

                  _setComposerStuff(_colorLevel, _filterLevel);
                },
                child: const Text('setComposer'),
              ),
              const Text('Color Level:', textAlign: TextAlign.left),
              Slider(
                  value: _colorLevel,
                  onChanged: _enableComposer
                      ? (double value) async {
                          setState(() {
                            _colorLevel = value;
                          });

                          _setComposerStuff(_colorLevel, _filterLevel);
                        }
                      : null),
              const Text('Filter Level:', textAlign: TextAlign.left),
              Slider(
                  value: _filterLevel,
                  onChanged: _enableComposer
                      ? (double value) async {
                          setState(() {
                            _filterLevel = value;
                          });

                          _setComposerStuff(_colorLevel, _filterLevel);
                        }
                      : null),
            ],
          ),
        ),
      ],
    );
  }
}
