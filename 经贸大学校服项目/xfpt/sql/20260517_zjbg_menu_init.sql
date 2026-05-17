-- 质检报告管理菜单与权限初始化
-- 可重复执行：菜单存在则更新，不存在则插入；授权先清理目标授权再插入。

-- 必要表结构：附件表主键使用数据库自增，避免上传附件时报 Field 'ID' doesn't have a default value
ALTER TABLE `xfpt_supplice_inspection_report_son`
  MODIFY COLUMN `ID` bigint NOT NULL AUTO_INCREMENT;

-- 父级目录：新增功能
INSERT INTO `sys_menu` (
  `menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`
)
SELECT
  2333, '新增功能', 0, 99, 'newFeature', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'tree',
  'admin', NOW(), 'admin', NOW(), '新增功能目录'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 2333);

UPDATE `sys_menu`
SET
  `menu_name` = '新增功能',
  `parent_id` = 0,
  `order_num` = 99,
  `path` = 'newFeature',
  `component` = NULL,
  `query` = NULL,
  `is_frame` = 1,
  `is_cache` = 0,
  `menu_type` = 'M',
  `visible` = '0',
  `status` = '0',
  `perms` = '',
  `icon` = 'tree',
  `update_by` = 'admin',
  `update_time` = NOW(),
  `remark` = '新增功能目录'
WHERE `menu_id` = 2333;

-- 子菜单：质检报告管理
INSERT INTO `sys_menu` (
  `menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`
)
SELECT
  3003, '质检报告管理', 2333, 3, 'zjbg', 'zjbg/index', NULL,
  1, 0, 'C', '0', '0', 'school:zjbg:list', 'pdf',
  'admin', NOW(), 'admin', NOW(), '管理质检报告'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 3003);

UPDATE `sys_menu`
SET
  `menu_name` = '质检报告管理',
  `parent_id` = 2333,
  `order_num` = 3,
  `path` = 'zjbg',
  `component` = 'zjbg/index',
  `query` = NULL,
  `is_frame` = 1,
  `is_cache` = 0,
  `menu_type` = 'C',
  `visible` = '0',
  `status` = '0',
  `perms` = 'school:zjbg:list',
  `icon` = 'pdf',
  `update_by` = 'admin',
  `update_time` = NOW(),
  `remark` = '管理质检报告'
WHERE `menu_id` = 3003;

-- 按钮权限：列表
INSERT INTO `sys_menu` (
  `menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`
)
SELECT
  3030, '列表', 3003, 1, '', NULL, NULL,
  1, 0, 'F', '0', '0', 'school:zjbg:list', '#',
  'admin', NOW(), '', NULL, '查看质检报告列表'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 3030);

UPDATE `sys_menu`
SET
  `menu_name` = '列表',
  `parent_id` = 3003,
  `order_num` = 1,
  `path` = '',
  `component` = NULL,
  `query` = NULL,
  `is_frame` = 1,
  `is_cache` = 0,
  `menu_type` = 'F',
  `visible` = '0',
  `status` = '0',
  `perms` = 'school:zjbg:list',
  `icon` = '#',
  `update_by` = '',
  `update_time` = NULL,
  `remark` = '查看质检报告列表'
WHERE `menu_id` = 3030;

-- 按钮权限：详情
INSERT INTO `sys_menu` (
  `menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`,
  `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`
)
SELECT
  3031, '详情', 3003, 2, '', NULL, NULL,
  1, 0, 'F', '0', '0', 'school:zjbg:query', '#',
  'admin', NOW(), '', NULL, '查看质检报告详情'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `menu_id` = 3031);

UPDATE `sys_menu`
SET
  `menu_name` = '详情',
  `parent_id` = 3003,
  `order_num` = 2,
  `path` = '',
  `component` = NULL,
  `query` = NULL,
  `is_frame` = 1,
  `is_cache` = 0,
  `menu_type` = 'F',
  `visible` = '0',
  `status` = '0',
  `perms` = 'school:zjbg:query',
  `icon` = '#',
  `update_by` = '',
  `update_time` = NULL,
  `remark` = '查看质检报告详情'
WHERE `menu_id` = 3031;

-- admin 角色授权
DELETE FROM `sys_role_menu`
WHERE `role_id` = 1
  AND `menu_id` IN (3003, 3030, 3031);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 3003),
(1, 3030),
(1, 3031);
