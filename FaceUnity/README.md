## Get started
### Android
1. Download the Android AAR from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases/download/faceunity-rte-extension-0.1.0/agora-faceunity-extension-0.1.0.aar), then copy the `agora-faceunity-extension-0.1.0.aar` to [`libs`](android/app/libs) directory
2. Download the assets from FaceUnity, then copy to [`assets`](android/app/src/main/assets) directory
3. Copy `authpack.java` to [`example`](android/app/src/main/java/io/agora/rte/extension/faceunity/example) directory
4. Update your `mAppId` in [`Constants.java`](android/app/src/main/java/io/agora/rte/extension/faceunity/example/Constants.java) file
5. Run the example

### iOS
1. Download the iOS framework from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases/download/faceunity-rte-extension-0.1.0/AgorafaceunityExtension_0.1.0.zip), then copy the `AgoraFaceUnityExtension.framework` to [`ExtensionExample`](ios/ExtensionExample) directory
2. Download the assets from FaceUnity, then copy to [`Resource`](ios/Resource) directory
3. Copy `authpack.h` to [`ExtensionExample`](ios/ExtensionExample) directory
4. Update your `appID` in [`AppID.m`](ios/ExtensionExample/AppID.m) file
5. Go to [`ios`](ios) directory and run `pod install`
6. Run the example