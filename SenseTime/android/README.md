# 商汤美颜插件快速开始

> 本文档主要介绍如何快速跑通 <mark>商汤美颜插件</mark> Android 示例工程
---

## 1. 环境准备

- <mark>最低兼容 Android 5.0</mark>（SDK API Level 21）
- Android Studio 4.1及以上版本。
- Android 5.0 及以上的真机设备。

---

## 2. 运行示例

##### 2.1 获取声网 App ID -------- [声网Agora - 文档中心 - 如何获取 App ID](https://docs.agora.io/cn/Agora%20Platform/get_appid_token?platform=All%20Platforms#%E8%8E%B7%E5%8F%96-app-id)

> - 点击创建应用
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/create_app_1.jpg)
>
> - 选择你要创建的应用类型
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/create_app_2.jpg)
>
> - 得到 App ID 与 App 证书
>
>   ![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/get_app_id.jpg)

##### 2.2 进入 [声网控制台 > 云市场 > 商汤高级美颜/特效系列](https://console.agora.io/marketplace/license/introduce?serviceName=sensetime-ar) 页面，点击**联系我们**获取专属的证书文件

![xxx](https://web-cdn.agora.io/docs-files/1677137763250)

- 申请时请提供绑定 License 的包名, 并将项目 [**build.gradle**](app/build.gradle) 文件中的applicaitionId 改为您自己 License 绑定的包名

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-5.png)

##### 2.3 在项目的 [**Constants.java**](app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java) 里填写需要的声网 App ID 、token 、商汤证书文件名, 注意⚠️:

* 若 appid 未开通 token 可不填写 mToken
* **mLicenseName** 需要和本地商汤证书文件名一致

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-1.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
mAppid: 声网appid
mToken: 声网appid对应的token, 若appid未开通token可不填写
mLisenseName: 商汤证书
```

##### 2.4 将商汤美颜需要的资源文件和申请的商汤美颜 License 拷贝到项目的 [**app/src/main/assets/**](app/src/main/assets/) 目录下

* [点击此处下载demo需要的资源文件包](https://download.agora.io/marketplace/release/SenseTime_v8.9.3_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-2.png)

* 商汤美颜 License 文件拷贝到项目 [**app/src/main/assets/Resource/license/**](app/src/main/assets/Resource/license/) 目录下, 请确保本地 Lisence文件名与 [**Constants.java**](app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java) 内填写的一致

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-3.png)

##### 2.5 下载插件 android-release.aar 文件, 并拷贝到项目 [**app/libs/**](app/libs/) 目录下

* [点击此处下载demo需要的插件aar](https://download.agora.io/marketplace/release/Agora_Marketplace_SenseTime_v8.9.3_Extension_for_Android_v4.1.1-2.zip)

<img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-4.png" alt="xxx" style="zoom:50%;" />

##### 2.6 用 Android Studio 打开项目、将项目与 Gradle 文件同步, 连接一台 Android 真机（非模拟器），运行项目

---

## 3. 项目介绍

### 3.1 概述

> 该项目展示了如何通过简单的 API 调用快速集成声网云市场商汤美颜插件

### 3.2 项目文件结构简介

```
├── app
│   ├── src
│   │   ├── androidTest //包含针对 Android 设备的测试代码
│   │   └── main
│   │       ├── assets //包含美颜需要的所有资源文件
│   │       ├── java //包含主要的 Java 代码
│   │       │   ├── io/agora/rte/extension/sensetime/example
│   │       │   │   ├── Constants //包含所有需要填写的配置
│   │       │   │   └── ......
│   │       │   └── androidManifest.xml //应用程序清单文件
│   │       └── res //包含所有的资源文件
│   │   
│   └── libs //放置插件 aar
│   └── build.gradle //Gradle 构建脚本
├── gradle //Gradle 的文件目录
├── .gitignore //Git 忽略文件
├── build.gradle //项目构建脚本
├── gradlew //Unix 系统的 Gradle Wrapper
├── gradlew.bat //Windows 系统的 Gradle Wrapper
├── settings.gradle //Gradle 的设置文件
└── local.properties //本地 Android SDK 目录的配置文件
```

### 3.3 Demo效果

> // TODO
>
> ---
>
> * enableExtension: 开启/关闭插件
> * initExtension: 初始化插件/鉴权
> * setBeautyMode: 设置美颜模式
> * setBeauty: 设置美颜效果
> * enableFaceDetect: 开启人脸检测结果显示

---

## 4. FAQ

### 如何获取声网 APPID

> 声网 APPID 申请：[https://www.agora.io/cn/](https://www.agora.io/cn/)

### 程序运行后，没有美颜效果
> 通常有以下几个原因：

> 1、插件动态库没有保存在正确位置，或者没有导入；
> 2、相芯资源包中的文件没有保存在正确位置，或者缺少部分文件；
> 3、证书文件与 app 包名不一致，导致鉴权失败。

### 想了解声网的其他云市场插件

> 声网云市场官网入口: https://www.shengwang.cn/cn/marketplace/

### 集成遇到困难，该如何联系声网获取协助

> 方案1：如果您已经在使用声网服务或者在对接中，可以直接联系对接的销售或服务；
>
> 方案2：发送邮件给 [support@agora.io](mailto:support@agora.io) 咨询

---