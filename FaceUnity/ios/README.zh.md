# 相芯美颜插件快速开始

> 本文档主要介绍如何快速跑通 <mark>相芯美颜插件</mark> iOS 示例工程
>
> 其他语言版本: [**English**](README.md)
---

## 1. 环境准备

- Xcode 9.0 或以上版本。
- 运行 iOS 9.0 或以上版本的真机（非模拟器）。

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

##### 2.2  在声网控制台 [**购买和激活**](https://docs.agora.io/cn/extension_customer/get_extension?platform=All Platforms) 相芯美颜道具高级版插件，点击联系我们获取专属的证书文件

- 申请时请提供绑定 License 的包名, 并将项目 Bundle Id 改为您自己 License 绑定的包名

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-5.png)

##### 2.3 在项目的 [**Config.h**](ExtensionExample/Config.h) 里填写需要的声网 App ID 、token, 注意⚠️:

* 若 appid 未开通 token 可不填写 mToken

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-4.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
appID: 声网appid
token: 声网appid对应的token, 若appid未开通token可不填写
```

##### 2.4 **将相芯美颜需要的资源文件拷贝到项目的** [**ExtensionExample/Resource/**](ExtensionExample/Resource) 目录下

* [点击此处下载demo需要的资源文件包](https://download.agora.io/marketplace/release/FaceUnity_v8.4.1_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-1.png)

##### 2.5 相芯美颜 authpack.h 文件拷贝到项目 [**ExtensionExample/**](ExtensionExample/) 目录下

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-3.png)

##### 2.6 下载插件 .framework 文件, 并拷贝到项目 [**ExtensionExample/**](ExtensionExample/) 目录下

* [点击此处下载demo需要的插件framework](https://download.agora.io/marketplace/release/Agora_Marketplace_FaceUnity_v8.4.1_Extension_for_iOS_v4.1.1.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/FaceUnity/FaceUnity-iOS-2.png)

##### 2.6 在终端中进入项目根目录，运行以下命令使用 CocoaPods 安装依赖

~~~shell
 pod install
~~~

##### 2.7 使用 Xcode 打开项目文件 ExtensionExample.xcworkspace ，连接一台 iOS 真机（非模拟器），运行项目

---

## 3. 项目介绍

### 3.1 概述

> 该项目展示了如何通过简单的 API 调用快速集成声网云市场相芯美颜插件

### 3.2 Demo效果

> <img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-effect-1.jpg" width="300" height="640"><img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-effect-2.jpg" width="300" height="640">
>
> ---
>
> * enableExtension: 开启/关闭插件
> * enableAITracking: 开启人脸检测
> * setComposer: 设置美颜效果
> * setSticker: 设置猫脸贴纸效果

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