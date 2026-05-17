-- 教育局结算管理：独立业务表和菜单权限
-- 说明：只新增教育局结算查询数据源和菜单，不改现有提现、资金流水、订单、支付流程。

CREATE TABLE IF NOT EXISTS `xfpt_edu_settlement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `unit_name` varchar(200) NOT NULL COMMENT '结算单位',
  `project_name` varchar(200) NOT NULL COMMENT '项目名称',
  `settlement_cycle` varchar(20) NOT NULL COMMENT '结算周期，格式yyyy-MM',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '结算状态，0待结算 1部分结算 2已结算',
  `settlement_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '结算金额',
  `tax_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '税额',
  `paid_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '已付金额',
  `unpaid_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '未付金额',
  `dept_id` bigint DEFAULT NULL COMMENT '当前归属部门/教育局',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_xfpt_edu_settlement_unit` (`unit_name`),
  KEY `idx_xfpt_edu_settlement_cycle` (`settlement_cycle`),
  KEY `idx_xfpt_edu_settlement_status` (`status`),
  KEY `idx_xfpt_edu_settlement_dept` (`dept_id`),
  KEY `idx_xfpt_edu_settlement_del` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教育局结算管理';

INSERT INTO `xfpt_edu_settlement` (
  `unit_name`, `project_name`, `settlement_cycle`, `status`,
  `settlement_amount`, `tax_amount`, `paid_amount`, `unpaid_amount`,
  `dept_id`, `create_by`, `create_time`, `remark`, `del_flag`
)
SELECT '石家庄市教育局', '2026年春季校服统购项目', '2026-05', '0',
       128600.00, 7716.00, 0.00, 128600.00,
       NULL, 'admin', NOW(), '按市级统购项目汇总结算，当前等待财务确认付款。', '0'
WHERE NOT EXISTS (
  SELECT 1 FROM `xfpt_edu_settlement`
  WHERE `unit_name` = '石家庄市教育局'
    AND `project_name` = '2026年春季校服统购项目'
    AND `settlement_cycle` = '2026-05'
    AND `del_flag` = '0'
);

INSERT INTO `xfpt_edu_settlement` (
  `unit_name`, `project_name`, `settlement_cycle`, `status`,
  `settlement_amount`, `tax_amount`, `paid_amount`, `unpaid_amount`,
  `dept_id`, `create_by`, `create_time`, `remark`, `del_flag`
)
SELECT '长安区教育局', '小学夏季校服采购项目', '2026-05', '1',
       86520.00, 5191.20, 40000.00, 46520.00,
       NULL, 'admin', NOW(), '已完成首批付款，剩余金额待到货验收后结算。', '0'
WHERE NOT EXISTS (
  SELECT 1 FROM `xfpt_edu_settlement`
  WHERE `unit_name` = '长安区教育局'
    AND `project_name` = '小学夏季校服采购项目'
    AND `settlement_cycle` = '2026-05'
    AND `del_flag` = '0'
);

INSERT INTO `xfpt_edu_settlement` (
  `unit_name`, `project_name`, `settlement_cycle`, `status`,
  `settlement_amount`, `tax_amount`, `paid_amount`, `unpaid_amount`,
  `dept_id`, `create_by`, `create_time`, `remark`, `del_flag`
)
SELECT '桥西区教育局', '中学校服补充采购项目', '2026-04', '2',
       73280.00, 4396.80, 73280.00, 0.00,
       NULL, 'admin', NOW(), '项目已完成结算，资金流水和付款凭证已归档。', '0'
WHERE NOT EXISTS (
  SELECT 1 FROM `xfpt_edu_settlement`
  WHERE `unit_name` = '桥西区教育局'
    AND `project_name` = '中学校服补充采购项目'
    AND `settlement_cycle` = '2026-04'
    AND `del_flag` = '0'
);

SET @new_feature_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `MENU_NAME` = '新增功能'
    AND `MENU_TYPE` = 'M'
  ORDER BY `MENU_ID` DESC
  LIMIT 1
);
SET @new_feature_menu_id := IFNULL(@new_feature_menu_id, 2333);

SET @edu_settlement_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:eduSettlementManage:list'
     OR (`PARENT_ID` = @new_feature_menu_id AND `PATH` = 'eduSettlementManage')
  LIMIT 1
);
SET @edu_settlement_menu_id := IFNULL(@edu_settlement_menu_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @edu_settlement_menu_id, '教育局结算管理', @new_feature_menu_id, 95,
       'eduSettlementManage', 'school/eduSettlementManage/index', NULL, '',
       1, 0, 'C', '0', '0', 'school:eduSettlementManage:list', 'money',
       'admin', NOW(), '', NULL, '教育局校服交易管理-结算管理', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu`
  WHERE `MENU_ID` = @edu_settlement_menu_id
);

UPDATE `sys_menu`
SET `MENU_NAME` = '教育局结算管理',
    `PARENT_ID` = @new_feature_menu_id,
    `ORDER_NUM` = 95,
    `PATH` = 'eduSettlementManage',
    `COMPONENT` = 'school/eduSettlementManage/index',
    `MENU_TYPE` = 'C',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `PERMS` = 'school:eduSettlementManage:list',
    `ICON` = 'money',
    `UPDATE_BY` = 'admin',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '教育局校服交易管理-结算管理'
WHERE `MENU_ID` = @edu_settlement_menu_id;

SET @edu_settlement_query_id := (
  SELECT `MENU_ID` FROM `sys_menu` WHERE `PERMS` = 'school:eduSettlementManage:query' LIMIT 1
);
SET @edu_settlement_query_id := IFNULL(@edu_settlement_query_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @edu_settlement_query_id, '教育局结算管理详情', @edu_settlement_menu_id, 1, '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:eduSettlementManage:query', '#',
       'admin', NOW(), '', NULL, '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:eduSettlementManage:query'
);

SET @edu_settlement_statistics_id := (
  SELECT `MENU_ID` FROM `sys_menu` WHERE `PERMS` = 'school:eduSettlementManage:statistics' LIMIT 1
);
SET @edu_settlement_statistics_id := IFNULL(@edu_settlement_statistics_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @edu_settlement_statistics_id, '教育局结算管理统计', @edu_settlement_menu_id, 2, '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:eduSettlementManage:statistics', '#',
       'admin', NOW(), '', NULL, '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:eduSettlementManage:statistics'
);

UPDATE `sys_menu`
SET `PARENT_ID` = @edu_settlement_menu_id,
    `UPDATE_BY` = 'admin',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` IN ('school:eduSettlementManage:query', 'school:eduSettlementManage:statistics');

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT r.`ROLE_ID`, m.`MENU_ID`
FROM `sys_role` r
JOIN `sys_menu` m
  ON m.`PERMS` IN (
    'school:eduSettlementManage:list',
    'school:eduSettlementManage:query',
    'school:eduSettlementManage:statistics'
  )
WHERE r.`ROLE_KEY` IN ('admin', 'gly')
  AND r.`DEL_FLAG` = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`ROLE_ID` = r.`ROLE_ID`
      AND rm.`MENU_ID` = m.`MENU_ID`
  );
