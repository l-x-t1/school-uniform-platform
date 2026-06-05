-- 补充管理员侧边栏“新增功能”中前端硬挂但导出库缺失的 3 个菜单：
-- 1. 供应商入驻申请
-- 2. 供应商入驻审核
-- 3. 投票结果
SET @new_feature_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PATH` = 'newFeature'
    AND `MENU_TYPE` = 'M'
  ORDER BY `MENU_ID`
  LIMIT 1
);

SET @admin_role_id := 1;

SET @supplier_settle_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PATH` = 'supplierSettle'
     OR `COMPONENT` = 'supplier-settle/index'
  ORDER BY `MENU_ID` DESC
  LIMIT 1
);
SET @supplier_settle_menu_id := IFNULL(
  @supplier_settle_menu_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @supplier_settle_menu_id, '供应商入驻申请', @new_feature_menu_id, 130,
       'supplierSettle', 'supplier-settle/index', NULL, 'SupplierSettle',
       1, 0, 'C', '0', '0', 'school:supplierSettleApply:entry', 'form',
       'codex', NOW(), 'codex', NOW(), '供应商入驻申请菜单', NULL
WHERE @new_feature_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PATH` = 'supplierSettle'
       OR `COMPONENT` = 'supplier-settle/index'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '供应商入驻申请',
    `PARENT_ID` = @new_feature_menu_id,
    `ORDER_NUM` = 130,
    `PATH` = 'supplierSettle',
    `COMPONENT` = 'supplier-settle/index',
    `QUERY` = NULL,
    `ROUTE_NAME` = 'SupplierSettle',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'C',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `PERMS` = 'school:supplierSettleApply:entry',
    `ICON` = 'form',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '供应商入驻申请菜单',
    `TYPE` = NULL
WHERE `MENU_ID` = @supplier_settle_menu_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @supplier_settle_menu_id
FROM DUAL
WHERE @supplier_settle_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @supplier_settle_menu_id
  );

SET @supplier_settle_audit_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PATH` = 'supplierSettleAudit'
     OR `COMPONENT` = 'jiaoyuju/supplierSettleAudit/index'
  ORDER BY `MENU_ID` DESC
  LIMIT 1
);
SET @supplier_settle_audit_menu_id := IFNULL(
  @supplier_settle_audit_menu_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @supplier_settle_audit_menu_id, '供应商入驻审核', @new_feature_menu_id, 131,
       'supplierSettleAudit', 'jiaoyuju/supplierSettleAudit/index', NULL, 'SupplierSettleAudit',
       1, 0, 'C', '0', '0', 'school:supplierSettleAudit:list', 'edit',
       'codex', NOW(), 'codex', NOW(), '供应商入驻审核菜单', NULL
WHERE @new_feature_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PATH` = 'supplierSettleAudit'
       OR `COMPONENT` = 'jiaoyuju/supplierSettleAudit/index'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '供应商入驻审核',
    `PARENT_ID` = @new_feature_menu_id,
    `ORDER_NUM` = 131,
    `PATH` = 'supplierSettleAudit',
    `COMPONENT` = 'jiaoyuju/supplierSettleAudit/index',
    `QUERY` = NULL,
    `ROUTE_NAME` = 'SupplierSettleAudit',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'C',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `PERMS` = 'school:supplierSettleAudit:list',
    `ICON` = 'edit',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '供应商入驻审核菜单',
    `TYPE` = NULL
WHERE `MENU_ID` = @supplier_settle_audit_menu_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @supplier_settle_audit_menu_id
FROM DUAL
WHERE @supplier_settle_audit_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @supplier_settle_audit_menu_id
  );

SET @supplier_settle_audit_query_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:supplierSettleAudit:query'
  LIMIT 1
);
SET @supplier_settle_audit_query_id := IFNULL(
  @supplier_settle_audit_query_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @supplier_settle_audit_query_id, '供应商入驻审核查询', @supplier_settle_audit_menu_id, 1,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:supplierSettleAudit:query', '#',
       'codex', NOW(), 'codex', NOW(), '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:supplierSettleAudit:query'
);

UPDATE `sys_menu`
SET `MENU_NAME` = '供应商入驻审核查询',
    `PARENT_ID` = @supplier_settle_audit_menu_id,
    `ORDER_NUM` = 1,
    `PATH` = '#',
    `COMPONENT` = '',
    `QUERY` = NULL,
    `ROUTE_NAME` = '',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'F',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` = 'school:supplierSettleAudit:query';

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @supplier_settle_audit_query_id
FROM DUAL
WHERE @supplier_settle_audit_query_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @supplier_settle_audit_query_id
  );

SET @supplier_settle_audit_approve_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:supplierSettleAudit:approve'
  LIMIT 1
);
SET @supplier_settle_audit_approve_id := IFNULL(
  @supplier_settle_audit_approve_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @supplier_settle_audit_approve_id, '供应商入驻审核通过', @supplier_settle_audit_menu_id, 2,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:supplierSettleAudit:approve', '#',
       'codex', NOW(), 'codex', NOW(), '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:supplierSettleAudit:approve'
);

UPDATE `sys_menu`
SET `MENU_NAME` = '供应商入驻审核通过',
    `PARENT_ID` = @supplier_settle_audit_menu_id,
    `ORDER_NUM` = 2,
    `PATH` = '#',
    `COMPONENT` = '',
    `QUERY` = NULL,
    `ROUTE_NAME` = '',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'F',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` = 'school:supplierSettleAudit:approve';

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @supplier_settle_audit_approve_id
FROM DUAL
WHERE @supplier_settle_audit_approve_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @supplier_settle_audit_approve_id
  );

SET @supplier_settle_audit_reject_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:supplierSettleAudit:reject'
  LIMIT 1
);
SET @supplier_settle_audit_reject_id := IFNULL(
  @supplier_settle_audit_reject_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @supplier_settle_audit_reject_id, '供应商入驻审核驳回', @supplier_settle_audit_menu_id, 3,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:supplierSettleAudit:reject', '#',
       'codex', NOW(), 'codex', NOW(), '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:supplierSettleAudit:reject'
);

UPDATE `sys_menu`
SET `MENU_NAME` = '供应商入驻审核驳回',
    `PARENT_ID` = @supplier_settle_audit_menu_id,
    `ORDER_NUM` = 3,
    `PATH` = '#',
    `COMPONENT` = '',
    `QUERY` = NULL,
    `ROUTE_NAME` = '',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'F',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` = 'school:supplierSettleAudit:reject';

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @supplier_settle_audit_reject_id
FROM DUAL
WHERE @supplier_settle_audit_reject_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @supplier_settle_audit_reject_id
  );

SET @supplier_settle_audit_export_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:supplierSettleAudit:export'
  LIMIT 1
);
SET @supplier_settle_audit_export_id := IFNULL(
  @supplier_settle_audit_export_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @supplier_settle_audit_export_id, '供应商入驻审核导出', @supplier_settle_audit_menu_id, 4,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:supplierSettleAudit:export', '#',
       'codex', NOW(), 'codex', NOW(), '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:supplierSettleAudit:export'
);

UPDATE `sys_menu`
SET `MENU_NAME` = '供应商入驻审核导出',
    `PARENT_ID` = @supplier_settle_audit_menu_id,
    `ORDER_NUM` = 4,
    `PATH` = '#',
    `COMPONENT` = '',
    `QUERY` = NULL,
    `ROUTE_NAME` = '',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'F',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` = 'school:supplierSettleAudit:export';

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @supplier_settle_audit_export_id
FROM DUAL
WHERE @supplier_settle_audit_export_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @supplier_settle_audit_export_id
  );

SET @vote_result_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PATH` = 'voteResult'
     OR `COMPONENT` = 'school/voteResult/index'
  ORDER BY `MENU_ID` DESC
  LIMIT 1
);
SET @vote_result_menu_id := IFNULL(
  @vote_result_menu_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @vote_result_menu_id, '投票结果', @new_feature_menu_id, 132,
       'voteResult', 'school/voteResult/index', NULL, 'VoteResult',
       1, 0, 'C', '0', '0', 'school:voteResult:list', 'form',
       'codex', NOW(), 'codex', NOW(), '投票结果菜单', NULL
WHERE @new_feature_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PATH` = 'voteResult'
       OR `COMPONENT` = 'school/voteResult/index'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '投票结果',
    `PARENT_ID` = @new_feature_menu_id,
    `ORDER_NUM` = 132,
    `PATH` = 'voteResult',
    `COMPONENT` = 'school/voteResult/index',
    `QUERY` = NULL,
    `ROUTE_NAME` = 'VoteResult',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'C',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `PERMS` = 'school:voteResult:list',
    `ICON` = 'form',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '投票结果菜单',
    `TYPE` = NULL
WHERE `MENU_ID` = @vote_result_menu_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @vote_result_menu_id
FROM DUAL
WHERE @vote_result_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @vote_result_menu_id
  );

SET @vote_result_query_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:voteResult:query'
  LIMIT 1
);
SET @vote_result_query_id := IFNULL(
  @vote_result_query_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @vote_result_query_id, '投票结果查询', @vote_result_menu_id, 1,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:voteResult:query', '#',
       'codex', NOW(), 'codex', NOW(), '', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `PERMS` = 'school:voteResult:query'
);

UPDATE `sys_menu`
SET `MENU_NAME` = '投票结果查询',
    `PARENT_ID` = @vote_result_menu_id,
    `ORDER_NUM` = 1,
    `PATH` = '#',
    `COMPONENT` = '',
    `QUERY` = NULL,
    `ROUTE_NAME` = '',
    `IS_FRAME` = 1,
    `IS_CACHE` = 0,
    `MENU_TYPE` = 'F',
    `VISIBLE` = '0',
    `STATUS` = '0',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` = 'school:voteResult:query';

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @admin_role_id, @vote_result_query_id
FROM DUAL
WHERE @vote_result_query_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @admin_role_id
      AND `MENU_ID` = @vote_result_query_id
  );

-- Dump completed on 2026-05-04 19:18:33
