/*
 Update local Nacos config for xfglpt-gen.
 Keep this config ASCII-only. On Windows + JDK8, non-ASCII YAML comments can
 make SnakeYAML fail with MalformedInputException.
*/

USE `xfglpt-nacos`;

UPDATE `config_info` c
SET
  c.`content` = '# spring config
spring:
  redis:
    host: localhost
    port: 6379
    password:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/xfglpt?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456

# mybatis config
mybatis:
  typeAliasesPackage: com.xfgl.gen.domain
  mapperLocations: classpath:mapper/**/*.xml

# springdoc config
springdoc:
  gatewayUrl: http://localhost:8080/${spring.application.name}
  api-docs:
    enabled: true
  info:
    title: Code generation API
    description: Code generation API
    contact:
      name: RuoYi
      url: https://ruoyi.vip

# code generation
gen:
  author: yinet
  packageName: com.xfgl.school
  autoRemovePre: false
  tablePrefix: xfpt_
',
  c.`md5` = MD5(c.`content`),
  c.`gmt_modified` = NOW(),
  c.`src_user` = 'codex'
WHERE c.`data_id` = 'xfglpt-gen-dev.yml'
  AND c.`group_id` = 'DEFAULT_GROUP'
  AND c.`tenant_id` = '5e3e155d-73b2-4a36-ab8b-e54454e8b351';
