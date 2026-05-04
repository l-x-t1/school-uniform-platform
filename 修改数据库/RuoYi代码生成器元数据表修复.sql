/*
 Navicat 导出的项目库里只有 gen_table_column，没有 gen_table；
 同时 gen_table_column.COLUMN_ID 不是 AUTO_INCREMENT。
 RuoYi 代码生成器导入表时会写 gen_table / gen_table_column，
 因此需要先补齐并修正这两张生成器元数据表。

 说明：
 1. 本脚本不删除已有 gen_table_column 数据。
 2. gen_table 从 1000 开始自增，避免和当前 gen_table_column 里旧的孤立 table_id 冲突。
*/

CREATE TABLE IF NOT EXISTS `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作 sub主子表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';

ALTER TABLE `gen_table` AUTO_INCREMENT = 1000;

CREATE TABLE IF NOT EXISTS `gen_table_column` (
  `COLUMN_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `TABLE_ID` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `COLUMN_NAME` varchar(200) DEFAULT NULL COMMENT '列名称',
  `COLUMN_COMMENT` text COMMENT '列描述',
  `COLUMN_TYPE` varchar(100) DEFAULT NULL COMMENT '列类型',
  `JAVA_TYPE` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `JAVA_FIELD` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `IS_PK` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `IS_INCREMENT` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `IS_REQUIRED` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `IS_INSERT` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `IS_EDIT` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `IS_LIST` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `IS_QUERY` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `QUERY_TYPE` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `HTML_TYPE` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `DICT_TYPE` varchar(200) DEFAULT '' COMMENT '字典类型',
  `SORT` int(11) DEFAULT NULL COMMENT '排序',
  `CREATE_BY` varchar(64) DEFAULT '' COMMENT '创建者',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_BY` varchar(64) DEFAULT '' COMMENT '更新者',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`COLUMN_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';

ALTER TABLE `gen_table_column`
  MODIFY COLUMN `COLUMN_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  MODIFY COLUMN `TABLE_ID` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  MODIFY COLUMN `COLUMN_NAME` varchar(200) DEFAULT NULL COMMENT '列名称',
  MODIFY COLUMN `COLUMN_TYPE` varchar(100) DEFAULT NULL COMMENT '列类型',
  MODIFY COLUMN `JAVA_TYPE` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  MODIFY COLUMN `JAVA_FIELD` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  MODIFY COLUMN `IS_PK` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  MODIFY COLUMN `IS_INCREMENT` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  MODIFY COLUMN `IS_REQUIRED` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  MODIFY COLUMN `IS_INSERT` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  MODIFY COLUMN `IS_EDIT` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  MODIFY COLUMN `IS_LIST` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  MODIFY COLUMN `IS_QUERY` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  MODIFY COLUMN `QUERY_TYPE` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  MODIFY COLUMN `HTML_TYPE` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  MODIFY COLUMN `DICT_TYPE` varchar(200) DEFAULT '' COMMENT '字典类型',
  MODIFY COLUMN `SORT` int(11) DEFAULT NULL COMMENT '排序';

ALTER TABLE `gen_table_column` AUTO_INCREMENT = 1000;
