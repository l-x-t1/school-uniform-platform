# Nacos SMTP 邮件配置

在 Nacos 配置中心找到 xfglpt-school 的配置项，在 spring: 节点下新增以下内容：

`yaml
spring:
  mail:
    host: smtp.xxx.com          # SMTP 服务器地址（根据实际邮箱服务商填写）
    port: 465                   # SSL 端口（QQ邮箱/企业邮箱一般为 465）
    username: noreply@xxx.com   # 发件邮箱地址
    password: your_auth_code    # 邮箱授权码（非登录密码）
    protocol: smtps
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true
          starttls:
            enable: false
`

## 常见邮箱 SMTP 配置参考

| 邮箱类型 | host | port |
|---------|------|------|
| QQ 邮箱 | smtp.qq.com | 465 |
| 163 邮箱 | smtp.163.com | 465 |
| 企业微信邮箱 | smtp.exmail.qq.com | 465 |
| 阿里企业邮箱 | smtp.mxhichina.com | 465 |

> **注意**：password 应使用邮箱的 **授权码**（非登录密码），需在邮箱设置中开启 SMTP 服务后获取。
> 建议使用 Nacos 的加密功能或环境变量 ${MAIL_PASSWORD} 来保护密码安全。
