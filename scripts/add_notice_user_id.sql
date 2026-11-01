-- Add notice_user_id column to xfpt_supplier_settle_apply
ALTER TABLE xfpt_supplier_settle_apply ADD COLUMN notice_user_id bigint DEFAULT NULL COMMENT '公告发布人ID';
