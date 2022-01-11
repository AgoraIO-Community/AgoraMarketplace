## Get started
### Android
1. Download the Android AAR from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases/download/bytedance-rte-extension-0.1.0/bytedance-effect-plugin-0.1.0.aar), then copy the `bytedance-effect-plugin-0.1.0.aar` to `ByteDance/android/app/libs` directory;
2. Download the assets from ByteDance, then copy to `ByteDance/android/app/src/main/assets` directory;
3. Update your `mAppId` and `mLicenseName` in `ByteDance/android/app/src/main/java/io/agora/rte/extension/bytedance/example/Constants.java` file.
4. Run the example

### iOS
1. Download the iOS framework from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases/download/bytedance-rte-extension-0.1.0/AgoraByteDanceEffectExtension_0.1.0.zip), then copy the `AgoraByteDanceEffectExtension.framework` to `ByteDance/ios/ExtensionExample` directory;
2. Download the assets from ByteDance, then copy to `ByteDance/ios/Resource/` directory
3. Update your `appID` and `license_name` in `ByteDance/ios/ExtensionExample/AppID.m` file
4. Go to `ByteDance/ios` directory and run `pod install`
5. Run the example