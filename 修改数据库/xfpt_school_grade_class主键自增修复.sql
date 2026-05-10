-- 修复说明：
-- 某些库里的 xfpt_school_grade_class.ID 不是 AUTO_INCREMENT，
-- 会导致新增采购流程时插入年级班级数据报错：
-- Field 'ID' doesn't have a default value

ALTER TABLE `xfpt_school_grade_class`
MODIFY COLUMN `ID` bigint NOT NULL AUTO_INCREMENT COMMENT '主键';

-- 如果当前自增值落后于现有数据，可以同步抬高。
-- 这里按仓库现有导出数据预设为 8159；如果你的库里最大 ID 更大，请改成 max(id)+1。
ALTER TABLE `xfpt_school_grade_class`
AUTO_INCREMENT = 8159;
