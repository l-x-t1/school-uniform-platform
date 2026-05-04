/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : xfglpt

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 03/05/2026 17:44:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_group_notice
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_group_notice`;
CREATE TABLE `xfpt_group_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `school_id` bigint NOT NULL COMMENT '学校ID',
  `school_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学校名称快照',
  `title` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告编号',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告类型，1选购公告 2结果公告',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告正文',
  `grade_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级ID集合',
  `grade_names` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级名称集合',
  `team_meeting_id` bigint NULL DEFAULT NULL COMMENT '班子会议ID',
  `purchase_process_id` bigint NULL DEFAULT NULL COMMENT '采购流程ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '公告开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '公告截止时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态，0草稿 1已发布 2已结束 3已撤回',
  `apply_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报名状态，0未开放 1报名中 2报名结束',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `result_publish_time` datetime NULL DEFAULT NULL COMMENT '结果发布时间',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '当前归属部门/学校',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_xfpt_group_notice_code`(`notice_code` ASC) USING BTREE,
  INDEX `idx_xfpt_group_notice_status`(`school_id` ASC, `status` ASC, `apply_status` ASC) USING BTREE,
  INDEX `idx_xfpt_group_notice_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910000502 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '统购公告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_group_notice
-- ----------------------------
INSERT INTO `xfpt_group_notice` VALUES (910000501, 442, '石家庄市麒麟小学', '闭环测试-2026春季校服统购公告', 'SEED-NOTICE-20260501-001', '1', '<p>闭环测试公告：用于接口联调，包含报名、中选、下单、生产、发货、结算。</p>', '2020,2024', '2020级,2024级', 910010001, 910020001, '2026-05-01 09:30:00', '2026-05-10 18:00:00', '2', '2', '2026-05-01 09:30:00', '2026-05-01 11:00:00', 442, 'seed', '2026-05-01 09:30:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
