# SenseTime Beauty Extension Get Started

> How to Quickly Set Up SenseTime Beauty Plugin iOS Sample Project
>
> Other Language: [**简体中文**](README.zh.md)

---

## 1. Environment Preparation

- Minimum compatibility with Android 5.0 (SDK API Level 21)
- Android Studio 4.1 or above
- Real Android devices of 5.0 or above

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

##### 2.2 Enter [SenseTime Beauty](https://console.agora.io/marketplace/license/introduce?serviceName=sensetime-ar) and click "Contact Us" to obtain an exclusive certificate file

![xxx](https://web-cdn.agora.io/docs-files/1677137763250)

- Provide the package name bound to the license during the application and change "applicationId" in the "build.gradle" file of the project to your own bound package name.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-5.png)

##### 2.3 Fill in the required Agora App ID, token, and SenseTime certificate file name in [**Constants.java**](app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java) of the project. Note⚠️:

* If token is not activated for appid, mToken can be left blank.
* "mLicenseName" needs to be consistent with the local SenseTime certificate file name.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-1.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
mAppid: Agora App ID
mToken: Token corresponding to Agora App ID. If token is not activated for appid, it can be left blank. 
mLisenseName: SenseTime certificate
```

##### 2.4 Copy SenseTime beauty required resource files and the applied SenseTime beauty license to [**app/src/main/assets/**](app/src/main/assets/) directory of the project.

* [Download the resource file package needed by the demo by clicking here.](https://download.agora.io/marketplace/release/SenseTime_v8.9.3_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-2.png)

* The SenseTime beauty license file is copied to the [**app/src/main/assets/Resource/license/**](app/src/main/assets/Resource/license/) directory of the project, ensuring that the local file name is consistent with the one filled in [**Constants.java**](app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-3.png)

##### 2.5 Download the plugin android-release.aar file and copy it to the [**app/libs/**](app/libs/) directory of the project.

* [点击此处下载demo需要的插件aar](https://download.agora.io/marketplace/release/Agora_Marketplace_SenseTime_v8.9.3_Extension_for_Android_v4.1.1-2.zip)

<img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-4.png" alt="xxx" style="zoom:50%;" />

##### 2.6 Open the project with Android Studio, synchronize the project with Gradle files, connect to an Android device (not emulator), and run the project.



---

## 3. Project Introduction

### 3.1 Overview

> This project shows how to quickly integrate Agora Marketplace SenseTime beauty plugin through simple API calls.

### 3.2 Project File Structure

```
├── app
│   ├── src
│   │   ├── androidTest //Contains testing code for Android devices.
│   │   └── main
│   │       ├── assets //Contains all resource files required for beauty.
│   │       ├── java //Contains the main Java code.
│   │       │   ├── io/agora/rte/extension/sensetime/example //Contains all the configuration needed to be filled.
│   │       │   └── ...
│   │       └── res //Contains all resource files.
│   │           └── libs //Contains the plugin aar.
│   └── build.gradle //Gradle build script
├── gradle //Gradle file directory.
├── .gitignore //Git ignore file.
├── build.gradle //Project build script.
├── gradlew //Gradle Wrapper for Unix systems.
├── gradlew.bat //Gradle Wrapper for Windows systems.
├── settings.gradle //Settings for Gradle.
└── local.properties //Configuration file for local Android SDK directory.
```

### 3.3 Demo Effect

> // TODO
>
> ---
>
> * enableExtension: Enable/Disable Extension
> * initExtension: Initialize Extension/Authenticate
> * setBeautyMode: Set Beauty Mode
> * setBeauty: Set Beauty Effect
> * enableFaceDetect: Enable Face Detection Display Results

---

## 4. FAQ

### How to Obtain Agora App ID?

> Obtain Agora App ID at：[https://www.agora.io/cn/](https://www.agora.io/cn/)

### No Beauty Effect Appeared After Program Running?

> Refer to documentation: https://docs.agora.io/cn/extension_customer/api_sensetime

### Want to Learn about Other Agora Marketplace Plugins?

> Agora Marketplace Homepage: https://www.shengwang.cn/cn/marketplace/

### Encounter Problems During Integration, How to Contact Agora for Assistance?

> Solution 1: If you are already using Agora service or in contact with Agora sales or service, you can contact them directly.
>
> 方案2：Solution 2: Email [support@agora.io](mailto:support@agora.io) for consultation.

---