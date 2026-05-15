-- 供应商端：在“新增功能”目录下增加质检报告、服务学校列表菜单
-- 页面组件：
--   supplier/qualityReport/index
--   supplier/serviceSchool/index

SET @parentId := (SELECT menu_id FROM sys_menu WHERE menu_name = '新增功能' LIMIT 1);

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT
  '质检报告', @parentId, 20, 'supplierQualityReport', 'supplier/qualityReport/index', NULL, 'SupplierQualityReport',
  1, 0, 'C', '0', '0', 'supplier:qualityReport:list', 'documentation', 'admin', NOW(), '', NULL, '供应商端质检报告'
WHERE @parentId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'supplier/qualityReport/index');

SET @qualityReportId := (SELECT menu_id FROM sys_menu WHERE component = 'supplier/qualityReport/index' LIMIT 1);

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '列表', @qualityReportId, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'supplier:qualityReport:list', '#', 'admin', NOW(), '', NULL, ''
WHERE @qualityReportId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @qualityReportId AND perms = 'supplier:qualityReport:list');

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '新增', @qualityReportId, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'supplier:qualityReport:add', '#', 'admin', NOW(), '', NULL, ''
WHERE @qualityReportId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @qualityReportId AND perms = 'supplier:qualityReport:add');

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '详情', @qualityReportId, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'supplier:qualityReport:query', '#', 'admin', NOW(), '', NULL, ''
WHERE @qualityReportId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @qualityReportId AND perms = 'supplier:qualityReport:query');

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT
  '服务学校列表', @parentId, 21, 'supplierServiceSchool', 'supplier/serviceSchool/index', NULL, 'SupplierServiceSchool',
  1, 0, 'C', '0', '0', 'supplier:serviceSchool:list', 'education', 'admin', NOW(), '', NULL, '供应商端服务学校列表'
WHERE @parentId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE component = 'supplier/serviceSchool/index');

SET @serviceSchoolId := (SELECT menu_id FROM sys_menu WHERE component = 'supplier/serviceSchool/index' LIMIT 1);

INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '列表', @serviceSchoolId, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'supplier:serviceSchool:list', '#', 'admin', NOW(), '', NULL, ''
WHERE @serviceSchoolId IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @serviceSchoolId AND perms = 'supplier:serviceSchool:list');

-- 绑定管理员角色权限。
-- 普通管理员角色 role_key = gly；超级管理员 role_key = admin 通常不需要 sys_role_menu 明细权限。
SET @adminRoleId := (SELECT role_id FROM sys_role WHERE role_key = 'gly' AND del_flag = '0' LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @adminRoleId, m.menu_id
FROM sys_menu m
WHERE @adminRoleId IS NOT NULL
  AND m.menu_id IN (@qualityReportId, @serviceSchoolId)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @adminRoleId AND rm.menu_id = m.menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @adminRoleId, m.menu_id
FROM sys_menu m
WHERE @adminRoleId IS NOT NULL
  AND m.parent_id IN (@qualityReportId, @serviceSchoolId)
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = @adminRoleId AND rm.menu_id = m.menu_id
  );

-- 绑定供应商角色权限。
-- 如果项目库里已有启用的供应商角色，请保证 role_key 在下面列表中；
-- 当前库的 changshang 是删除状态时不会绑定，避免脚本误恢复历史角色。
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m ON m.menu_id IN (@qualityReportId, @serviceSchoolId)
WHERE r.del_flag = '0'
  AND r.role_key IN ('changshang', 'supplier', 'supply', 'gys')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = r.role_id AND rm.menu_id = m.menu_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN sys_menu m ON m.parent_id IN (@qualityReportId, @serviceSchoolId)
WHERE r.del_flag = '0'
  AND r.role_key IN ('changshang', 'supplier', 'supply', 'gys')
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = r.role_id AND rm.menu_id = m.menu_id
  );

-- 服务学校列表补充字段：服务电话
ALTER TABLE xfpt_school_supplier_rel
ADD COLUMN service_phone varchar(50) DEFAULT NULL COMMENT '服务电话' AFTER cooperate_status;
