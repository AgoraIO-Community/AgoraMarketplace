# 商汤美颜插件

> 本文档主要介绍如何快速跑通 <mark>商汤美颜插件</mark> Android 示例工程

---

## 1. 环境准备

- <mark>最低兼容 Android 5.0</mark>（SDK API Level 21）
- Android Studio 3.5及以上版本。
- Android 5.0 及以上的手机设备。

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

##### 2.2 <mark>联系销售申请商汤美颜 License </mark>(如果您没有销售人员的联系方式可通过智能客服联系销售人员 [Agora 支持](https://agora-ticket.agora.io/) )

- 申请时请提供绑定 License 的包名, 并将项目 [**build.gradle**](app/build.gradle) 文件中的applicaitionId 改为您自己 License 绑定的包名

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-5.png)

##### 2.3 在项目的 [**Constants.java**](app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java) 里填写需要的声网 App ID 、token 、商汤证书文件名, 注意⚠️:

* 若 appid 未开通 token 可不填写 mToken
* **mLicenseName** 需要和本地商汤证书文件名一致

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-1.png)

```texag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0ag-0-1gpap96h0ag-1-1gpap96h0
mAppid: 声网appid
mToken: 声网appid对应的token, 若appid未开通token可不填写
mLisenseName: 声网Certificate
```

##### 2.4 将商汤美颜需要的资源文件和申请的商汤美颜 License 拷贝到项目的 [**app/src/main/assets/**](app/src/main/assets/) 目录下

* [点击此处下载demo需要的资源文件包](https://download.agora.io/marketplace/release/SenseTime_v8.9.3_Resources.zip)

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-2.png)

* 商汤美颜 License 文件拷贝到项目  [**app/src/main/assets/Resource/license/**](app/src/main/assets/Resource/license/) 目录下, 请确保本地 Lisence文件名与 [**Constants.java**](app/src/main/java/io/agora/rte/extension/sensetime/example/Constants.java) 内填写的一致

![xxx](https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-3.png)

##### 2.5 下载插件 .aar 文件, 并拷贝到项目   [**app/libs/**](app/libs/) 目录下

<img src="https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/github_readme/market-place/SenseTime/SenseTime-Android-4.png" alt="xxx" style="zoom:50%;" />

##### 2.6 用 Android Studio 运行项目即可开始您的体验

---

## 3. 项目介绍

### 3.1 概述

> 

### 3.2 项目文件结构简介

```

```

### 3.3 Demo效果

> 
>
> ---
>
> 

---

## 4. FAQ

### 如何获取声网 APPID

> 声网 APPID 申请：[https://www.agora.io/cn/](https://www.agora.io/cn/)

### 程序运行后，没有美颜效果

> 

### 想了解声网的其他云市场插件

> 

### 集成遇到困难，该如何联系声网获取协助

> 方案1：如果您已经在使用声网服务或者在对接中，可以直接联系对接的销售或服务；
>
> 方案2：发送邮件给 [support@agora.io](mailto:support@agora.io) 咨询

---