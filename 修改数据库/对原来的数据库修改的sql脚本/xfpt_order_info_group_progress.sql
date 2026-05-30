ALTER TABLE xfpt_order_info
    ADD COLUMN progress_status char(1) DEFAULT '0' COMMENT '统购订购进度状态：0待查看 1供应商已查看 2制作中 3已完成 4已发货' AFTER order_status,
    ADD COLUMN logistics_no varchar(100) DEFAULT NULL COMMENT '物流单号' AFTER progress_status;

CREATE INDEX idx_xfpt_order_info_school_group_progress
    ON xfpt_order_info (school_id, dept_id, order_type, class_name, progress_status, order_no);

CREATE INDEX idx_xfpt_order_info_supplier_group_progress
    ON xfpt_order_info (supplier_id, order_type, progress_status, order_no);
