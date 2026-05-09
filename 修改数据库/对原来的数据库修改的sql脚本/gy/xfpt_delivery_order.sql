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

 Date: 03/05/2026 17:40:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_delivery_order
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_delivery_order`;
CREATE TABLE `xfpt_delivery_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `delivery_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发货单号',
  `notice_id` bigint(20) NULL DEFAULT NULL COMMENT '统购公告ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单号快照',
  `production_id` bigint(20) NULL DEFAULT NULL COMMENT '生产单ID',
  `school_id` bigint(20) NULL DEFAULT NULL COMMENT '学校ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名快照',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话快照',
  `receiver_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址快照',
  `logistics_company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流单号',
  `delivery_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '发货状态，0待发货 1已发货 2已签收',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签收时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '当前归属部门/学校',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  `carriage_fee` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运费',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_xfpt_delivery_order_no`(`delivery_no` ASC) USING BTREE,
  INDEX `idx_xfpt_delivery_order_notice`(`notice_id` ASC) USING BTREE,
  INDEX `idx_xfpt_delivery_order_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_xfpt_delivery_order_production`(`production_id` ASC) USING BTREE,
  INDEX `idx_xfpt_delivery_order_supplier`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_xfpt_delivery_order_status`(`delivery_status` ASC) USING BTREE,
  INDEX `idx_xfpt_delivery_order_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910001503 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '发货单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_delivery_order
-- ----------------------------
INSERT INTO `xfpt_delivery_order` VALUES (910001501, 'SEED-DEL-20260501-001', 910000501, 910001101, 'SEED-ORDER-20260501-001', 910001301, 442, 223, '闭环测试家长', '13800000001', '河北省石家庄市桥西区石家庄市麒麟小学门卫室', '顺丰速运', 'SF-SEED-20260501-001', '2', '2026-05-01 15:00:00', '2026-05-01 16:00:00', 442, 'seed', '2026-05-01 15:00:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0', NULL);

SET FOREIGN_KEY_CHECKS = 1;
