/*
 将 20 张新增业务表预导入 RuoYi 代码生成器元数据。

 执行前置条件：
 1. 已执行 20张业务表创建SQL.sql。
 2. 已执行 RuoYi代码生成器元数据表修复.sql。

 执行效果：
 1. gen_table 中直接出现 20 张表。
 2. 主表默认设置为主子表模板：
    - xfpt_goods -> xfpt_goods_size(goods_id)
    - xfpt_school_supplier_rel -> xfpt_school_supplier_goods_rel(rel_id)
    - xfpt_group_notice -> xfpt_group_notice_item(notice_id)
    - xfpt_order_info -> xfpt_order_item(order_id)
    - xfpt_production_order -> xfpt_production_order_item(production_id)
    - xfpt_delivery_order -> xfpt_delivery_order_item(delivery_id)
    - xfpt_comment -> xfpt_comment_reply(comment_id)
 3. 前端模板默认 Vue2 Element UI，包路径默认 com.xfgl.school。
*/

DROP FUNCTION IF EXISTS `xfglpt_gen_to_camel`;

DELIMITER //
CREATE FUNCTION `xfglpt_gen_to_camel`(input_text varchar(255), upper_first tinyint)
RETURNS varchar(255)
DETERMINISTIC
BEGIN
  DECLARE part_count int DEFAULT 0;
  DECLARE idx int DEFAULT 1;
  DECLARE part varchar(255) DEFAULT '';
  DECLARE result_text varchar(255) DEFAULT '';

  SET input_text = LOWER(input_text);
  SET part_count = LENGTH(input_text) - LENGTH(REPLACE(input_text, '_', '')) + 1;

  WHILE idx <= part_count DO
    SET part = SUBSTRING_INDEX(SUBSTRING_INDEX(input_text, '_', idx), '_', -1);
    IF idx = 1 AND upper_first = 0 THEN
      SET result_text = CONCAT(result_text, part);
    ELSE
      SET result_text = CONCAT(result_text, UPPER(LEFT(part, 1)), SUBSTRING(part, 2));
    END IF;
    SET idx = idx + 1;
  END WHILE;

  RETURN result_text;
END//
DELIMITER ;

DROP TEMPORARY TABLE IF EXISTS `tmp_xfglpt_gen_table_map`;
CREATE TEMPORARY TABLE `tmp_xfglpt_gen_table_map` (
  `table_id` bigint(20) NOT NULL,
  `table_name` varchar(200) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `business_name` varchar(64) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `tpl_category` varchar(20) COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sub_table_name` varchar(200) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `sub_table_fk_name` varchar(64) COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`table_id`),
  UNIQUE KEY `uk_tmp_gen_table_name` (`table_name`)
) ENGINE=Memory DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `tmp_xfglpt_gen_table_map`
  (`table_id`, `table_name`, `business_name`, `tpl_category`, `sub_table_name`, `sub_table_fk_name`)
VALUES
  (10001, 'xfpt_goods', 'goods', 'sub', 'xfpt_goods_size', 'goods_id'),
  (10002, 'xfpt_goods_size', 'goodsSize', 'crud', NULL, NULL),
  (10003, 'xfpt_school_supplier_rel', 'schoolSupplierRel', 'sub', 'xfpt_school_supplier_goods_rel', 'rel_id'),
  (10004, 'xfpt_school_supplier_goods_rel', 'schoolSupplierGoodsRel', 'crud', NULL, NULL),
  (10005, 'xfpt_group_notice', 'groupNotice', 'sub', 'xfpt_group_notice_item', 'notice_id'),
  (10006, 'xfpt_group_notice_item', 'groupNoticeItem', 'crud', NULL, NULL),
  (10007, 'xfpt_supplier_apply', 'supplierApply', 'crud', NULL, NULL),
  (10008, 'xfpt_user_address', 'userAddress', 'crud', NULL, NULL),
  (10009, 'xfpt_cart_item', 'cartItem', 'crud', NULL, NULL),
  (10010, 'xfpt_order_info', 'orderInfo', 'sub', 'xfpt_order_item', 'order_id'),
  (10011, 'xfpt_order_item', 'orderItem', 'crud', NULL, NULL),
  (10012, 'xfpt_production_order', 'productionOrder', 'sub', 'xfpt_production_order_item', 'production_id'),
  (10013, 'xfpt_production_order_item', 'productionOrderItem', 'crud', NULL, NULL),
  (10014, 'xfpt_delivery_order', 'deliveryOrder', 'sub', 'xfpt_delivery_order_item', 'delivery_id'),
  (10015, 'xfpt_delivery_order_item', 'deliveryOrderItem', 'crud', NULL, NULL),
  (10016, 'xfpt_after_sale', 'afterSale', 'crud', NULL, NULL),
  (10017, 'xfpt_comment', 'comment', 'sub', 'xfpt_comment_reply', 'comment_id'),
  (10018, 'xfpt_comment_reply', 'commentReply', 'crud', NULL, NULL),
  (10019, 'xfpt_finance_flow', 'financeFlow', 'crud', NULL, NULL),
  (10020, 'xfpt_withdraw_apply', 'withdrawApply', 'crud', NULL, NULL);

DELETE c
FROM `gen_table_column` c
JOIN `gen_table` t ON c.`table_id` = t.`table_id`
JOIN `tmp_xfglpt_gen_table_map` m ON t.`table_name` COLLATE utf8mb4_0900_ai_ci = m.`table_name`;

DELETE FROM `gen_table_column` WHERE `table_id` BETWEEN 10001 AND 10020;

DELETE t
FROM `gen_table` t
JOIN `tmp_xfglpt_gen_table_map` m ON t.`table_name` COLLATE utf8mb4_0900_ai_ci = m.`table_name`;

DELETE FROM `gen_table` WHERE `table_id` BETWEEN 10001 AND 10020;

INSERT INTO `gen_table` (
  `table_id`,
  `table_name`,
  `table_comment`,
  `sub_table_name`,
  `sub_table_fk_name`,
  `class_name`,
  `tpl_category`,
  `tpl_web_type`,
  `package_name`,
  `module_name`,
  `business_name`,
  `function_name`,
  `function_author`,
  `gen_type`,
  `gen_path`,
  `options`,
  `create_by`,
  `create_time`,
  `remark`
)
SELECT
  m.`table_id`,
  m.`table_name`,
  t.`table_comment`,
  m.`sub_table_name`,
  m.`sub_table_fk_name`,
  `xfglpt_gen_to_camel`(m.`table_name`, 1),
  m.`tpl_category`,
  'element-ui',
  'com.xfgl.school',
  'school',
  m.`business_name`,
  REPLACE(REPLACE(t.`table_comment`, '表', ''), '若依', ''),
  'yinet',
  '0',
  '/',
  '{"parentMenuId":"3"}',
  'codex',
  NOW(),
  'Codex预导入20张业务表'
FROM `tmp_xfglpt_gen_table_map` m
JOIN `information_schema`.`tables` t
  ON t.`table_schema` = DATABASE()
 AND CONVERT(t.`table_name` USING utf8mb4) COLLATE utf8mb4_0900_ai_ci = m.`table_name`;

INSERT INTO `gen_table_column` (
  `table_id`,
  `column_name`,
  `column_comment`,
  `column_type`,
  `java_type`,
  `java_field`,
  `is_pk`,
  `is_increment`,
  `is_required`,
  `is_insert`,
  `is_edit`,
  `is_list`,
  `is_query`,
  `query_type`,
  `html_type`,
  `dict_type`,
  `sort`,
  `create_by`,
  `create_time`
)
SELECT
  m.`table_id`,
  LOWER(c.`column_name`),
  c.`column_comment`,
  LOWER(c.`column_type`),
  CASE
    WHEN c.`data_type` IN ('date', 'datetime', 'time', 'timestamp') THEN 'Date'
    WHEN c.`data_type` IN ('decimal', 'float', 'double') THEN 'BigDecimal'
    WHEN c.`data_type` = 'bigint' THEN 'Long'
    WHEN c.`data_type` IN ('tinyint', 'smallint', 'mediumint', 'int', 'integer', 'bit') THEN 'Integer'
    ELSE 'String'
  END,
  `xfglpt_gen_to_camel`(LOWER(c.`column_name`), 0),
  CASE WHEN c.`column_key` = 'PRI' THEN '1' ELSE '0' END,
  CASE WHEN c.`extra` = 'auto_increment' THEN '1' ELSE '0' END,
  CASE WHEN c.`is_nullable` = 'NO' AND c.`column_key` <> 'PRI' THEN '1' ELSE NULL END,
  '1',
  CASE
    WHEN c.`column_key` = 'PRI' THEN NULL
    WHEN LOWER(c.`column_name`) IN ('id', 'create_by', 'create_time', 'del_flag') THEN NULL
    ELSE '1'
  END,
  CASE
    WHEN c.`column_key` = 'PRI' THEN NULL
    WHEN LOWER(c.`column_name`) IN ('id', 'create_by', 'create_time', 'del_flag', 'update_by', 'update_time') THEN NULL
    WHEN LOWER(c.`column_name`) IN (
      'payee_bank_account_no',
      'receipt_qr_code',
      'detail_images_json',
      'detail_content',
      'measure_json',
      'size_rule_json',
      'apply_files_json',
      'compare_files_json',
      'images_json',
      'bank_info_json',
      'content',
      'reply_content'
    ) THEN NULL
    ELSE '1'
  END,
  CASE
    WHEN c.`column_key` = 'PRI' THEN NULL
    WHEN LOWER(c.`column_name`) IN ('id', 'create_by', 'create_time', 'del_flag', 'update_by', 'update_time', 'remark') THEN NULL
    WHEN LOWER(c.`column_name`) IN (
      'payee_bank_account_no',
      'receipt_qr_code',
      'detail_images_json',
      'detail_content',
      'measure_json',
      'size_rule_json',
      'apply_files_json',
      'compare_files_json',
      'images_json',
      'bank_info_json',
      'content',
      'reply_content'
    ) THEN NULL
    ELSE '1'
  END,
  CASE
    WHEN LOWER(c.`column_name`) LIKE '%name' THEN 'LIKE'
    WHEN LOWER(c.`column_name`) LIKE '%no' THEN 'LIKE'
    WHEN LOWER(c.`column_name`) LIKE '%code' THEN 'LIKE'
    ELSE 'EQ'
  END,
  CASE
    WHEN LOWER(c.`column_name`) IN ('main_image', 'receipt_qr_code') THEN 'imageUpload'
    WHEN LOWER(c.`column_name`) LIKE '%image' THEN 'imageUpload'
    WHEN LOWER(c.`column_name`) LIKE '%file' THEN 'fileUpload'
    WHEN LOWER(c.`column_name`) LIKE '%status' THEN 'radio'
    WHEN LOWER(c.`column_name`) LIKE '%type' OR LOWER(c.`column_name`) LIKE '%sex' THEN 'select'
    WHEN LOWER(c.`column_name`) LIKE '%content' THEN 'editor'
    WHEN c.`data_type` IN ('date', 'datetime', 'time', 'timestamp') THEN 'datetime'
    WHEN c.`data_type` IN ('tinytext', 'text', 'mediumtext', 'longtext') THEN 'textarea'
    WHEN COALESCE(c.`character_maximum_length`, 0) >= 500 THEN 'textarea'
    ELSE 'input'
  END,
  '',
  c.`ordinal_position`,
  'codex',
  NOW()
FROM `information_schema`.`columns` c
JOIN `tmp_xfglpt_gen_table_map` m
  ON CONVERT(c.`table_name` USING utf8mb4) COLLATE utf8mb4_0900_ai_ci = m.`table_name`
WHERE c.`table_schema` = DATABASE()
ORDER BY m.`table_id`, c.`ordinal_position`;

ALTER TABLE `gen_table` AUTO_INCREMENT = 11000;
ALTER TABLE `gen_table_column` AUTO_INCREMENT = 11000;

DROP TEMPORARY TABLE IF EXISTS `tmp_xfglpt_gen_table_map`;
DROP FUNCTION IF EXISTS `xfglpt_gen_to_camel`;
