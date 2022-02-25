#!/usr/bin/env bash

set -e

ANDROID_PATH=$(find $(pwd) -name "android")
IOS_PATH=$(find $(pwd) -name "ios")

for android in $ANDROID_PATH; do
    echo "Android path: $android"
    pushd $android
        ./gradlew clean assembleDebug
    popd
done;

for ios in $IOS_PATH; do
    echo "iOS path: $ios"
    pushd $ios
        pod install
        xcodebuild clean build CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO -configuration Debug -workspace ExtensionExample.xcworkspace -scheme "ExtensionExample" CODE_SIGN_ENTITLEMENTS="" CODE_SIGNING_ALLOWED="NO"
    popd
done;

# xcodebuild clean build CODE_SIGN_IDENTITY="Apple Development" CODE_SIGNING_REQUIRED=NO -configuration Debug -workspace ExtensionExample.xcworkspace -scheme "ExtensionExample" CODE_SIGN_ENTITLEMENTS="" CODE_SIGNING_ALLOWED="NO"