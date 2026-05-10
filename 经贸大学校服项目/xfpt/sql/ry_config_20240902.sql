DROP DATABASE IF EXISTS `ry-config`;

CREATE DATABASE  `ry-config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE `ry-config`;

/******************************************/
/*   鐞涖劌鎮曠粔?= config_info   */
/******************************************/
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '閸掓稑缂撻弮鍫曟？',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娣囶喗鏁奸弮鍫曟？',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '缁夌喐鍩涚€涙顔?,
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  `encrypted_data_key` text COMMENT '缁夋﹢鎸?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

insert into config_info(id, data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, c_schema, encrypted_data_key) values 
(1,'application-dev.yml','DEFAULT_GROUP','spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n\n# feign 闁板秶鐤哱nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: true\n      min-request-size: 8192\n    response:\n      enabled: true\n\n# 閺嗘挳婀堕惄鎴炲付缁旑垳鍋nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n','9928f41dfb10386ad38b3254af5692e0','2020-05-20 12:00:00','2024-08-29 12:14:45','nacos','0:0:0:0:0:0:0:1','','','闁氨鏁ら柊宥囩枂','null','null','yaml','',''),
(2,'xfglpt-gateway-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 鐠併倛鐦夋稉顓炵妇\n        - id: xfglpt-auth\n          uri: lb://xfglpt-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 妤犲矁鐦夐惍浣割槱閻炲摫n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 娴狅絿鐖滈悽鐔稿灇\n        - id: xfglpt-gen\n          uri: lb://xfglpt-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 鐎规碍妞傛禒璇插\n        - id: xfglpt-job\n          uri: lb://xfglpt-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 缁崵绮哄Ο鈥虫健\n        - id: xfglpt-system\n          uri: lb://xfglpt-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 閺傚洣娆㈤張宥呭\n        - id: xfglpt-file\n          uri: lb://xfglpt-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n\n# 鐎瑰鍙忛柊宥囩枂\nsecurity:\n  # 妤犲矁鐦夐惍涔梟  captcha:\n    enabled: true\n    type: math\n  # 闂冨弶顒沊SS閺€璇插毊\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n\n  # 娑撳秵鐗庢宀€娅ч崥宥呭礋\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /*/v3/api-docs\n      - /csrf\n\n# springdoc闁板秶鐤哱nspringdoc:\n  webjars:\n    # 鐠佸潡妫堕崜宥囩磻\n    prefix:\n','4d329eb08a941a8dd9d26f542c6ac6c5','2020-05-14 14:17:55','2024-09-02 12:13:50','nacos','0:0:0:0:0:0:0:1','','','缂冩垵鍙уΟ鈥虫健','null','null','yaml','',''),
(3,'xfglpt-auth-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n','a03e7632a0a74520eeb4fbedd6d82d97','2020-11-20 00:00:00','2024-09-02 12:13:58','nacos','0:0:0:0:0:0:0:1','','','鐠併倛鐦夋稉顓炵妇','null','null','yaml','',''),
(4,'xfglpt-monitor-dev.yml','DEFAULT_GROUP','# spring\nspring:\n  security:\n    user:\n      name: ruoyi\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: 閼汇儰绶烽張宥呭閻樿埖鈧胶娲冮幒顪','6f122fd2bfb8d45f858e7d6529a9cd44','2020-11-20 00:00:00','2024-08-29 12:15:11','nacos','0:0:0:0:0:0:0:1','','','閻╂垶甯舵稉顓炵妇','null','null','yaml','',''),
(5,'xfglpt-system-dev.yml','DEFAULT_GROUP','# spring闁板秶鐤哱nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: ruoyi\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 娑撹绨遍弫鐗堝祦濠ф€絥          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://192.168.1.63:3306/xfglpt?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n          # 娴犲骸绨遍弫鐗堝祦濠ф€絥          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis闁板秶鐤哱nmybatis:\n    # 閹兼粎鍌ㄩ幐鍥х暰閸栧懎鍩嗛崥宄攏    typeAliasesPackage: com.xfgl.system\n    # 闁板秶鐤唌apper閻ㄥ嫭澹傞幓蹇ョ礉閹垫儳鍩岄幍鈧張澶屾畱mapper.xml閺勭姴鐨犻弬鍥︽\n    mapperLocations: classpath:mapper/**/*.xml\n\n# springdoc闁板秶鐤哱nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 閺勵垰鎯佸鈧崥顖涘复閸欙絾鏋冨顤    enabled: true\n  info:\n    # 閺嶅洭顣絓n    title: \'缁崵绮哄Ο鈥虫健閹恒儱褰涢弬鍥ㄣ€俓'\n    # 閹诲繗鍫猏n    description: \'缁崵绮哄Ο鈥虫健閹恒儱褰涢幓蹇氬牚\'\n    # 娴ｆ粏鈧懍淇婇幁鐥媙    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n','786c7daf4543411fc65c3e48dfb15243','2020-11-20 00:00:00','2024-09-02 12:14:33','nacos','0:0:0:0:0:0:0:1','','','缁崵绮哄Ο鈥虫健','null','null','yaml','',''),
(6,'xfglpt-gen-dev.yml','DEFAULT_GROUP','# spring闁板秶鐤哱nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://192.168.1.63:3306/xfglpt?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n\n# mybatis闁板秶鐤哱nmybatis:\n    # 閹兼粎鍌ㄩ幐鍥х暰閸栧懎鍩嗛崥宄攏    typeAliasesPackage: com.xfgl.gen.domain\n    # 闁板秶鐤唌apper閻ㄥ嫭澹傞幓蹇ョ礉閹垫儳鍩岄幍鈧張澶屾畱mapper.xml閺勭姴鐨犻弬鍥︽\n    mapperLocations: classpath:mapper/**/*.xml\n\n# springdoc闁板秶鐤哱nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 閺勵垰鎯佸鈧崥顖涘复閸欙絾鏋冨顤    enabled: true\n  info:\n    # 閺嶅洭顣絓n    title: \'娴狅絿鐖滈悽鐔稿灇閹恒儱褰涢弬鍥ㄣ€俓'\n    # 閹诲繗鍫猏n    description: \'娴狅絿鐖滈悽鐔稿灇閹恒儱褰涢幓蹇氬牚\'\n    # 娴ｆ粏鈧懍淇婇幁鐥媙    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n\n# 娴狅絿鐖滈悽鐔稿灇\ngen:\n  # 娴ｆ粏鈧尲n  author: ruoyi\n  # 姒涙顓婚悽鐔稿灇閸栧懓鐭惧?system 闂団偓閺€瑙勫灇閼奉亜绻侀惃鍕侀崸妤€鎮曠粔?婵?system monitor tool\n  packageName: com.xfgl.system\n  # 閼奉亜濮╅崢濠氭珟鐞涖劌澧犵紓鈧敍宀勭帛鐠併倖妲竑alse\n  autoRemovePre: false\n  # 鐞涖劌澧犵紓鈧敍鍫㈡晸閹存劗琚崥宥勭瑝娴兼艾瀵橀崥顐ャ€冮崜宥囩磻閿涘苯顦挎稉顏嗘暏闁褰块崚鍡涙閿涘　n  tablePrefix: sys_\n','14d41dda290bf445b7ed06c5667e8e88','2020-11-20 00:00:00','2024-09-02 12:14:48','nacos','0:0:0:0:0:0:0:1','','','娴狅絿鐖滈悽鐔稿灇','null','null','yaml','',''),
(7,'xfglpt-job-dev.yml','DEFAULT_GROUP','# spring闁板秶鐤哱nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://192.168.1.63:3306/xfglpt?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n\n# mybatis闁板秶鐤哱nmybatis:\n    # 閹兼粎鍌ㄩ幐鍥х暰閸栧懎鍩嗛崥宄攏    typeAliasesPackage: com.xfgl.job.domain\n    # 闁板秶鐤唌apper閻ㄥ嫭澹傞幓蹇ョ礉閹垫儳鍩岄幍鈧張澶屾畱mapper.xml閺勭姴鐨犻弬鍥︽\n    mapperLocations: classpath:mapper/**/*.xml\n\n# springdoc闁板秶鐤哱nspringdoc:\n  gatewayUrl: http://localhost:8080/${spring.application.name}\n  api-docs:\n    # 閺勵垰鎯佸鈧崥顖涘复閸欙絾鏋冨顤    enabled: true\n  info:\n    # 閺嶅洭顣絓n    title: \'鐎规碍妞傛禒璇插閹恒儱褰涢弬鍥ㄣ€俓'\n    # 閹诲繗鍫猏n    description: \'鐎规碍妞傛禒璇插閹恒儱褰涢幓蹇氬牚\'\n    # 娴ｆ粏鈧懍淇婇幁鐥媙    contact:\n      name: RuoYi\n      url: https://ruoyi.vip\n','f78483f845777335b9ed4a9f84758848','2020-11-20 00:00:00','2024-09-02 12:14:56','nacos','0:0:0:0:0:0:0:1','','','鐎规碍妞傛禒璇插','null','null','yaml','',''),
(8,'xfglpt-file-dev.yml','DEFAULT_GROUP','# 閺堫剙婀撮弬鍥︽娑撳﹣绱?   \r\nfile:\r\n    domain: http://127.0.0.1:9300\r\n    path: D:/ruoyi/uploadPath\r\n    prefix: /statics\r\n\r\n# FastDFS闁板秶鐤哱r\nfdfs:\r\n  domain: http://8.129.231.12\r\n  soTimeout: 3000\r\n  connectTimeout: 2000\r\n  trackerList: 8.129.231.12:22122\r\n\r\n# Minio闁板秶鐤哱r\nminio:\r\n  url: http://8.129.231.12:9000\r\n  accessKey: minioadmin\r\n  secretKey: minioadmin\r\n  bucketName: test','5382b93f3d8059d6068c0501fdd41195','2020-11-20 00:00:00','2020-12-21 21:01:59',NULL,'0:0:0:0:0:0:0:1','','','閺傚洣娆㈤張宥呭','null','null','yaml',NULL,''),
(10,'xfglpt-school-dev.yml','DEFAULT_GROUP','spring:
  redis:
    host: localhost
    port: 6379
    password: 
  datasource:
    druid:
      stat-view-servlet:
        enabled: true
        loginUsername: ruoyi
        loginPassword: 123456
    dynamic:
      primary: master
      druid:
        initial-size: 5
        min-idle: 5
        maxActive: 20
        maxWait: 60000
        connectTimeout: 30000
        socketTimeout: 60000
        timeBetweenEvictionRunsMillis: 60000
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1
        testWhileIdle: true
        testOnBorrow: false
        testOnReturn: false
        poolPreparedStatements: true
        maxPoolPreparedStatementPerConnectionSize: 20
        filters: stat,slf4j
        connectionProperties: druid.stat.mergeSql\=true;druid.stat.slowSqlMillis\=5000
      datasource:
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://192.168.1.63:3306/xfglpt?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
          username: root
          password: root

mybatis:
  typeAliasesPackage: com.xfgl.school.domain,com.xfgl.system.api.domain
  mapperLocations: classpath:mapper/**/*.xml

springdoc:
  gatewayUrl: http://localhost:8080/${spring.application.name}
  api-docs:
    enabled: true
  info:
    title: '瀛︽牎妯″潡鎺ュ彛鏂囨。'
    description: '瀛︽牎妯″潡鎺ュ彛鎻忚堪'
    contact:
      name: RuoYi
      url: https://ruoyi.vip
','b6b2f0d2e83a4d2a9f1d3f5f7c6a1b90','2026-05-09 17:05:00','2026-05-09 17:05:00','nacos','0:0:0:0:0:0:0:1','','','瀛︽牎妯″潡','null','null','yaml','',''),(9,'sentinel-xfglpt-gateway','DEFAULT_GROUP','[\r\n    {\r\n        \"resource\": \"xfglpt-auth\",\r\n        \"count\": 500,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"xfglpt-system\",\r\n        \"count\": 1000,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"xfglpt-gen\",\r\n        \"count\": 200,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"xfglpt-job\",\r\n        \"count\": 300,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    }\r\n]','9f3a3069261598f74220bc47958ec252','2020-11-20 00:00:00','2020-11-20 00:00:00',NULL,'0:0:0:0:0:0:0:1','','','闂勬劖绁︾粵鏍殣','null','null','json',NULL,'');


/******************************************/
/*   鐞涖劌鎮曠粔?= config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '閸愬懎顔?,
  `gmt_modified` datetime NOT NULL COMMENT '娣囶喗鏁奸弮鍫曟？',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '缁夌喐鍩涚€涙顔?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='婢х偛濮炵粔鐔稿煕鐎涙顔?;


/******************************************/
/*   鐞涖劌鎮曠粔?= config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '閸掓稑缂撻弮鍫曟？',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娣囶喗鏁奸弮鍫曟？',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '缁夌喐鍩涚€涙顔?,
  `encrypted_data_key` text COMMENT '缁夋﹢鎸?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   鐞涖劌鎮曠粔?= config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '閸掓稑缂撻弮鍫曟？',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娣囶喗鏁奸弮鍫曟？',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   鐞涖劌鎮曠粔?= config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   鐞涖劌鎮曠粔?= group_capacity   */
/******************************************/
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID閿涘瞼鈹栫€涙顑佺悰銊с仛閺佺繝閲滈梿鍡欏參',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '闁板秹顤傞敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '娴ｈ法鏁ら柌?,
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閸楁洑閲滈柊宥囩枂婢堆冪毈娑撳﹪妾洪敍灞藉礋娴ｅ秳璐熺€涙濡敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閼辨艾鎮庣€涙劙鍘ょ純顔芥付婢堆傞嚋閺佸府绱濋敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閸楁洑閲滈懕姘値閺佺増宓侀惃鍕摍闁板秶鐤嗘径褍鐨稉濠囨閿涘苯宕熸担宥勮礋鐎涙濡敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閺堚偓婢堆冨綁閺囨潙宸婚崣鍙夋殶闁?,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '閸掓稑缂撻弮鍫曟？',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娣囶喗鏁奸弮鍫曟？',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='闂嗗棛鍏㈤妴浣告倗Group鐎瑰綊鍣烘穱鈩冧紖鐞?;

/******************************************/
/*   鐞涖劌鎮曠粔?= his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '缁夌喐鍩涚€涙顔?,
  `encrypted_data_key` text COMMENT '缁夋﹢鎸?,
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='婢舵氨顫ら幋閿嬫暭闁?;


/******************************************/
/*   閺佺増宓佹惔鎾冲弿閸?= nacos_config   */
/*   鐞涖劌鎮曠粔?= tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '闁板秹顤傞敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '娴ｈ法鏁ら柌?,
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閸楁洑閲滈柊宥囩枂婢堆冪毈娑撳﹪妾洪敍灞藉礋娴ｅ秳璐熺€涙濡敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閼辨艾鎮庣€涙劙鍘ょ純顔芥付婢堆傞嚋閺?,
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閸楁洑閲滈懕姘値閺佺増宓侀惃鍕摍闁板秶鐤嗘径褍鐨稉濠囨閿涘苯宕熸担宥勮礋鐎涙濡敍?鐞涖劎銇氭担璺ㄦ暏姒涙顓婚崐?,
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '閺堚偓婢堆冨綁閺囨潙宸婚崣鍙夋殶闁?,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '閸掓稑缂撻弮鍫曟？',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娣囶喗鏁奸弮鍫曟？',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='缁夌喐鍩涚€瑰綊鍣烘穱鈩冧紖鐞?;


CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '閸掓稑缂撻弮鍫曟？',
  `gmt_modified` bigint(20) NOT NULL COMMENT '娣囶喗鏁奸弮鍫曟？',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
	`username` varchar(50) NOT NULL PRIMARY KEY,
	`password` varchar(500) NOT NULL,
	`enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
	`username` varchar(50) NOT NULL,
	`role` varchar(50) NOT NULL,
	UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
    `role` varchar(50) NOT NULL,
    `resource` varchar(255) NOT NULL,
    `action` varchar(8) NOT NULL,
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');
