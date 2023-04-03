# SenseTime Beauty Extension Get Started

> How to Quickly Set Up Commercial Tang Beauty Extension iOS Sample Project
>
> Other Language: [**简体中文**](README.zh.md)
---

## 1. Environment Preparation

- Xcode 9.0 或以上版本。
- 运行 iOS 9.0 或以上版本的真机（非模拟器）。

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

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-iOS-5.png)

##### 2.3 Fill in the required Agora App ID, token, and SenseTime certificate file name in [**AppID.m**](ExtensionExample/AppID.m) of the project. Note⚠️:

* If token is not activated for appid, mToken can be left blank.
* "license_name" needs to be consistent with the local SenseTime certificate file name.

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-iOS-4.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
appID: Agora App ID
token: Token corresponding to Agora App ID. If token is not activated for appid, it can be left blank. 
license_name: SenseTime certificate
```

##### 2.4 Copy the necessary resource files and acquired SenseTime Beauty License to the [**ExtensionExample/Resource/**](ExtensionExample/Resource) directory of the project.

* [Click here to download the resource file package required for the demo](https://download.agora.io/marketplace/release/SenseTime_v8.9.3_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-iOS-1.png)

* Copy the SenseTime Beauty License file to the project's [**ExtensionExample/Resource/license/**](ExtensionExample/Resource/license/) directory, Make sure that the local License file name is consistent with what is filled in [**AppID.m**](ExtensionExample/AppID.m) .

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-iOS-3.png)

##### 2.5 Download the .framework file of the extension and copy it to the [**ExtensionExample/**](ExtensionExample/) directory of the project.

* [Click here to download the extension framework required for the demo](https://download.agora.io/marketplace/release/Agora_Marketplace_SenseTime_v8.9.3_Extension_for_iOS_v4.1.1-2.zip)

<img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-iOS-2.png" alt="xxx" style="zoom:50%;" />

##### 2.6 In the terminal, enter the project root directory and run the following command to install dependencies using CocoaPods.

~~~shell
 pod install
~~~

##### Use Xcode to open the project file ExtensionExample.xcworkspace, connect an iOS device (not a simulator), and run the project.

---

## 3. Project Introduction

### 3.1 Overview

> This project shows how to quickly integrate Agora Marketplace SenseTime beauty extension through simple API calls.

### 3.2 Demo Effect

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

### Want to Learn about Other Agora Marketplace Extensions?

> Agora Marketplace Homepage: https://www.shengwang.cn/cn/marketplace/

### Encounter Problems During Integration, How to Contact Agora for Assistance?

> Solution 1: If you are already using Agora service or in contact with Agora sales or service, you can contact them directly.
>
> 方案2：Solution 2: Email [support@agora.io](mailto:support@agora.io) for consultation.

---