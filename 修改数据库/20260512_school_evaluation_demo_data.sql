-- 学校端评价管理演示数据
-- 仅用于本地/测试环境联调；生产环境请不要执行。

INSERT INTO `xfpt_order_info` (
  `order_no`, `order_type`, `notice_id`, `school_id`, `school_name`, `supplier_id`, `supplier_name`,
  `user_id`, `user_name`, `student_id`, `student_name`, `grade_name`, `class_name`, `address_id`,
  `receive_type`, `receiver_name`, `receiver_phone`, `receiver_address`, `total_amount`, `pay_amount`,
  `refund_amount`, `pay_status`, `order_status`, `pay_time`, `cancel_time`, `finish_time`, `dept_id`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  'DEMO-EVALUATION-ORDER-20260512-GROUP-1', '1', NULL, 222, '石家庄交通运输学校', 1, '河北超享纺织服装有限公司',
  2194, 'testUser', NULL, NULL, NULL, NULL, NULL,
  '1', '石家庄交通运输学校', '13000000000', '石家庄交通运输学校收货点', 188.00, 188.00,
  0.00, '1', '4', '2026-05-10 09:10:00', NULL, '2026-05-11 16:20:00', 222,
  'admin', NOW(), NULL, NULL, '评价管理演示统购订单', '0'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `xfpt_order_info` WHERE `order_no` = 'DEMO-EVALUATION-ORDER-20260512-GROUP-1'
);

SET @evaluation_group_order_id := (
  SELECT `id`
  FROM `xfpt_order_info`
  WHERE `order_no` = 'DEMO-EVALUATION-ORDER-20260512-GROUP-1'
  LIMIT 1
);

INSERT INTO `xfpt_order_item` (
  `order_id`, `goods_id`, `size_id`, `goods_name`, `size_name`, `main_image`,
  `sale_type`, `unit_price`, `buy_num`, `item_amount`, `dept_id`, `create_by`,
  `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  @evaluation_group_order_id, 1, 1, '春秋运动校服套装', '155', '/profile/upload/demo/goods_1.jpg',
  '1', 188.00, 1, 188.00, 222, 'admin',
  NOW(), NULL, NULL, '评价管理演示统购明细', '0'
FROM DUAL
WHERE @evaluation_group_order_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `xfpt_order_item`
    WHERE `order_id` = @evaluation_group_order_id
      AND `goods_id` = 1
      AND `size_id` = 1
      AND `del_flag` = '0'
  );

SET @evaluation_group_item_id := (
  SELECT `id`
  FROM `xfpt_order_item`
  WHERE `order_id` = @evaluation_group_order_id
    AND `goods_id` = 1
    AND `size_id` = 1
    AND `del_flag` = '0'
  LIMIT 1
);

INSERT INTO `xfpt_comment` (
  `order_id`, `order_item_id`, `order_no`, `goods_id`, `goods_name`, `user_id`, `user_name`,
  `supplier_id`, `score`, `content`, `images_json`, `reply_content`, `reply_time`, `status`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  @evaluation_group_order_id, @evaluation_group_item_id, 'DEMO-EVALUATION-ORDER-20260512-GROUP-1',
  1, '春秋运动校服套装', 2194, 'testUser',
  1, 5, '面料舒适，尺码标准，孩子试穿后很满意。', '["/profile/upload/demo/comment.jpg"]',
  '感谢您的反馈，我们会继续做好服务。', '2026-05-12 09:35:00', '0',
  'admin', '2026-05-12 09:30:00', 'admin', '2026-05-12 09:35:00', '评价管理演示数据', '0'
FROM DUAL
WHERE @evaluation_group_order_id IS NOT NULL
  AND @evaluation_group_item_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `xfpt_comment`
    WHERE `order_item_id` = @evaluation_group_item_id
      AND `user_id` = 2194
      AND `del_flag` = '0'
  );

SET @evaluation_group_comment_id := (
  SELECT `id`
  FROM `xfpt_comment`
  WHERE `order_item_id` = @evaluation_group_item_id
    AND `user_id` = 2194
    AND `del_flag` = '0'
  LIMIT 1
);

INSERT INTO `xfpt_comment_reply` (
  `comment_id`, `order_id`, `order_item_id`, `goods_id`, `supplier_id`,
  `from_user_id`, `from_user_name`, `from_role_type`, `from_role_title`,
  `to_user_id`, `to_user_name`, `to_role_type`, `to_role_title`,
  `content`, `status`, `sort_num`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  @evaluation_group_comment_id, @evaluation_group_order_id, @evaluation_group_item_id, 1, 1,
  1, '管理员', '4', '管理员',
  2194, 'testUser', '1', '家长',
  '感谢您的反馈，我们会继续做好服务。', '0', 1, 'admin', '2026-05-12 09:35:00', NULL, NULL, '评价管理演示回复', '0'
FROM DUAL
WHERE @evaluation_group_comment_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `xfpt_comment_reply`
    WHERE `comment_id` = @evaluation_group_comment_id
      AND `content` = '感谢您的反馈，我们会继续做好服务。'
      AND `del_flag` = '0'
  );

INSERT INTO `xfpt_order_info` (
  `order_no`, `order_type`, `notice_id`, `school_id`, `school_name`, `supplier_id`, `supplier_name`,
  `user_id`, `user_name`, `student_id`, `student_name`, `grade_name`, `class_name`, `address_id`,
  `receive_type`, `receiver_name`, `receiver_phone`, `receiver_address`, `total_amount`, `pay_amount`,
  `refund_amount`, `pay_status`, `order_status`, `pay_time`, `cancel_time`, `finish_time`, `dept_id`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  'DEMO-EVALUATION-ORDER-20260512-RETAIL-1', '2', NULL, 222, '石家庄交通运输学校', 2, '河北春风树服饰有限公司',
  2194, 'testUser', NULL, NULL, NULL, NULL, NULL,
  '2', 'testUser', '13000000000', '石家庄交通运输学校测试收货地址', 99.00, 99.00,
  0.00, '1', '4', '2026-05-10 10:10:00', NULL, '2026-05-11 17:20:00', 222,
  'admin', NOW(), NULL, NULL, '评价管理演示零购订单', '0'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `xfpt_order_info` WHERE `order_no` = 'DEMO-EVALUATION-ORDER-20260512-RETAIL-1'
);

SET @evaluation_retail_order_id := (
  SELECT `id`
  FROM `xfpt_order_info`
  WHERE `order_no` = 'DEMO-EVALUATION-ORDER-20260512-RETAIL-1'
  LIMIT 1
);

INSERT INTO `xfpt_order_item` (
  `order_id`, `goods_id`, `size_id`, `goods_name`, `size_name`, `main_image`,
  `sale_type`, `unit_price`, `buy_num`, `item_amount`, `dept_id`, `create_by`,
  `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  @evaluation_retail_order_id, 3, 3, '冬季加绒冲锋衣校服', '170', '/profile/upload/demo/goods_3.jpg',
  '2', 99.00, 1, 99.00, 222, 'admin',
  NOW(), NULL, NULL, '评价管理演示零购明细', '0'
FROM DUAL
WHERE @evaluation_retail_order_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `xfpt_order_item`
    WHERE `order_id` = @evaluation_retail_order_id
      AND `goods_id` = 3
      AND `size_id` = 3
      AND `del_flag` = '0'
  );

SET @evaluation_retail_item_id := (
  SELECT `id`
  FROM `xfpt_order_item`
  WHERE `order_id` = @evaluation_retail_order_id
    AND `goods_id` = 3
    AND `size_id` = 3
    AND `del_flag` = '0'
  LIMIT 1
);

INSERT INTO `xfpt_comment` (
  `order_id`, `order_item_id`, `order_no`, `goods_id`, `goods_name`, `user_id`, `user_name`,
  `supplier_id`, `score`, `content`, `images_json`, `reply_content`, `reply_time`, `status`,
  `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
)
SELECT
  @evaluation_retail_order_id, @evaluation_retail_item_id, 'DEMO-EVALUATION-ORDER-20260512-RETAIL-1',
  3, '冬季加绒冲锋衣校服', 2194, 'testUser',
  2, 3, '袖口线头略多，希望后续质检再细一些。', '["/profile/upload/demo/comment.jpg"]',
  NULL, NULL, '1',
  'admin', '2026-05-12 10:30:00', NULL, NULL, '评价管理演示隐藏数据', '0'
FROM DUAL
WHERE @evaluation_retail_order_id IS NOT NULL
  AND @evaluation_retail_item_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM `xfpt_comment`
    WHERE `order_item_id` = @evaluation_retail_item_id
      AND `user_id` = 2194
      AND `del_flag` = '0'
  );
