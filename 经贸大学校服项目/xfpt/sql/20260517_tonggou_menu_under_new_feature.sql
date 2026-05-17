-- 统购管理菜单层级调整（安全版）
-- 目标：新增功能 -> 统购管理 -> 原有统购管理子菜单
-- 说明：
-- 1. 不手动指定 menu_id，不使用 MAX(menu_id)+1。
-- 2. 若“新增功能”不存在，则插入时由 sys_menu.menu_id 自增生成。
-- 3. 不修改“统购管理”及其子菜单的 path/component/perms/接口/页面。
-- 4. 若当前“统购管理”的 path 已经是 /tonggou，本脚本会保持 /tonggou 不变。
-- 5. 若当前 path 不是 /tonggou，本脚本会中止，避免在不改 path 的前提下改变 /tonggou/xxx 路由。

-- =========================================================
-- 一、执行前检查 SQL
-- =========================================================

-- 1. 查看“新增功能”和“统购管理”当前记录。
SELECT
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  menu_type,
  visible,
  status,
  perms,
  icon
FROM sys_menu
WHERE (menu_name = '新增功能' AND menu_type = 'M')
   OR (menu_name = '统购管理' AND menu_type = 'M')
ORDER BY parent_id, order_num, menu_id;

-- 2. 查看“统购管理”及其下级菜单当前结构。
SELECT
  parent_menu.menu_id AS parent_menu_id,
  parent_menu.menu_name AS parent_menu_name,
  parent_menu.parent_id AS parent_parent_id,
  parent_menu.path AS parent_path,
  child_menu.menu_id AS child_menu_id,
  child_menu.menu_name AS child_menu_name,
  child_menu.parent_id AS child_parent_id,
  child_menu.order_num AS child_order_num,
  child_menu.path AS child_path,
  child_menu.component AS child_component,
  child_menu.perms AS child_perms
FROM sys_menu parent_menu
LEFT JOIN sys_menu child_menu
  ON child_menu.parent_id = parent_menu.menu_id
WHERE parent_menu.menu_name = '统购管理'
  AND parent_menu.menu_type = 'M'
ORDER BY parent_menu.menu_id, child_menu.order_num, child_menu.menu_id;

-- 3. 查看目标菜单完整树形关系。
WITH RECURSIVE menu_tree AS (
  SELECT
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    menu_type,
    perms,
    CAST(menu_name AS CHAR(1000)) AS tree_path,
    0 AS level_no
  FROM sys_menu
  WHERE (menu_name = '新增功能' AND menu_type = 'M')
     OR (menu_name = '统购管理' AND menu_type = 'M')

  UNION ALL

  SELECT
    child.menu_id,
    child.menu_name,
    child.parent_id,
    child.order_num,
    child.path,
    child.component,
    child.menu_type,
    child.perms,
    CONCAT(menu_tree.tree_path, ' -> ', child.menu_name) AS tree_path,
    menu_tree.level_no + 1 AS level_no
  FROM sys_menu child
  JOIN menu_tree
    ON child.parent_id = menu_tree.menu_id
)
SELECT
  level_no,
  tree_path,
  menu_id,
  menu_name,
  parent_id,
  order_num,
  path,
  component,
  menu_type,
  perms
FROM menu_tree
ORDER BY tree_path, level_no, order_num, menu_id;

-- =========================================================
-- 二、正向执行 SQL
-- =========================================================

START TRANSACTION;

SET @new_feature_menu_id := NULL;
SET @tonggou_menu_id := NULL;
SET @tonggou_path := NULL;

-- 1. 先查已有“新增功能”目录。
SELECT menu_id
INTO @new_feature_menu_id
FROM sys_menu
WHERE menu_name = '新增功能'
  AND menu_type = 'M'
ORDER BY parent_id, order_num, menu_id
LIMIT 1;

-- 2. 不存在则插入，让 menu_id 自增生成。
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
  update_by,
  update_time,
  remark
)
SELECT
  '新增功能',
  0,
  99,
  'newFeature',
  NULL,
  NULL,
  '',
  1,
  0,
  'M',
  '0',
  '0',
  '',
  'tree',
  'admin',
  NOW(),
  '',
  NULL,
  '新增功能目录'
WHERE @new_feature_menu_id IS NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE menu_name = '新增功能'
      AND menu_type = 'M'
  );

-- 3. 插入后重新获取“新增功能”的 menu_id。
SET @new_feature_menu_id := NULL;

SELECT menu_id
INTO @new_feature_menu_id
FROM sys_menu
WHERE menu_name = '新增功能'
  AND menu_type = 'M'
ORDER BY parent_id, order_num, menu_id
LIMIT 1;

-- 4. 获取已有“统购管理”目录，不创建、不改 path/component/perms。
SELECT menu_id, path
INTO @tonggou_menu_id, @tonggou_path
FROM sys_menu
WHERE menu_name = '统购管理'
  AND menu_type = 'M'
ORDER BY parent_id, order_num, menu_id
LIMIT 1;

-- 5. 必要保护：目标父菜单或统购菜单不存在时终止。
SET @missing_menu_message := CASE
  WHEN @new_feature_menu_id IS NULL THEN '未找到或未能创建“新增功能”菜单'
  WHEN @tonggou_menu_id IS NULL THEN '未找到“统购管理”菜单'
  WHEN @tonggou_path <> '/tonggou' THEN '当前“统购管理”path不是/tonggou；在不修改path的前提下无法保证/tonggou/xxx路由不变'
  ELSE NULL
END;

SET @raise_sql := IF(
  @missing_menu_message IS NULL,
  'SELECT 1',
  CONCAT('SIGNAL SQLSTATE ''45000'' SET MESSAGE_TEXT = ''', @missing_menu_message, '''')
);

PREPARE raise_stmt FROM @raise_sql;
EXECUTE raise_stmt;
DEALLOCATE PREPARE raise_stmt;

-- 6. 只调整“统购管理”的 parent_id；不修改 path/component/perms。
UPDATE sys_menu
SET parent_id = @new_feature_menu_id,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @tonggou_menu_id
  AND menu_name = '统购管理'
  AND menu_type = 'M'
  AND parent_id <> @new_feature_menu_id;

-- 7. 已拥有“统购管理”的角色，补上“新增功能”父级菜单权限，避免左侧树缺父节点。
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @new_feature_menu_id
FROM sys_role_menu rm
WHERE rm.menu_id = @tonggou_menu_id
  AND rm.role_id <> 0
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu exists_rm
    WHERE exists_rm.role_id = rm.role_id
      AND exists_rm.menu_id = @new_feature_menu_id
  );

COMMIT;

-- =========================================================
-- 三、执行后检查 SQL
-- =========================================================

SELECT
  nf.menu_id AS new_feature_menu_id,
  nf.menu_name AS new_feature_menu_name,
  nf.parent_id AS new_feature_parent_id,
  tg.menu_id AS tonggou_menu_id,
  tg.menu_name AS tonggou_menu_name,
  tg.parent_id AS tonggou_parent_id,
  tg.path AS tonggou_path,
  tg.component AS tonggou_component,
  tg.perms AS tonggou_perms
FROM sys_menu nf
JOIN sys_menu tg
  ON tg.parent_id = nf.menu_id
WHERE nf.menu_name = '新增功能'
  AND nf.menu_type = 'M'
  AND tg.menu_name = '统购管理'
  AND tg.menu_type = 'M';

SELECT
  parent_menu.menu_name AS parent_menu_name,
  child_menu.menu_id,
  child_menu.menu_name,
  child_menu.parent_id,
  child_menu.order_num,
  child_menu.path,
  child_menu.component,
  child_menu.perms
FROM sys_menu parent_menu
JOIN sys_menu child_menu
  ON child_menu.parent_id = parent_menu.menu_id
WHERE parent_menu.menu_name = '统购管理'
  AND parent_menu.menu_type = 'M'
ORDER BY child_menu.order_num, child_menu.menu_id;

-- 回滚脚本请单独执行：
-- 经贸大学校服项目/xfpt/sql/20260517_tonggou_menu_under_new_feature_rollback.sql
