ALTER TABLE xfpt_group_notice
  ADD COLUMN purchase_start_time datetime DEFAULT NULL COMMENT '统购开始时间' AFTER end_time,
  ADD COLUMN purchase_end_time datetime DEFAULT NULL COMMENT '统购结束时间' AFTER purchase_start_time;

CREATE INDEX idx_xfpt_group_notice_purchase_time
  ON xfpt_group_notice (purchase_start_time, purchase_end_time, status, del_flag);
