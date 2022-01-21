## Get started
### Android
1. Download the Android AAR from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases/download/bytedance-rte-extension-0.1.0/agora-bytedance-extension-0.1.0.aar), then copy the `agora-bytedance-extension-0.1.0.aar` to [`libs`](android/app/libs) directory;
2. Download the assets from ByteDance, then copy to [`assets`](android/app/src/main/assets) directory;
3. Update your `mAppId` and `mLicenseName` in [`Constants.java`](android/app/src/main/java/io/agora/rte/extension/bytedance/example/Constants.java) file.
4. Run the example

### iOS
1. Download the iOS framework from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases/download/bytedance-rte-extension-0.1.0/AgoraByteDanceExtension_0.1.0.zip), then copy the `AgoraByteDanceExtension.framework` to [`ExtensionExample`](ios/ExtensionExample) directory;
2. Download the assets from ByteDance, then copy to [`Resource`](ios/Resource) directory
3. Update your `appID` and `license_name` in [`AppID.m`](ios/ExtensionExample/AppID.m) file
4. Go to [`ios`](ios) directory and run `pod install`
5. Run the example