-- Add education bureau settlement management menu under "新增功能".
-- This only adds a frontend menu entry and does not change existing business data.

INSERT INTO sys_menu (
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  query,
  is_frame,
  is_cache,
  menu_type,
  visible,
  status,
  perms,
  icon,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT
  3061,
  '教育局结算管理',
  2333,
  2,
  'eduSettlementManage',
  'school/eduSettlementManage/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'school:eduSettlementManage:list',
  'money',
  'system',
  NOW(),
  NULL,
  NULL,
  '教育局校服交易管理-结算管理'
WHERE NOT EXISTS (
  SELECT 1
  FROM sys_menu
  WHERE menu_id = 3061
     OR (parent_id = 2333 AND path = 'eduSettlementManage')
);
