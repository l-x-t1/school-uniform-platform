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

 Date: 03/05/2026 17:43:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_cart_item
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_cart_item`;
CREATE TABLE `xfpt_cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '家长用户ID',
  `student_id` bigint NULL DEFAULT NULL COMMENT '学生ID',
  `goods_id` bigint NOT NULL COMMENT '商品ID',
  `size_id` bigint NOT NULL COMMENT '尺码ID',
  `goods_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称快照',
  `size_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '尺码名称快照',
  `main_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品主图快照',
  `unit_price` decimal(18, 2) NOT NULL COMMENT '当前单价',
  `buy_num` int UNSIGNED NOT NULL COMMENT '购买数量',
  `checked_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选中标记，0未选中 1选中',
  `order_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型，1统购 2零售',
  `notice_id` bigint NULL DEFAULT NULL COMMENT '统购公告ID',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_xfpt_cart_item_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_xfpt_cart_item_student`(`student_id` ASC) USING BTREE,
  INDEX `idx_xfpt_cart_item_goods`(`goods_id` ASC) USING BTREE,
  INDEX `idx_xfpt_cart_item_size`(`size_id` ASC) USING BTREE,
  INDEX `idx_xfpt_cart_item_notice`(`notice_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910001003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_cart_item
-- ----------------------------
INSERT INTO `xfpt_cart_item` VALUES (910001001, 2184, 36, 910000001, 910000102, '闭环测试-夏季短袖校服套装', 'M', '/seed/images/summer_uniform.jpg', 120.00, 1, '1', '1', 910000501, 'seed', '2026-05-01 12:00:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');
INSERT INTO `xfpt_cart_item` VALUES (910001002, 2184, 36, 910000002, 910000201, '闭环测试-春秋运动校服套装', 'M', '/seed/images/spring_uniform.jpg', 180.00, 1, '1', '1', 910000501, 'seed', '2026-05-01 12:01:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
