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

 Date: 03/05/2026 17:44:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_group_notice_item
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_group_notice_item`;
CREATE TABLE `xfpt_group_notice_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `notice_id` bigint NOT NULL COMMENT '公告ID',
  `goods_id` bigint NOT NULL COMMENT '商品ID',
  `goods_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称快照',
  `size_rule_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '可选尺码规则JSON文本',
  `unit_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `buy_num` int UNSIGNED NOT NULL COMMENT '采购数量',
  `price_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '价格区间或价格说明',
  `grade_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用年级ID',
  `grade_names` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用年级名称',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '当前归属部门/学校',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_xfpt_group_notice_item_goods`(`notice_id` ASC, `goods_id` ASC) USING BTREE,
  INDEX `idx_xfpt_group_notice_item_notice`(`notice_id` ASC) USING BTREE,
  INDEX `idx_xfpt_group_notice_item_goods`(`goods_id` ASC) USING BTREE,
  INDEX `idx_xfpt_group_notice_item_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910000513 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '统购公告商品' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_group_notice_item
-- ----------------------------
INSERT INTO `xfpt_group_notice_item` VALUES (910000511, 910000501, 910000001, '闭环测试-夏季短袖校服套装', '{\"sizeIds\":[910000101,910000102,910000103],\"required\":true}', '套', 300, '统购价120.00元/套，零售价139.00元/套', '2020,2024', '2020级,2024级', 442, 'seed', '2026-05-01 09:35:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');
INSERT INTO `xfpt_group_notice_item` VALUES (910000512, 910000501, 910000002, '闭环测试-春秋运动校服套装', '{\"sizeIds\":[910000201,910000202],\"required\":true}', '套', 300, '统购价180.00元/套，零售价199.00元/套', '2020,2024', '2020级,2024级', 442, 'seed', '2026-05-01 09:36:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
