# FaceUnity Beauty Extension Get Started

> How to Quickly Set Up FaceUnity Beauty Extension iOS Sample Project
>
> Other Language: [**简体中文**](README.zh.md)
---

## 1. Environment Preparation

- Xcode 9.0 or later version.
- A real iOS device running on iOS 9.0 or later version, not a simulator.

---

## 2. Running Examples

##### 2.1 Obtain Agora App ID -------- [Obtain Agora App ID](https://docs.agora.io/en/video-calling/reference/manage-agora-account?platform=ios#get-the-app-id)

> - Obtain App ID and App certificate
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/Market-Place-1.png)

##### 2.2 Enter [FaceUnity Beauty](https://console.agora.io/marketplace/extension/introduce?serviceName=faceunity-ar-en) and click "Contact Us" to obtain an exclusive certificate file

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-EN-1.png)

- Provide the package name bound to the license during the application and change "Bundle Id" of the project to your own bound package name.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-5.png)

##### 2.3 Fill in the required Agora App ID, token in [Config.h](ExtensionExample/Config.h**) of the project. Note⚠️:

* If token is not activated for appid, mToken can be left blank.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-4.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
appID: Agora App ID
token: Token corresponding to Agora App ID. If token is not activated for appid, it can be left blank. 
```

##### 2.4 Copy the necessary resource files to the [**Resource**](Resource/) directory of the project.

* [Click here to download the resource file package required for the demo](https://download.agora.io/marketplace/release/FaceUnity_v8.4.1_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-1.png)

##### 2.5 Copy the FaceUnity Beauty License authpack.h file to the project's [**ExtensionExample/**](ExtensionExample/) directory.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-3.png)

##### 2.6 Download the .framework file of the extension and copy it to the [**ExtensionExample/**](ExtensionExample/) directory of the project.

* [Click here to download the extension framework required for the demo](https://download.agora.io/marketplace/release/Agora_Marketplace_FaceUnity_v8.6.0_Extension_for_iOS_v4.1.1.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-2.png)

##### 2.7 In the terminal, enter the project root directory and run the following command to install dependencies using CocoaPods.

~~~shell
 pod install
~~~

##### Use Xcode to open the project file ExtensionExample.xcworkspace, connect an iOS device (not a simulator), and run the project.

---

## 3. Project Introduction

### 3.1 Overview

> This project shows how to quickly integrate Agora Marketplace FaceUnity beauty extension through simple API calls.

### 3.2 Demo Effect

> <img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-effect-1.jpg" width="300" height="640"><img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-effect-2.jpg" width="300" height="640">
>
> ---
>
> * enableExtension: Enable/Disable Extension
> * enableAITracking: Enable Face Detection Display Results
> * setComposer: Set Beauty Effect
> * setSticker: Set Sticker Effect

---

## 4. FAQ

### How to Obtain Agora App ID?

> Obtain Agora App ID at：[Obtain Agora App ID](https://docs.agora.io/en/video-calling/reference/manage-agora-account?platform=ios#get-the-app-id)

### No Beauty Effect Appeared After Program Running?

> 1、The extension dynamic library is not saved in the correct location or not imported.
> 2、The files in the Xiangxin resource package are not saved in the correct location or some files are missing.
> 3、The certificate file is inconsistent with the app package name, resulting in authentication failure.

### Want to Learn about Other Agora Marketplace Extensions?

> Agora Marketplace Homepage: https://www.agora.io/en/agora-extensions-marketplace/

### Encounter Problems During Integration, How to Contact Agora for Assistance?

> Solution 1: If you are already using Agora service or in contact with Agora sales or service, you can contact them directly.
>
> Solution 2: Email [support@agora.io](mailto:support@agora.io) for consultation.

---