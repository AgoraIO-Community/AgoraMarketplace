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

##### 2.1 Obtain Agora App ID -------- [Obtain Agora App ID](https://docs.agora.io/cn/Agora%20Platform/get_appid_token?platform=All%20Platforms#%E8%8E%B7%E5%8F%96-app-id)

> - Click "Create Application"
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/create_app_1.jpg)
>
> - Choose the application type to create
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/create_app_2.jpg)
>
> - Obtain App ID and App certificate
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/get_app_id.jpg)

##### 2.2 Enter [Contact Us]() and click "Contact Us" to obtain an exclusive certificate file

![xxx](https://web-cdn.agora.io/docs-files/1677137763250)

- Provide the package name bound to the license during the application and change "Bundle Id" of the project to your own bound package name.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-5.png)

##### 2.3 Fill in the required Agora App ID, token in [Config.h](ExtensionExample/Config.h**) of the project. Note⚠️:

* If token is not activated for appid, mToken can be left blank.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-4.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
appID: Agora App ID
token: Token corresponding to Agora App ID. If token is not activated for appid, it can be left blank. 
```

##### 2.4 Copy the necessary resource files and acquired FaceUnity Beauty License to the [**ExtensionExample/Resource/**](ExtensionExample/Resource) directory of the project.

* [Click here to download the resource file package required for the demo](https://download.agora.io/marketplace/release/FaceUnity_v8.4.1_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-1.png)

* Copy the FaceUnity Beauty License authpack.h file to the project's [**ExtensionExample/**](ExtensionExample/) directory.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-3.png)

##### 2.5 Download the .framework file of the extension and copy it to the [**ExtensionExample/**](ExtensionExample/) directory of the project.

* [Click here to download the extension framework required for the demo](https://download.agora.io/marketplace/release/Agora_Marketplace_FaceUnity_v8.4.1_Extension_for_iOS_v4.1.1.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-2.png)

##### 2.6 In the terminal, enter the project root directory and run the following command to install dependencies using CocoaPods.

~~~shell
 pod install
~~~

##### Use Xcode to open the project file ExtensionExample.xcworkspace, connect an iOS device (not a simulator), and run the project.

---

## 3. Project Introduction

### 3.1 Overview

> This project shows how to quickly integrate Agora Marketplace FaceUnity beauty extension through simple API calls.

### 3.2 Demo Effect

> // TODO
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

> Obtain Agora App ID at：[https://www.agora.io/cn/](https://www.agora.io/cn/)

### No Beauty Effect Appeared After Program Running?

> 1、The extension dynamic library is not saved in the correct location or not imported.
> 2、The files in the Xiangxin resource package are not saved in the correct location or some files are missing.
> 3、The certificate file is inconsistent with the app package name, resulting in authentication failure.

### Want to Learn about Other Agora Marketplace Extensions?

> Agora Marketplace Homepage: https://www.shengwang.cn/cn/marketplace/

### Encounter Problems During Integration, How to Contact Agora for Assistance?

> Solution 1: If you are already using Agora service or in contact with Agora sales or service, you can contact them directly.
>
> Solution 2: Email [support@agora.io](mailto:support@agora.io) for consultation.

---