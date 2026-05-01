# IDEA 本地启动说明

## 前置条件

1. 工程根目录：`D:\xiaofu\经贸大学校服项目\后端代码\xfpt`
2. JDK：项目 `pom.xml` 要求 `Java 1.8`
3. MySQL：`127.0.0.1:3306`
4. Nacos：`127.0.0.1:8848`
5. Redis：`127.0.0.1:6379`

## 已处理的本地化配置

以下配置已经改成适合本机启动：

1. 所有服务注册 IP 统一改为 `127.0.0.1`
2. `xfglpt-supplier` 的 `spring.application.name` 已修正
3. `xfglpt-app` 注册端口已修正为 `9206`
4. `xfglpt-monitor` 已改为连接本地 `localhost:8848`
5. Nacos 与业务模块 MySQL JDBC 参数已兼容当前本机 MySQL 认证方式

## IDEA 操作

1. 用 IDEA 打开工程根目录 `xfpt`
2. 等待 Maven 全量导入完成
3. 把项目 SDK 手动切到 `JDK 1.8`
4. IDEA 会自动识别 `.run` 目录下的运行配置

## 运行配置清单

| 配置名 | 模块 | Main Class | 端口 |
|---|---|---|---:|
| `01-auth` | `xfgl-auth` | `com.xfgl.auth.RuoYiAuthApplication` | `9200` |
| `02-system` | `xfgl-modules-system` | `com.xfgl.system.RuoYiSystemApplication` | `9201` |
| `03-gen` | `xfgl-modules-gen` | `com.xfgl.gen.RuoYiGenApplication` | `9202` |
| `04-job` | `xfgl-modules-job` | `com.xfgl.job.RuoYiJobApplication` | `9203` |
| `05-file` | `xfgl-modules-file` | `com.xfgl.file.RuoYiFileApplication` | `9300` |
| `06-school` | `xfgl-modules-shcool` | `com.xfgl.RuoYiSchoolApplication` | `9204` |
| `07-supplier` | `xfgl-modules-education` | `com.xfgl.RuoYiSupplierApplication` | `9211` |
| `08-app` | `xfgl-modules-app` | `com.xfgl.system.RuoYiAppApplication` | `9206` |
| `09-monitor` | `xfgl-visual-monitor` | `com.xfgl.modules.monitor.RuoYiMonitorApplication` | `9100` |
| `10-gateway` | `xfgl-gateway` | `com.xfgl.gateway.RuoYiGatewayApplication` | `8080` |

## 推荐启动顺序

按下面顺序逐个启动，最稳：

1. `01-auth`
2. `02-system`
3. `03-gen`
4. `04-job`
5. `05-file`
6. `06-school`
7. `07-supplier`
8. `08-app`
9. `09-monitor`
10. `10-gateway`

## 最小可用启动集

如果只是先跑主链路，可以先起这些：

1. `01-auth`
2. `02-system`
3. `05-file`
4. `06-school`
5. `07-supplier`
6. `08-app`
7. `10-gateway`

## 常见问题

### 1. IDEA 里看不到运行配置

确认工程打开的是：

`D:\xiaofu\经贸大学校服项目\后端代码\xfpt`

并确认 `.run` 目录存在。

### 2. 启动时报 JDK 版本不兼容

把 IDEA 的 `Project SDK` 和 `Module SDK` 都切到 `JDK 1.8`。

### 3. 启动时报连不上 Redis

确认本机 `6379` 端口可用。

### 4. 启动时报连不上 Nacos

确认本机 `8848` 端口可用，并且 Nacos 已启动。

### 5. 启动时报连不上 MySQL

确认本机 `3306` 可用，数据库 `xfglpt` 和 `xfglpt-nacos` 已存在。
