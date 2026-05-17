-- 服务学校列表菜单
-- 位置：新增功能 -> 服务学校列表
-- 前端组件：src/views/serviceSchool/index.vue

INSERT INTO sys_menu (
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  query,
  route_name,
  is_frame,
  is_cache,
  menu_type,
  visible,
  status,
  perms,
  icon,
  create_by,
  create_time,
  remark
)
SELECT
  '服务学校列表',
  parent_menu.menu_id,
  99,
  'serviceSchool',
  'serviceSchool/index',
  NULL,
  'serviceSchool',
  1,
  0,
  'C',
  '0',
  '0',
  '',
  'education',
  'admin',
  NOW(),
  '服务学校列表菜单'
FROM (
  SELECT menu_id
  FROM sys_menu
  WHERE menu_name = '新增功能'
    AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
) parent_menu
WHERE NOT EXISTS (
  SELECT 1
  FROM (
    SELECT menu_id
    FROM sys_menu
    WHERE path = 'serviceSchool'
       OR component = 'serviceSchool/index'
  ) exists_menu
);
