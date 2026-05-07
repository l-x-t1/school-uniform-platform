-- 账户余额页面菜单初始化脚本
-- 执行前确认“新增功能”目录 MENU_ID = 2333 存在
-- 页面组件路径：school/accountBalance/index
-- 页面复用已有的资金流水、提现申请接口权限，不额外新增按钮权限

INSERT INTO `sys_menu`
(`MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`, `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`, `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`)
SELECT
  2334, '账户余额', 2333, 2, 'accountBalance', 'school/accountBalance/index', NULL, '', 1, 0, 'C', '0', '0', 'school:financeFlow:list', 'money', 'admin', NOW(), 'admin', NOW(), '账户余额菜单', NULL
FROM dual
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu` WHERE `MENU_ID` = 2334 OR (`PARENT_ID` = 2333 AND `PATH` = 'accountBalance')
);
