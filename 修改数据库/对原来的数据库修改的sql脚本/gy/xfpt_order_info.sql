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

 Date: 03/05/2026 17:38:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xfpt_order_info
-- ----------------------------
DROP TABLE IF EXISTS `xfpt_order_info`;
CREATE TABLE `xfpt_order_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `order_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单类型，1统购 2零售',
  `notice_id` bigint(20) NULL DEFAULT NULL COMMENT '统购公告ID',
  `school_id` bigint(20) NULL DEFAULT NULL COMMENT '学校ID',
  `school_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校名称快照',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '供应商名称快照',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下单用户名称',
  `student_id` bigint(20) NULL DEFAULT NULL COMMENT '学生ID',
  `student_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学生姓名',
  `grade_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级名称',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班级名称',
  `address_id` bigint(20) NULL DEFAULT NULL COMMENT '收货地址ID',
  `receive_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货类型，1学校 2家庭',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名快照',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话快照',
  `receiver_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址快照',
  `total_amount` decimal(18, 2) NOT NULL COMMENT '订单总金额（衣服价格加上运费）',
  `pay_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '实付金额',
  `refund_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '已退款金额',
  `pay_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付状态，0待支付 1已支付 2部分退款 3已退款',
  `order_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态，0待支付 1待生产 2待发货 3待收货 4已完成 5已取消 6售后中',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `finish_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '当前归属部门/学校',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '删除标记，0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_xfpt_order_info_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_xfpt_order_info_status`(`order_type` ASC, `pay_status` ASC, `order_status` ASC) USING BTREE,
  INDEX `idx_xfpt_order_info_supplier`(`supplier_id` ASC) USING BTREE,
  INDEX `idx_xfpt_order_info_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_xfpt_order_info_notice`(`notice_id` ASC) USING BTREE,
  INDEX `idx_xfpt_order_info_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 910001104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of xfpt_order_info
-- ----------------------------
INSERT INTO `xfpt_order_info` VALUES (910001101, 'SEED-ORDER-20260501-001', '1', 910000501, 442, '石家庄市麒麟小学', 223, '测试一', 2184, 'csyh', 36, '测试', '2020级', '1班', 910000901, '2', '闭环测试家长', '13800000001', '河北省石家庄市桥西区石家庄市麒麟小学门卫室', 300.00, 300.00, 0.00, '1', '4', '2026-05-01 12:10:00', NULL, '2026-05-01 16:30:00', 442, 'seed', '2026-05-01 12:05:00', NULL, NULL, 'SEED_SUPPLIER_CLOSED_LOOP_20260501', '0');

SET FOREIGN_KEY_CHECKS = 1;
