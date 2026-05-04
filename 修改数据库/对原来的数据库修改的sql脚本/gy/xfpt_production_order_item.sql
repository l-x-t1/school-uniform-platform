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

 Date: 03/05/2026 17:39:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_production_order_item
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_production_order_item`;
CREATE TABLE `xfpt_production_order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `production_id` bigint(20) NOT NULL COMMENT '生产单主表ID',
  `order_item_id` bigint(20) NULL DEFAULT NULL COMMENT '订单明细ID',
  `goods_id` bigint(20) NOT NULL COMMENT '商品ID',
  `size_id` bigint(20) NULL DEFAULT NULL COMMENT '尺码ID',
  `goods_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称快照',
  `size_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '尺码名称快照',
  `buy_num` int(10) UNSIGNED NOT NULL COMMENT '应生产数量',
  `finish_num` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '已完成数量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态，0待生产 1生产中 2生产完成',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_xfpt_production_order_item_production`(`production_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_item_order_item`(`order_item_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_item_goods`(`goods_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_item_size`(`size_id` ASC) USING BTREE,
  INDEX `idx_xfpt_production_order_item_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910001404 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '生产单明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_production_order_item
-- ----------------------------
INSERT INTO `xfpt_production_order_item` VALUES (910001401, 910001301, 910001201, 910000001, 910000102, '闭环测试-夏季短袖校服套装', 'M', 1, 1, '2', 'seed', '2026-05-01 13:05:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');
INSERT INTO `xfpt_production_order_item` VALUES (910001402, 910001301, 910001202, 910000002, 910000201, '闭环测试-春秋运动校服套装', 'M', 1, 1, '2', 'seed', '2026-05-01 13:05:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
