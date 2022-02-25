#!/usr/bin/env bash

set -e

IOS_PATH=$(find $(pwd) -name "ios")

for ios in $IOS_PATH; do
    echo "iOS path: $ios"
    pushd $ios
        pod install
        xcodebuild clean build CODE_SIGN_IDENTITY="" CODE_SIGNING_REQUIRED=NO -configuration Debug -workspace ExtensionExample.xcworkspace -scheme "ExtensionExample" CODE_SIGN_ENTITLEMENTS="" CODE_SIGNING_ALLOWED="NO"
    popd
done;