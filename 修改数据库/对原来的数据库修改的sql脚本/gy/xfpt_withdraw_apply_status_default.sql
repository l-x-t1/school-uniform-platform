-- 供应商提现申请状态默认值：0待审核
ALTER TABLE `xfpt_withdraw_apply`
  MODIFY COLUMN `status` char(1) NOT NULL DEFAULT '0' COMMENT '提现状态，0待审核 1审核通过 2审核拒绝 3已打款 4已取消';
