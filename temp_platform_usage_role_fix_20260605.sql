SET @city_edu_role_id := (
  SELECT `ROLE_ID`
  FROM `sys_role`
  WHERE `ROLE_KEY` = 'sjyj'
  ORDER BY `ROLE_ID`
  LIMIT 1
);

SET @platform_usage_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:platformUsage:list'
     OR `PATH` = 'platformUsage'
     OR `COMPONENT` = 'school/platformUsage/index'
  ORDER BY `MENU_ID`
  LIMIT 1
);

SET @platform_usage_finance_perm_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PARENT_ID` = @platform_usage_menu_id
    AND `MENU_TYPE` = 'F'
    AND `PERMS` = 'school:financeFlow:list'
  ORDER BY `MENU_ID`
  LIMIT 1
);
SET @platform_usage_finance_perm_id := IFNULL(
  @platform_usage_finance_perm_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @platform_usage_finance_perm_id, '监管统计-资金流水查询', @platform_usage_menu_id, 1,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:financeFlow:list', '#',
       'codex', NOW(), 'codex', NOW(), '给市教育局放行监管统计页面的资金流水接口', NULL
WHERE @platform_usage_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PARENT_ID` = @platform_usage_menu_id
      AND `MENU_TYPE` = 'F'
      AND `PERMS` = 'school:financeFlow:list'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '监管统计-资金流水查询',
    `PARENT_ID` = @platform_usage_menu_id,
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
    `PERMS` = 'school:financeFlow:list',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '给市教育局放行监管统计页面的资金流水接口',
    `TYPE` = NULL
WHERE `MENU_ID` = @platform_usage_finance_perm_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @city_edu_role_id, @platform_usage_finance_perm_id
FROM DUAL
WHERE @city_edu_role_id IS NOT NULL
  AND @platform_usage_finance_perm_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @city_edu_role_id
      AND `MENU_ID` = @platform_usage_finance_perm_id
  );

SET @platform_usage_settlement_perm_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PARENT_ID` = @platform_usage_menu_id
    AND `MENU_TYPE` = 'F'
    AND `PERMS` = 'school:eduSettlementManage:list'
  ORDER BY `MENU_ID`
  LIMIT 1
);
SET @platform_usage_settlement_perm_id := IFNULL(
  @platform_usage_settlement_perm_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @platform_usage_settlement_perm_id, '监管统计-结算管理查询', @platform_usage_menu_id, 2,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:eduSettlementManage:list', '#',
       'codex', NOW(), 'codex', NOW(), '给市教育局放行监管统计页面的结算管理接口', NULL
WHERE @platform_usage_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PARENT_ID` = @platform_usage_menu_id
      AND `MENU_TYPE` = 'F'
      AND `PERMS` = 'school:eduSettlementManage:list'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '监管统计-结算管理查询',
    `PARENT_ID` = @platform_usage_menu_id,
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
    `PERMS` = 'school:eduSettlementManage:list',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '给市教育局放行监管统计页面的结算管理接口',
    `TYPE` = NULL
WHERE `MENU_ID` = @platform_usage_settlement_perm_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @city_edu_role_id, @platform_usage_settlement_perm_id
FROM DUAL
WHERE @city_edu_role_id IS NOT NULL
  AND @platform_usage_settlement_perm_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @city_edu_role_id
      AND `MENU_ID` = @platform_usage_settlement_perm_id
  );

SET @platform_usage_order_info_perm_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PARENT_ID` = @platform_usage_menu_id
    AND `MENU_TYPE` = 'F'
    AND `PERMS` = 'school:orderInfo:list'
  ORDER BY `MENU_ID`
  LIMIT 1
);
SET @platform_usage_order_info_perm_id := IFNULL(
  @platform_usage_order_info_perm_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @platform_usage_order_info_perm_id, '监管统计-订单信息查询', @platform_usage_menu_id, 3,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:orderInfo:list', '#',
       'codex', NOW(), 'codex', NOW(), '给市教育局放行监管统计页面的订单信息接口', NULL
WHERE @platform_usage_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PARENT_ID` = @platform_usage_menu_id
      AND `MENU_TYPE` = 'F'
      AND `PERMS` = 'school:orderInfo:list'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '监管统计-订单信息查询',
    `PARENT_ID` = @platform_usage_menu_id,
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
    `PERMS` = 'school:orderInfo:list',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '给市教育局放行监管统计页面的订单信息接口',
    `TYPE` = NULL
WHERE `MENU_ID` = @platform_usage_order_info_perm_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @city_edu_role_id, @platform_usage_order_info_perm_id
FROM DUAL
WHERE @city_edu_role_id IS NOT NULL
  AND @platform_usage_order_info_perm_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @city_edu_role_id
      AND `MENU_ID` = @platform_usage_order_info_perm_id
  );

SET @platform_usage_order_item_perm_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PARENT_ID` = @platform_usage_menu_id
    AND `MENU_TYPE` = 'F'
    AND `PERMS` = 'school:orderItem:list'
  ORDER BY `MENU_ID`
  LIMIT 1
);
SET @platform_usage_order_item_perm_id := IFNULL(
  @platform_usage_order_item_perm_id,
  (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`)
);

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @platform_usage_order_item_perm_id, '监管统计-订单明细查询', @platform_usage_menu_id, 4,
       '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:orderItem:list', '#',
       'codex', NOW(), 'codex', NOW(), '给市教育局放行监管统计页面的订单明细接口', NULL
WHERE @platform_usage_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_menu`
    WHERE `PARENT_ID` = @platform_usage_menu_id
      AND `MENU_TYPE` = 'F'
      AND `PERMS` = 'school:orderItem:list'
  );

UPDATE `sys_menu`
SET `MENU_NAME` = '监管统计-订单明细查询',
    `PARENT_ID` = @platform_usage_menu_id,
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
    `PERMS` = 'school:orderItem:list',
    `ICON` = '#',
    `UPDATE_BY` = 'codex',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '给市教育局放行监管统计页面的订单明细接口',
    `TYPE` = NULL
WHERE `MENU_ID` = @platform_usage_order_item_perm_id;

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT @city_edu_role_id, @platform_usage_order_item_perm_id
FROM DUAL
WHERE @city_edu_role_id IS NOT NULL
  AND @platform_usage_order_item_perm_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu`
    WHERE `ROLE_ID` = @city_edu_role_id
      AND `MENU_ID` = @platform_usage_order_item_perm_id
  );
