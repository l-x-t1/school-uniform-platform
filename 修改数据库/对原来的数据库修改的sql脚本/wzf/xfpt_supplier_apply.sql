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

 Date: 03/05/2026 17:44:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_supplier_apply
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_supplier_apply`;
CREATE TABLE `xfpt_supplier_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `notice_id` bigint NOT NULL COMMENT '公告ID',
  `school_id` bigint NOT NULL COMMENT '学校ID',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称快照',
  `apply_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报名单号',
  `apply_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报名状态，0待提交 1已提交 2已撤回',
  `qualification_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资质审查状态，0待审查 1通过 2不通过',
  `bid_stage_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '比选阶段状态，0未进入 1已进入 2已完成',
  `bid_result_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '比选结果状态，0未出结果 1已中选 2未中选',
  `apply_time` datetime NULL DEFAULT NULL COMMENT '报名时间',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `apply_files_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '报名附件JSON文本',
  `compare_files_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '比选资料JSON文本',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '当前归属部门/学校',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_xfpt_supplier_apply_no`(`apply_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_xfpt_supplier_apply_notice_supplier`(`notice_id` ASC, `supplier_id` ASC) USING BTREE,
  INDEX `idx_xfpt_supplier_apply_status`(`notice_id` ASC, `supplier_id` ASC, `qualification_status` ASC, `bid_stage_status` ASC, `bid_result_status` ASC) USING BTREE,
  INDEX `idx_xfpt_supplier_apply_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910000602 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '供应商报名' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_supplier_apply
-- ----------------------------
INSERT INTO `xfpt_supplier_apply` VALUES (910000601, 910000501, 442, 223, '测试一', 'SEED-APPLY-20260501-001', '1', '1', '2', '1', '2026-05-01 09:45:00', '2026-05-01 10:30:00', '资质审查通过，闭环测试供应商中选。', '[{\"name\":\"营业执照.pdf\",\"url\":\"/seed/files/license.pdf\"}]', '[{\"name\":\"报价单.pdf\",\"url\":\"/seed/files/quote.pdf\"}]', 442, 'seed', '2026-05-01 09:45:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
