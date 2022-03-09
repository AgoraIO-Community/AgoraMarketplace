## Get started
### Android
1. Download the latest Android AAR from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases?q=sensetime-effect-plugin&expanded=true), then copy the `sensetime-effect-plugin-<latest-version>.aar` to `android/app/libs` directory;

2. Download the assets from SenseTime, then copy to `android/app/src/main/assets` directory;

3. Update your `mAppId`(e.g., `ccb8b8f5a8cdxxxxxxxx`) and `mLicenseName`(e.g., `SenseME.lic`) in `android/app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java` file.

4. Run the example

### iOS
1. Download the iOS latest framework from [AgoraMarketPlace release page](https://github.com/AgoraIO-Community/AgoraMarketPlace/releases?q=sensetime-effect-plugin&expanded=true), then copy the `AgoraSenseTimeExtension.framework` to `ios/ExtensionExample` directory;

2. Download the assets from SenseTime, then copy to `ios/Resource` directory.

3. Update your `appID`(e.g., `ccb8b8f5a8cdxxxxxxxx`) and `license_name`(e.g., `SenseME.lic`) in `ios/ExtensionExample/AppID.m` file

4. Go to `ios` directory and run `pod install`

5. Run the example