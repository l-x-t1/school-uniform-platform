-- 统购管理菜单层级调整回滚 SQL
-- 说明：
-- 1. 按当前需求描述的原始结构，回滚为“统购管理”顶级菜单 parent_id = 0。
-- 2. 不删除“新增功能”，避免影响已经挂在“新增功能”下的其他菜单。
-- 3. 不删除 sys_role_menu 中“新增功能”的授权，避免影响其他新增功能菜单可见性。
-- 4. 不修改“统购管理”的 path/component/perms/接口/页面。

-- 回滚前检查。
SELECT
  nf.menu_id AS new_feature_menu_id,
  nf.menu_name AS new_feature_menu_name,
  tg.menu_id AS tonggou_menu_id,
  tg.menu_name AS tonggou_menu_name,
  tg.parent_id AS tonggou_parent_id,
  tg.path AS tonggou_path,
  tg.component AS tonggou_component,
  tg.perms AS tonggou_perms
FROM sys_menu tg
LEFT JOIN sys_menu nf
  ON nf.menu_id = tg.parent_id
WHERE tg.menu_name = '统购管理'
  AND tg.menu_type = 'M'
ORDER BY tg.menu_id;

START TRANSACTION;

SET @rollback_tonggou_menu_id := NULL;

SELECT menu_id
INTO @rollback_tonggou_menu_id
FROM sys_menu
WHERE menu_name = '统购管理'
  AND menu_type = 'M'
ORDER BY parent_id, order_num, menu_id
LIMIT 1;

UPDATE sys_menu
SET parent_id = 0,
    update_by = 'admin',
    update_time = NOW()
WHERE @rollback_tonggou_menu_id IS NOT NULL
  AND menu_id = @rollback_tonggou_menu_id
  AND menu_name = '统购管理'
  AND menu_type = 'M'
  AND parent_id <> 0;

COMMIT;

-- 回滚后检查。
SELECT
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  menu_type,
  perms
FROM sys_menu
WHERE menu_name = '统购管理'
  AND menu_type = 'M'
ORDER BY menu_id;
