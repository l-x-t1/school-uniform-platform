/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : xfglpt

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 03/05/2026 17:39:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_production_order
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_production_order`;
CREATE TABLE `xfpt_production_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `production_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '生产单号',
  `notice_id` bigint(20) NULL DEFAULT NULL COMMENT '统购公告ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单号快照',
  `order_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型，1统购 2零售',
  `school_id` bigint(20) NULL DEFAULT NULL COMMENT '学校ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态，0待生产 1生产中 2生产完成',
  `planned_finish_time` datetime NULL DEFAULT NULL COMMENT '计划完成时间',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始生产时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '生产完成时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '当前归属部门/学校',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_xfpt_production_order_no`(`production_no` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_notice`(`notice_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_supplier`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_status`(`status` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910001303 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '生产单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_production_order
-- ----------------------------
INSERT INTO `xfpt_production_order` VALUES (910001301, 'SEED-PROD-20260501-001', 910000501, 910001101, 'SEED-ORDER-20260501-001', '1', 442, 223, '2', '2026-05-03 18:00:00', '2026-05-01 13:00:00', '2026-05-01 14:30:00', 442, 'seed', '2026-05-01 13:00:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
