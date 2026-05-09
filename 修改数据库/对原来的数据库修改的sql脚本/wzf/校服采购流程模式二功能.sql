-- 校服采购流程：选用组织自主确定企业模式（二）功能权限
-- 一级模板：校服采购流程（menu_id = 2021）

INSERT INTO `sys_menu` (`MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`, `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`) VALUES
(3060, '选用组织现场投票确定企业列表', 2021, 41, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'xfpt:mse-scene-list', '#', 'admin', NOW(), '', NULL, '', NULL),
(3061, '选用组织线上投票确定企业列表', 2021, 42, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'xfpt:mse-online-list', '#', 'admin', NOW(), '', NULL, '', NULL),
(3062, '发布选购信息', 2021, 43, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'xfpt:mse-add', '#', 'admin', NOW(), '', NULL, '发布公告后公告详情页面有企业报名入口，企业可以进行报名需要修改功能', NULL)
ON DUPLICATE KEY UPDATE
  `MENU_NAME` = VALUES(`MENU_NAME`),
  `PARENT_ID` = VALUES(`PARENT_ID`),
  `ORDER_NUM` = VALUES(`ORDER_NUM`),
  `PATH` = VALUES(`PATH`),
  `COMPONENT` = VALUES(`COMPONENT`),
  `MENU_TYPE` = VALUES(`MENU_TYPE`),
  `VISIBLE` = VALUES(`VISIBLE`),
  `STATUS` = VALUES(`STATUS`),
  `PERMS` = VALUES(`PERMS`),
  `ICON` = VALUES(`ICON`),
  `UPDATE_BY` = 'admin',
  `UPDATE_TIME` = NOW(),
  `REMARK` = VALUES(`REMARK`),
  `TYPE` = VALUES(`TYPE`);

INSERT IGNORE INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT role_id, menu_id
FROM (
  SELECT DISTINCT role_id
  FROM `sys_role_menu`
  WHERE menu_id = 2021
    AND role_id <> 0
) roles
CROSS JOIN (
  SELECT 3060 AS menu_id
  UNION ALL SELECT 3061
  UNION ALL SELECT 3062
) menus;
