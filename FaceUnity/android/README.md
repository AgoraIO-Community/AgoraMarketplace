# FaceUnity Beauty Extension Get Started

> How to Quickly Set Up FaceUnity Beauty Extension Android Sample Project
>
> Other Language: [**简体中文**](README.zh.md)

---

## 1. Environment Preparation

- Minimum compatibility with Android 5.0 (SDK API Level 21).
- Android Studio 4.1 or higher.
- Real devices running Android 5.0 or higher.

---

## 2. Running Examples

##### 2.1 Obtain Agora App ID -------- [Obtain Agora App ID](https://docs.agora.io/en/video-calling/reference/manage-agora-account?platform=ios#get-the-app-id)

> - Obtain App ID and App certificate
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/Market-Place-1.png)

##### 2.2 Enter [FaceUnity Beauty](https://console.agora.io/marketplace/extension/introduce?serviceName=faceunity-ar-en) and click "Contact Us" to obtain an exclusive certificate file

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-EN-1.png)

- Provide the package name bound to the license during the application and change "applicationId" in the "build.gradle" file of the project to your own bound package name.

![xxx](https://github.com/AgoraIO-Community/AgoraMarketplace/assets/47940328/23210c29-d55e-4abb-aaf6-5da25a3a2135)

##### 2.3 Fill in the required Agora App ID, token, and FaceUnity certificate file name in [**Config.java**](extension_demo/src/main/java/io/agora/rte/extension/faceunity/Config.java) of the project. Note⚠️:

* If token is not activated for appid, mToken can be left blank.

![xxx](https://github.com/AgoraIO-Community/AgoraMarketplace/assets/47940328/7c090cd6-4645-4040-9d24-ab1ddab5a3a2)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
appID: Agora App ID
token: Token corresponding to Agora App ID. If token is not activated for appid, it can be left blank. 
```

##### 2.4 Copy the necessary resource files to the [**extension_demo/src/main/assets/**](extension_demo/src/main/assets/) directory of the project.

* [Click here to download the resource file package required for the demo](https://github.com/AgoraIO-Community/AgoraMarketplace/tree/master/FaceUnity/ResourceNG)

![xxx](https://github.com/AgoraIO-Community/AgoraMarketplace/assets/47940328/ce9b29d0-1aa4-4831-a3ec-2c21ebfcf091)

##### 2.5 Copy the FaceUnity Beauty License authpack.java file to the project's [**extension_demo/src/main/java/io/agora/rte/extension/faceunity/**](app/src/main/java/io/agora/rte/extension/faceunity/) directory. （This Demo contains an authpack bound to the package name io.agora.rte.extension.faceunity)

![xxx](https://github.com/AgoraIO-Community/AgoraMarketplace/assets/47940328/6600ecaf-a845-4c19-b80c-638a23d27519)
##### 2.6 Download the **android-release.aar** file of the extension and copy it to the [**app/libs/**](app/libs/) directory of the project. (This Demo already has this aar file in place)


![xxx](https://github.com/AgoraIO-Community/AgoraMarketplace/assets/47940328/7517ae8c-9f1c-4b1a-8d54-ffa5424cdb8f)

##### 2.7 Open the project with Android Studio, synchronize the project with Gradle files, connect to an Android device (not emulator), and run the project.

---

## 3. Project Introduction

### 3.1 Overview

> This project shows how to quickly integrate Agora Marketplace FaceUnity beauty extension through simple API calls.

### 3.2 Project File Structure

~~~
├── extension_demo
│   ├── src
│   │   ├── androidTest //Contains testing code for Android devices.
│   │   └── main
│   │       ├── assets //Contains all resource files required for beauty.
│   │       ├── java //Contains the main Java code.
│   │       │   ├── io/agora/rte/extension/faceunity/ //Contains all the configuration needed to be filled.
│   │       │   └── ...
│   │       └── res //Contains all resource files.
│   │           └── libs //Contains the extension aar.
│   └── build.gradle //Gradle build script
├── gradle //Gradle file directory.
├── .gitignore //Git ignore file.
├── build.gradle //Project build script.
├── gradlew //Gradle Wrapper for Unix systems.
├── gradlew.bat //Gradle Wrapper for Windows systems.
├── settings.gradle //Settings for Gradle.
└── local.properties //Configuration file for local Android SDK directory.
~~~



### 3.3 Demo Effect

> <img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-effect-3.jpg.jpg" width="300" height="640">
> <img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-effect-4.jpg.jpg" width="300" height="640">
>
> ---
>
> * enableExtension: Enable/Disable Extension
> * enableAITracking: Enable Face Detection Display Results
> * setComposer: Set Beauty Effect
> * setSticker: Set Sticker Effect
> * ENABLELIGHTMAKEUP: Enable/Disable Lightmakeup Effect

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
