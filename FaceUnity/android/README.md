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

##### 2.2 Enter [FaceUnity Beauty](https://console.agora.io/marketplace/license/introduce?serviceName=faceunity-ar) and click "Contact Us" to obtain an exclusive certificate file

![xxx](https://web-cdn.agora.io/docs-files/1677137763250)

- Provide the package name bound to the license during the application and change "applicationId" in the "build.gradle" file of the project to your own bound package name.

![xxx](https://web-cdn.agora.io/docs-files/1679457359046)

##### 2.3 ill in the required Agora App ID, token, and FaceUnity certificate file name in [**Config.java**](app/src/main/java/io/agora/rte/extension/faceunity/example/Config.java) of the project. Note⚠️:

* If token is not activated for appid, mToken can be left blank.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-Android-5.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
appID: Agora App ID
token: Token corresponding to Agora App ID. If token is not activated for appid, it can be left blank. 
```

##### 2.4 Copy the necessary resource files and acquired FaceUnity Beauty License to the [**app/src/main/assets/**](app/src/main/assets/) directory of the project.

* [Click here to download the resource file package required for the demo](https://download.agora.io/marketplace/release/FaceUnity_v8.4.1_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-Android-1.png)

##### 2.5 Copy the FaceUnity Beauty License authpack.java file to the project's [**app/src/main/java/io/agora/rte/extension/faceunity/example/**](app/src/main/java/io/agora/rte/extension/faceunity/example/) directory.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-Android-2.png)

##### 2.6 Download the **android-release.aar** file of the extension and copy it to the [**app/libs/**](app/libs/) directory of the project.

* [Click here to download the extension aar required for the demo](https://download.agora.io/marketplace/release/Agora_Marketplace_FaceUnity_v8.4.1_Extension_for_Android_v4.1.1.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-Android-3.png)

##### 2.7 Open the project with Android Studio, synchronize the project with Gradle files, connect to an Android device (not emulator), and run the project.

---

## 3. Project Introduction

### 3.1 Overview

> This project shows how to quickly integrate Agora Marketplace FaceUnity beauty extension through simple API calls.

### 3.2 Project File Structure

~~~
├── app
│   ├── src
│   │   ├── androidTest //Contains testing code for Android devices.
│   │   └── main
│   │       ├── assets //Contains all resource files required for beauty.
│   │       ├── java //Contains the main Java code.
│   │       │   ├── io/agora/rte/extension/faceunity/example //Contains all the configuration needed to be filled.
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