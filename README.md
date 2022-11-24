# Sync-It

一个多设备实时同步（剪贴板、文件等）的客户端与服务端应用，实现自动实时同步设备剪贴板内容，手动上传文件暂存与下载，管理在线设备等功能。

已完成**windows**与**macos**端应用编写，安卓端应用正在准备开发中。

使用**springboot+redis+mysql**提供后端服务， **electron**构建windows与macos应用。

## 使用

### 部署云端

在[release](https://github.com/Foreverddb/Sync-It/releases/latest)中下载名为`backend-version.zip`的源码文件或直接下载`backend`目录源码，手动进行maven打包后部署服务器使用。

#### 配置项

在后端源码的`src/main/resources/application.yml`目录中修改如下配置项：

```yml
spring:
  datasource:
    url: mysql数据库地址
    username: mysql用户名
    password: mysql密码
  redis:
    host: redis主机地址
    port: redis端口
    password: redis密码
```

使用`java -jar xxx.jar`运行maven打包生成的jar包。

### 使用应用

部署好云端应用后，在[release](https://github.com/Foreverddb/Sync-It/releases/latest)中下载并打开对应系统的安装包，安装即可，软件会在初次运行时要求手动配置服务端信息，跟随应用指引即可。

## 应用截图

同步剪贴板列表：

![9ADC927A94330BE23B8148130A602065](https://user-images.githubusercontent.com/60093071/203846924-17af08be-4954-4598-a0bc-aac95fb44532.jpg)
