-- 供应商质检报告管理菜单调整
-- 目标：
-- 1. 保留“统购管理”下面的“质检报告管理”，给学校查看使用。
-- 2. 在“新增功能”下面新增“供应商质检报告管理”，复用学校端 path/component/perms。
-- 3. 仅供应商菜单具备新增、上传功能权限。

SET NAMES utf8mb4;

-- 执行前检查
SELECT
  menu_id, menu_name, parent_id, order_num, path, component,
  menu_type, perms, visible, status, icon
FROM sys_menu
WHERE menu_name IN ('新增功能', '统购管理', '质检报告管理', '供应商质检报告管理')
   OR perms IN ('school:zjbg:list', 'school:zjbg:query', 'school:zjbg:add', 'school:zjbg:upload')
ORDER BY parent_id, order_num, menu_id;

START TRANSACTION;

SET @new_feature_menu_id := NULL;
SET @new_feature_path := NULL;
SET @tonggou_menu_id := NULL;
SET @school_zjbg_menu_id := NULL;
SET @supplier_zjbg_menu_id := NULL;
SET @zjbg_path := NULL;
SET @zjbg_component := NULL;
SET @zjbg_perms := NULL;
SET @zjbg_icon := NULL;

-- 查找“新增功能”，不存在则插入。
SELECT menu_id, path
INTO @new_feature_menu_id, @new_feature_path
FROM sys_menu
WHERE menu_name = '新增功能'
  AND menu_type = 'M'
ORDER BY menu_id
LIMIT 1;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '新增功能', 0, 99, 'newFeature', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'tree',
  'admin', NOW(), '', NULL, '新增功能目录'
WHERE @new_feature_menu_id IS NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE menu_name = '新增功能'
      AND menu_type = 'M'
  );

SELECT menu_id, path
INTO @new_feature_menu_id, @new_feature_path
FROM sys_menu
WHERE menu_name = '新增功能'
  AND menu_type = 'M'
ORDER BY menu_id
LIMIT 1;

-- 安全保护：如果“新增功能”path 不正确，则中止执行。
SET @abort_message := CASE
  WHEN @new_feature_menu_id IS NULL THEN '新增功能菜单不存在且创建失败'
  WHEN IFNULL(@new_feature_path, '') <> 'newFeature' THEN '新增功能 path 不正确，正向 SQL 已中止'
  ELSE NULL
END;

SET @guard_sql := IF(
  @abort_message IS NULL,
  'SELECT 1',
  CONCAT('SIGNAL SQLSTATE ''45000'' SET MESSAGE_TEXT = ''', @abort_message, '''')
);

PREPARE guard_stmt FROM @guard_sql;
EXECUTE guard_stmt;
DEALLOCATE PREPARE guard_stmt;

-- 查找“统购管理”，不存在则插入。
SELECT menu_id
INTO @tonggou_menu_id
FROM sys_menu
WHERE menu_name = '统购管理'
  AND menu_type = 'M'
ORDER BY menu_id
LIMIT 1;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '统购管理', 0, 2, 'tonggou', NULL, NULL, '',
  1, 0, 'M', '0', '0', '', 'shopping',
  'admin', NOW(), '', NULL, '统购管理目录'
WHERE @tonggou_menu_id IS NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE menu_name = '统购管理'
      AND menu_type = 'M'
  );

SELECT menu_id
INTO @tonggou_menu_id
FROM sys_menu
WHERE menu_name = '统购管理'
  AND menu_type = 'M'
ORDER BY menu_id
LIMIT 1;

-- 查找学校端“质检报告管理”，不存在则插入基础菜单。
SELECT menu_id
INTO @school_zjbg_menu_id
FROM sys_menu
WHERE menu_name = '质检报告管理'
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list'
ORDER BY menu_id
LIMIT 1;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '质检报告管理', @tonggou_menu_id, 3, 'zjbg', 'zjbg/index', NULL, '',
  1, 0, 'C', '0', '0', 'school:zjbg:list', 'pdf',
  'admin', NOW(), '', NULL, '学校端质检报告管理'
WHERE @school_zjbg_menu_id IS NULL
  AND @tonggou_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE menu_name = '质检报告管理'
      AND menu_type = 'C'
      AND perms = 'school:zjbg:list'
  );

SELECT menu_id
INTO @school_zjbg_menu_id
FROM sys_menu
WHERE menu_name = '质检报告管理'
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list'
ORDER BY menu_id
LIMIT 1;

-- 明确保证学校端菜单仍在“统购管理”下，不修改 path/component/perms。
UPDATE sys_menu
SET parent_id = @tonggou_menu_id,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @school_zjbg_menu_id
  AND menu_name = '质检报告管理'
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list'
  AND parent_id <> @tonggou_menu_id;

-- 获取学校端原有 path/component/perms/icon，用于供应商菜单复用。
SELECT path, component, perms, icon
INTO @zjbg_path, @zjbg_component, @zjbg_perms, @zjbg_icon
FROM sys_menu
WHERE menu_id = @school_zjbg_menu_id
  AND menu_name = '质检报告管理'
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list'
LIMIT 1;

-- 查找或插入供应商菜单。
SELECT menu_id
INTO @supplier_zjbg_menu_id
FROM sys_menu
WHERE menu_name = '供应商质检报告管理'
  AND menu_type = 'C'
ORDER BY menu_id
LIMIT 1;

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '供应商质检报告管理', @new_feature_menu_id, 30, @zjbg_path, @zjbg_component, NULL, '',
  1, 0, 'C', '0', '0', @zjbg_perms, @zjbg_icon,
  'admin', NOW(), '', NULL, '供应商质检报告管理'
WHERE @supplier_zjbg_menu_id IS NULL
  AND @new_feature_menu_id IS NOT NULL
  AND @school_zjbg_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE menu_name = '供应商质检报告管理'
      AND menu_type = 'C'
  );

SELECT menu_id
INTO @supplier_zjbg_menu_id
FROM sys_menu
WHERE menu_name = '供应商质检报告管理'
  AND menu_type = 'C'
ORDER BY menu_id
LIMIT 1;

-- 明确将供应商菜单挂到“新增功能”下，并复用学校端 path/component/perms/icon。
UPDATE sys_menu
SET parent_id = @new_feature_menu_id,
    path = @zjbg_path,
    component = @zjbg_component,
    perms = @zjbg_perms,
    icon = @zjbg_icon,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @supplier_zjbg_menu_id
  AND menu_name = '供应商质检报告管理'
  AND menu_type = 'C';

-- 供应商菜单：列表、详情、新增、上传权限。
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '列表', @supplier_zjbg_menu_id, 1, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'school:zjbg:list', '#',
  'admin', NOW(), '', NULL, '供应商质检报告列表'
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = @supplier_zjbg_menu_id
      AND menu_type = 'F'
      AND perms = 'school:zjbg:list'
  );

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '详情', @supplier_zjbg_menu_id, 2, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'school:zjbg:query', '#',
  'admin', NOW(), '', NULL, '供应商质检报告详情'
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = @supplier_zjbg_menu_id
      AND menu_type = 'F'
      AND perms = 'school:zjbg:query'
  );

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '新增', @supplier_zjbg_menu_id, 3, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'school:zjbg:add', '#',
  'admin', NOW(), '', NULL, '供应商质检报告新增'
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = @supplier_zjbg_menu_id
      AND menu_type = 'F'
      AND perms = 'school:zjbg:add'
  );

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT
  '上传', @supplier_zjbg_menu_id, 4, '', NULL, NULL, '',
  1, 0, 'F', '0', '0', 'school:zjbg:upload', '#',
  'admin', NOW(), '', NULL, '供应商质检报告上传'
WHERE @supplier_zjbg_menu_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_menu
    WHERE parent_id = @supplier_zjbg_menu_id
      AND menu_type = 'F'
      AND perms = 'school:zjbg:upload'
  );

-- 授权给超级管理员和供应商角色，避免学校角色获得上传权限。
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m
  ON m.menu_id = @new_feature_menu_id
  OR m.menu_id = @supplier_zjbg_menu_id
  OR m.parent_id = @supplier_zjbg_menu_id
WHERE r.role_key IN ('admin', 'changshang')
  AND r.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.role_id
      AND rm.menu_id = m.menu_id
  );

COMMIT;

-- 执行后检查
SELECT
  menu_id, menu_name, parent_id, order_num, path, component,
  menu_type, perms, visible, status, icon
FROM sys_menu
WHERE menu_id IN (@new_feature_menu_id, @tonggou_menu_id, @school_zjbg_menu_id, @supplier_zjbg_menu_id)
   OR parent_id IN (@school_zjbg_menu_id, @supplier_zjbg_menu_id)
ORDER BY parent_id, order_num, menu_id;
