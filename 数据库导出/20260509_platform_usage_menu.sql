-- 平台使用情况页面菜单初始化脚本
-- 执行前确认“新增功能”目录 MENU_ID = 2333 存在
-- 页面组件路径：school/platformUsage/index
INSERT INTO `sys_menu`
(`MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`, `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`)
SELECT
  2335, '平台使用情况', 2333, 3, 'platformUsage', 'school/platformUsage/index', NULL, '', 1, 0, 'C', '0', '0', 'school:platformUsage:list', 'pie-chart', 'admin', NOW(), 'admin', NOW(), '平台使用情况菜单', NULL
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `MENU_ID` = 2335 OR (`PARENT_ID` = 2333 AND `PATH` = 'platformUsage')
);
