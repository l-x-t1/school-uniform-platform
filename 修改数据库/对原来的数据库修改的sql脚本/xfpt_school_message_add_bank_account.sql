-- 学校信息增加银行卡账户字段
-- 设计说明：
-- 1. 银行账户属于学校基础资料，放在 xfpt_school_message，不放 sys_dept。
-- 2. 拆成独立字段，便于表单校验、查询、脱敏展示和后续结算使用。

ALTER TABLE `xfpt_school_message`
  ADD COLUMN `bank_account_name` varchar(100) DEFAULT NULL COMMENT '银行账户户名' AFTER `principal_phone`,
  ADD COLUMN `bank_name` varchar(100) DEFAULT NULL COMMENT '开户银行' AFTER `bank_account_name`,
  ADD COLUMN `bank_branch_name` varchar(200) DEFAULT NULL COMMENT '开户支行' AFTER `bank_name`,
  ADD COLUMN `bank_account_no` varchar(64) DEFAULT NULL COMMENT '银行卡号' AFTER `bank_branch_name`;
