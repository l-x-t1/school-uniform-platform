-- Create group notice supplier visibility relation table.
CREATE TABLE IF NOT EXISTS `xfpt_group_notice_supplier` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `notice_id` bigint NOT NULL COMMENT '公告ID',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(100) DEFAULT NULL COMMENT '供应商名称快照',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`),
  KEY `idx_gns_notice_id` (`notice_id`),
  KEY `idx_gns_supplier_id` (`supplier_id`),
  KEY `idx_gns_notice_supplier` (`notice_id`, `supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统购公告供应商可见范围表';

-- Rollback:
-- DROP TABLE IF EXISTS `xfpt_group_notice_supplier`;
