-- 统购管理菜单
-- 位置：校服采购流程之后、校服厂商管理之前

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(3000, '统购管理', 0, 2, 'tonggou', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '', NULL, '统购管理目录')
ON DUPLICATE KEY UPDATE
  `menu_name` = VALUES(`menu_name`),
  `parent_id` = VALUES(`parent_id`),
  `order_num` = VALUES(`order_num`),
  `path` = VALUES(`path`),
  `component` = VALUES(`component`),
  `menu_type` = VALUES(`menu_type`),
  `visible` = VALUES(`visible`),
  `status` = VALUES(`status`),
  `icon` = VALUES(`icon`),
  `update_by` = 'admin',
  `update_time` = NOW(),
  `remark` = VALUES(`remark`);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(3001, '公告管理', 3000, 1, 'groupNotice', 'school/groupNotice/index', NULL, '', 1, 0, 'C', '0', '0', 'school:groupNotice:list', 'documentation', 'admin', NOW(), '', NULL, '给企业发布统购公告，企业可以查看'),
(3002, '统购订购进度管理', 3000, 2, 'tgjd', 'tgjd/index', NULL, '', 1, 0, 'C', '0', '0', 'school:tgjd:list', 'chart', 'admin', NOW(), '', NULL, '查看统购订购进度'),
(3003, '质检报告管理', 3000, 3, 'zjbg', 'zjbg/index', NULL, '', 1, 0, 'C', '0', '0', 'school:zjbg:list', 'pdf', 'admin', NOW(), '', NULL, '管理统购质检报告'),
(3004, '尺码调换记录', 3000, 4, 'sxjjl', 'sxjjl/index', NULL, '', 1, 0, 'C', '0', '0', 'school:sxjjl:list', 'size', 'admin', NOW(), '', NULL, '查看尺码调换记录'),
(3005, '发货单统计', 3000, 5, 'tgfh', 'tgfh/index', NULL, '', 1, 0, 'C', '0', '0', 'school:tgfh:list', 'excel', 'admin', NOW(), '', NULL, '统计统购发货单')
ON DUPLICATE KEY UPDATE
  `menu_name` = VALUES(`menu_name`),
  `parent_id` = VALUES(`parent_id`),
  `order_num` = VALUES(`order_num`),
  `path` = VALUES(`path`),
  `component` = VALUES(`component`),
  `menu_type` = VALUES(`menu_type`),
  `visible` = VALUES(`visible`),
  `status` = VALUES(`status`),
  `perms` = VALUES(`perms`),
  `icon` = VALUES(`icon`),
  `update_by` = 'admin',
  `update_time` = NOW(),
  `remark` = VALUES(`remark`);

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(3010, '列表', 3001, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:groupNotice:list', '#', 'admin', NOW(), '', NULL, '查看统购公告列表'),
(3011, '添加公告', 3001, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:groupNotice:add', '#', 'admin', NOW(), '', NULL, '给企业发布统购公告，企业可以查看'),
(3012, '详情', 3001, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:groupNotice:query', '#', 'admin', NOW(), '', NULL, '查看统购公告详情'),
(3020, '列表', 3002, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:tgjd:list', '#', 'admin', NOW(), '', NULL, '查看统购订购进度列表'),
(3021, '详情', 3002, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:tgjd:query', '#', 'admin', NOW(), '', NULL, '查看统购订购进度详情'),
(3030, '列表', 3003, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:zjbg:list', '#', 'admin', NOW(), '', NULL, '查看质检报告列表'),
(3031, '详情', 3003, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:zjbg:query', '#', 'admin', NOW(), '', NULL, '查看质检报告详情'),
(3040, '列表（待受理、已通过、已退回、已更换）', 3004, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:sxjjl:list', '#', 'admin', NOW(), '', NULL, '按状态查看尺码调换记录列表'),
(3041, '详情', 3004, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:sxjjl:query', '#', 'admin', NOW(), '', NULL, '查看尺码调换记录详情'),
(3050, '列表', 3005, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:tgfh:list', '#', 'admin', NOW(), '', NULL, '查看发货单统计列表')
ON DUPLICATE KEY UPDATE
  `menu_name` = VALUES(`menu_name`),
  `parent_id` = VALUES(`parent_id`),
  `order_num` = VALUES(`order_num`),
  `path` = VALUES(`path`),
  `component` = VALUES(`component`),
  `menu_type` = VALUES(`menu_type`),
  `visible` = VALUES(`visible`),
  `status` = VALUES(`status`),
  `perms` = VALUES(`perms`),
  `icon` = VALUES(`icon`),
  `update_by` = 'admin',
  `update_time` = NOW(),
  `remark` = VALUES(`remark`);
