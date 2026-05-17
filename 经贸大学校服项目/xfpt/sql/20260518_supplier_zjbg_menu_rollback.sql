-- 回滚：供应商质检报告管理菜单调整
-- 目标：
-- 1. 删除“新增功能”下面的“供应商质检报告管理”及其功能权限。
-- 2. 删除对应 sys_role_menu 授权。
-- 3. 保留“统购管理”下面的“质检报告管理”及其原有功能。

SET NAMES utf8mb4;

-- 回滚前检查
SELECT
  menu_id, menu_name, parent_id, order_num, path, component,
  menu_type, perms, visible, status, icon, remark
FROM sys_menu
WHERE menu_name IN ('新增功能', '统购管理', '质检报告管理', '供应商质检报告管理')
   OR perms IN ('school:zjbg:list', 'school:zjbg:query', 'school:zjbg:add', 'school:zjbg:upload')
ORDER BY parent_id, order_num, menu_id;

START TRANSACTION;

SET @new_feature_menu_id := NULL;
SET @tonggou_menu_id := NULL;
SET @school_zjbg_menu_id := NULL;
SET @supplier_zjbg_menu_id := NULL;

SELECT menu_id
INTO @new_feature_menu_id
FROM sys_menu
WHERE menu_name = '新增功能'
  AND menu_type = 'M'
  AND path = 'newFeature'
ORDER BY menu_id
LIMIT 1;

SELECT menu_id
INTO @tonggou_menu_id
FROM sys_menu
WHERE menu_name = '统购管理'
  AND menu_type = 'M'
ORDER BY menu_id
LIMIT 1;

SELECT menu_id
INTO @school_zjbg_menu_id
FROM sys_menu
WHERE menu_name = '质检报告管理'
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list'
ORDER BY menu_id
LIMIT 1;

SELECT menu_id
INTO @supplier_zjbg_menu_id
FROM sys_menu
WHERE menu_name = '供应商质检报告管理'
  AND menu_type = 'C'
ORDER BY menu_id
LIMIT 1;

-- 确保学校端菜单仍保留在“统购管理”下。
UPDATE sys_menu
SET parent_id = @tonggou_menu_id,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @school_zjbg_menu_id
  AND menu_name = '质检报告管理'
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list'
  AND @tonggou_menu_id IS NOT NULL
  AND parent_id <> @tonggou_menu_id;

-- 删除供应商菜单及其子权限的角色授权。
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m
  ON rm.menu_id = m.menu_id
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND (
    m.menu_id = @supplier_zjbg_menu_id
    OR m.parent_id = @supplier_zjbg_menu_id
  )
  AND (
    m.menu_name = '供应商质检报告管理'
    OR m.perms IN ('school:zjbg:list', 'school:zjbg:query', 'school:zjbg:add', 'school:zjbg:upload')
  );

-- 删除“供应商质检报告管理”下面的功能权限。
DELETE FROM sys_menu
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND parent_id = @supplier_zjbg_menu_id
  AND menu_type = 'F'
  AND perms IN ('school:zjbg:list', 'school:zjbg:query', 'school:zjbg:add', 'school:zjbg:upload');

-- 删除“供应商质检报告管理”菜单。
DELETE FROM sys_menu
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND menu_id = @supplier_zjbg_menu_id
  AND menu_name = '供应商质检报告管理'
  AND menu_type = 'C';

-- 如果“新增功能”是本脚本创建且已无子菜单，则清理该目录。
DELETE FROM sys_menu
WHERE @new_feature_menu_id IS NOT NULL
  AND menu_id = @new_feature_menu_id
  AND menu_name = '新增功能'
  AND menu_type = 'M'
  AND path = 'newFeature'
  AND remark = '新增功能目录'
  AND NOT EXISTS (
    SELECT 1
    FROM (
      SELECT child.menu_id
      FROM sys_menu child
      WHERE child.parent_id = @new_feature_menu_id
      LIMIT 1
    ) remain_child
  );

COMMIT;

-- 回滚后检查
SELECT
  menu_id, menu_name, parent_id, order_num, path, component,
  menu_type, perms, visible, status, icon, remark
FROM sys_menu
WHERE menu_name IN ('新增功能', '统购管理', '质检报告管理', '供应商质检报告管理')
   OR perms IN ('school:zjbg:list', 'school:zjbg:query', 'school:zjbg:add', 'school:zjbg:upload')
ORDER BY parent_id, order_num, menu_id;
