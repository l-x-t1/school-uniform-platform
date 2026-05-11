-- 学校使用情况页面菜单初始化脚本
-- 执行前确认“新增功能”目录 MENU_ID = 2333 存在
INSERT INTO `sys_menu`
(`MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`, `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`)
SELECT
  2336, '学校使用情况', 2333, 4, 'schoolUsage', 'school/schoolUsage/index', NULL, '', 1, 0, 'C', '0', '0', 'school:schoolUsage:list', 'school', 'admin', NOW(), 'admin', NOW(), '学校使用情况菜单', NULL
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `MENU_ID` = 2336 OR (`PARENT_ID` = 2333 AND `PATH` = 'schoolUsage')
);
