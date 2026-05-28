ALTER TABLE xfpt_after_sale
  ADD COLUMN return_tracking_no varchar(100) DEFAULT NULL COMMENT '退货物流单号' AFTER target_size_name;
