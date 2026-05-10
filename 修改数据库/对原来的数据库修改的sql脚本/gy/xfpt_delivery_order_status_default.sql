ALTER TABLE `xfpt_delivery_order`
  MODIFY `delivery_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '发货状态，0待发货 1已发货 2已签收';
