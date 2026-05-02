SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

START TRANSACTION;

-- Supplier 23-table closed-loop test data.
-- All seed rows use fixed high IDs and remark tag: SEED_SUPPLIER_CLOSED_LOOP_20260501.

DELETE FROM `xfpt_comment` WHERE `id` IN (910001801);
DELETE FROM `xfpt_after_sale` WHERE `id` IN (910001701);
DELETE FROM `xfpt_settlement_order_item` WHERE `id` IN (910002101);
DELETE FROM `xfpt_withdraw_apply` WHERE `id` IN (910002201);
DELETE FROM `xfpt_finance_flow` WHERE `id` IN (910001901, 910001902, 910001903);
DELETE FROM `xfpt_settlement_order` WHERE `id` IN (910002001);
DELETE FROM `xfpt_delivery_order_item` WHERE `id` IN (910001601, 910001602);
DELETE FROM `xfpt_delivery_order` WHERE `id` IN (910001501);
DELETE FROM `xfpt_production_order_item` WHERE `id` IN (910001401, 910001402);
DELETE FROM `xfpt_production_order` WHERE `id` IN (910001301);
DELETE FROM `xfpt_order_item` WHERE `id` IN (910001201, 910001202);
DELETE FROM `xfpt_order_info` WHERE `id` IN (910001101);
DELETE FROM `xfpt_cart_item` WHERE `id` IN (910001001, 910001002);
DELETE FROM `xfpt_user_address` WHERE `id` IN (910000901);
DELETE FROM `xfpt_student_size_ext` WHERE `id` IN (910000801);
DELETE FROM `xfpt_school_supplier_goods_rel` WHERE `id` IN (910000711, 910000712);
DELETE FROM `xfpt_school_supplier_rel` WHERE `id` IN (910000701);
DELETE FROM `xfpt_supplier_apply` WHERE `id` IN (910000601);
DELETE FROM `xfpt_group_notice_item` WHERE `id` IN (910000511, 910000512);
DELETE FROM `xfpt_group_notice` WHERE `id` IN (910000501);
DELETE FROM `xfpt_goods_publish_log` WHERE `id` IN (910000301, 910000302);
DELETE FROM `xfpt_goods_size` WHERE `id` IN (910000101, 910000102, 910000103, 910000201, 910000202);
DELETE FROM `xfpt_goods` WHERE `id` IN (910000001, 910000002);

INSERT INTO `xfpt_goods`
(`id`, `goods_name`, `goods_code`, `category_name`, `supplier_id`, `supplier_name`, `brand_name`, `main_image`, `detail_images_json`, `detail_content`, `sale_type`, `status`, `publish_status`, `publish_time`, `sort_num`, `create_by`, `create_time`, `remark`)
VALUES
(910000001, '闭环测试-夏季短袖校服套装', 'SEED-GOODS-SUMMER-001', '夏季校服', 223, '测试一', '麒麟优选', '/seed/images/summer_uniform.jpg', '["/seed/images/summer_uniform_1.jpg","/seed/images/summer_uniform_2.jpg"]', '<p>闭环测试数据：夏季短袖上衣和短裤套装。</p>', '3', '1', '1', '2026-05-01 09:00:00', 10, 'seed', '2026-05-01 09:00:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000002, '闭环测试-春秋运动校服套装', 'SEED-GOODS-SPRING-001', '春秋校服', 223, '测试一', '麒麟优选', '/seed/images/spring_uniform.jpg', '["/seed/images/spring_uniform_1.jpg","/seed/images/spring_uniform_2.jpg"]', '<p>闭环测试数据：春秋运动外套和长裤套装。</p>', '3', '1', '1', '2026-05-01 09:05:00', 20, 'seed', '2026-05-01 09:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_goods_size`
(`id`, `goods_id`, `size_code`, `size_name`, `gender_type`, `measure_json`, `retail_price`, `group_price`, `stock_num`, `status`, `create_by`, `create_time`, `remark`)
VALUES
(910000101, 910000001, 'S', 'S', '0', '{"heightRange":"135-150","weightRange":"30-42"}', 139.00, 120.00, 200, '1', 'seed', '2026-05-01 09:10:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000102, 910000001, 'M', 'M', '0', '{"heightRange":"150-165","weightRange":"42-55"}', 139.00, 120.00, 200, '1', 'seed', '2026-05-01 09:10:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000103, 910000001, 'L', 'L', '0', '{"heightRange":"165-175","weightRange":"55-70"}', 139.00, 120.00, 200, '1', 'seed', '2026-05-01 09:10:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000201, 910000002, 'M', 'M', '0', '{"heightRange":"150-165","weightRange":"42-55"}', 199.00, 180.00, 160, '1', 'seed', '2026-05-01 09:12:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000202, 910000002, 'L', 'L', '0', '{"heightRange":"165-175","weightRange":"55-70"}', 199.00, 180.00, 160, '1', 'seed', '2026-05-01 09:12:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_goods_publish_log`
(`id`, `goods_id`, `operate_type`, `operate_by`, `operate_time`, `create_by`, `create_time`, `remark`)
VALUES
(910000301, 910000001, '1', 'seed', '2026-05-01 09:20:00', 'seed', '2026-05-01 09:20:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000302, 910000002, '1', 'seed', '2026-05-01 09:25:00', 'seed', '2026-05-01 09:25:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_group_notice`
(`id`, `school_id`, `school_name`, `title`, `notice_code`, `notice_type`, `content`, `grade_ids`, `grade_names`, `team_meeting_id`, `purchase_process_id`, `start_time`, `end_time`, `status`, `apply_status`, `publish_time`, `result_publish_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910000501, 442, '石家庄市麒麟小学', '闭环测试-2026春季校服统购公告', 'SEED-NOTICE-20260501-001', '1', '<p>闭环测试公告：用于接口联调，包含报名、中选、下单、生产、发货、结算。</p>', '2020,2024', '2020级,2024级', 910010001, 910020001, '2026-05-01 09:30:00', '2026-05-10 18:00:00', '2', '2', '2026-05-01 09:30:00', '2026-05-01 11:00:00', 442, '0,100,200', 'seed', '2026-05-01 09:30:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_group_notice_item`
(`id`, `notice_id`, `goods_id`, `goods_name`, `size_rule_json`, `unit_name`, `buy_num`, `price_desc`, `grade_ids`, `grade_names`, `sort_num`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910000511, 910000501, 910000001, '闭环测试-夏季短袖校服套装', '{"sizeIds":[910000101,910000102,910000103],"required":true}', '套', 300, '统购价120.00元/套，零售价139.00元/套', '2020,2024', '2020级,2024级', 1, 442, '0,100,200', 'seed', '2026-05-01 09:35:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000512, 910000501, 910000002, '闭环测试-春秋运动校服套装', '{"sizeIds":[910000201,910000202],"required":true}', '套', 300, '统购价180.00元/套，零售价199.00元/套', '2020,2024', '2020级,2024级', 2, 442, '0,100,200', 'seed', '2026-05-01 09:36:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_supplier_apply`
(`id`, `notice_id`, `school_id`, `supplier_id`, `supplier_name`, `apply_no`, `apply_status`, `qualification_status`, `bid_stage_status`, `bid_result_status`, `apply_time`, `audit_time`, `audit_remark`, `apply_files_json`, `compare_files_json`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910000601, 910000501, 442, 223, '测试一', 'SEED-APPLY-20260501-001', '1', '1', '2', '1', '2026-05-01 09:45:00', '2026-05-01 10:30:00', '资质审查通过，闭环测试供应商中选。', '[{"name":"营业执照.pdf","url":"/seed/files/license.pdf"}]', '[{"name":"报价单.pdf","url":"/seed/files/quote.pdf"}]', 442, '0,100,200', 'seed', '2026-05-01 09:45:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_school_supplier_rel`
(`id`, `school_id`, `school_name`, `supplier_id`, `supplier_name`, `service_phone`, `notice_id`, `contract_id`, `cooperate_status`, `start_time`, `end_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910000701, 442, '石家庄市麒麟小学', 223, '测试一', '0311-88886666', 910000501, 910030001, '1', '2026-05-01 11:00:00', '2027-05-01 00:00:00', 442, '0,100,200', 'seed', '2026-05-01 11:00:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_school_supplier_goods_rel`
(`id`, `rel_id`, `school_id`, `supplier_id`, `goods_id`, `status`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910000711, 910000701, 442, 223, 910000001, '1', 442, '0,100,200', 'seed', '2026-05-01 11:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910000712, 910000701, 442, 223, 910000002, '1', 442, '0,100,200', 'seed', '2026-05-01 11:06:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_student_size_ext`
(`id`, `student_id`, `height`, `weight`, `chest`, `waistline`, `hipline`, `size_id`, `measure_time`, `measure_remark`, `create_by`, `create_time`, `remark`)
VALUES
(910000801, 36, 158.00, 46.00, 78.00, 65.00, 82.00, 910000102, '2026-05-01 11:20:00', '推荐夏季校服M码，闭环测试数据。', 'seed', '2026-05-01 11:20:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_user_address`
(`id`, `user_id`, `receiver_name`, `receiver_phone`, `province_name`, `city_name`, `district_name`, `detail_address`, `address_type`, `is_default`, `status`, `create_by`, `create_time`, `remark`)
VALUES
(910000901, 2184, '闭环测试家长', '13800000001', '河北省', '石家庄市', '桥西区', '石家庄市麒麟小学门卫室', '2', '1', '1', 'seed', '2026-05-01 11:30:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_cart_item`
(`id`, `user_id`, `student_id`, `goods_id`, `size_id`, `goods_name`, `size_name`, `main_image`, `unit_price`, `buy_num`, `checked_flag`, `order_type`, `notice_id`, `create_by`, `create_time`, `remark`)
VALUES
(910001001, 2184, 36, 910000001, 910000102, '闭环测试-夏季短袖校服套装', 'M', '/seed/images/summer_uniform.jpg', 120.00, 1, '1', '1', 910000501, 'seed', '2026-05-01 12:00:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910001002, 2184, 36, 910000002, 910000201, '闭环测试-春秋运动校服套装', 'M', '/seed/images/spring_uniform.jpg', 180.00, 1, '1', '1', 910000501, 'seed', '2026-05-01 12:01:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_order_info`
(`id`, `order_no`, `order_type`, `notice_id`, `school_id`, `school_name`, `supplier_id`, `supplier_name`, `user_id`, `user_name`, `student_id`, `student_name`, `grade_name`, `class_name`, `address_id`, `receive_type`, `receiver_name`, `receiver_phone`, `receiver_address`, `total_amount`, `pay_amount`, `refund_amount`, `pay_status`, `order_status`, `pay_time`, `finish_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910001101, 'SEED-ORDER-20260501-001', '1', 910000501, 442, '石家庄市麒麟小学', 223, '测试一', 2184, 'csyh', 36, '测试', '2020级', '1班', 910000901, '2', '闭环测试家长', '13800000001', '河北省石家庄市桥西区石家庄市麒麟小学门卫室', 300.00, 300.00, 0.00, '1', '4', '2026-05-01 12:10:00', '2026-05-01 16:30:00', 442, '0,100,200', 'seed', '2026-05-01 12:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_order_item`
(`id`, `order_id`, `goods_id`, `size_id`, `goods_name`, `size_name`, `main_image`, `sale_type`, `unit_price`, `buy_num`, `item_amount`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910001201, 910001101, 910000001, 910000102, '闭环测试-夏季短袖校服套装', 'M', '/seed/images/summer_uniform.jpg', '1', 120.00, 1, 120.00, 442, '0,100,200', 'seed', '2026-05-01 12:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910001202, 910001101, 910000002, 910000201, '闭环测试-春秋运动校服套装', 'M', '/seed/images/spring_uniform.jpg', '1', 180.00, 1, 180.00, 442, '0,100,200', 'seed', '2026-05-01 12:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_production_order`
(`id`, `production_no`, `notice_id`, `order_id`, `order_no`, `order_type`, `school_id`, `supplier_id`, `status`, `planned_finish_time`, `start_time`, `finish_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910001301, 'SEED-PROD-20260501-001', 910000501, 910001101, 'SEED-ORDER-20260501-001', '1', 442, 223, '2', '2026-05-03 18:00:00', '2026-05-01 13:00:00', '2026-05-01 14:30:00', 442, '0,100,200', 'seed', '2026-05-01 13:00:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_production_order_item`
(`id`, `production_id`, `order_item_id`, `goods_id`, `size_id`, `goods_name`, `size_name`, `buy_num`, `finish_num`, `status`, `create_by`, `create_time`, `remark`)
VALUES
(910001401, 910001301, 910001201, 910000001, 910000102, '闭环测试-夏季短袖校服套装', 'M', 1, 1, '2', 'seed', '2026-05-01 13:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910001402, 910001301, 910001202, 910000002, 910000201, '闭环测试-春秋运动校服套装', 'M', 1, 1, '2', 'seed', '2026-05-01 13:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_delivery_order`
(`id`, `delivery_no`, `notice_id`, `order_id`, `order_no`, `production_id`, `school_id`, `supplier_id`, `receiver_name`, `receiver_phone`, `receiver_address`, `logistics_company`, `logistics_no`, `delivery_status`, `delivery_time`, `sign_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910001501, 'SEED-DEL-20260501-001', 910000501, 910001101, 'SEED-ORDER-20260501-001', 910001301, 442, 223, '闭环测试家长', '13800000001', '河北省石家庄市桥西区石家庄市麒麟小学门卫室', '顺丰速运', 'SF-SEED-20260501-001', '2', '2026-05-01 15:00:00', '2026-05-01 16:00:00', 442, '0,100,200', 'seed', '2026-05-01 15:00:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_delivery_order_item`
(`id`, `delivery_id`, `order_item_id`, `goods_id`, `size_id`, `goods_name`, `size_name`, `delivery_num`, `sign_num`, `status`, `create_by`, `create_time`, `remark`)
VALUES
(910001601, 910001501, 910001201, 910000001, 910000102, '闭环测试-夏季短袖校服套装', 'M', 1, 1, '2', 'seed', '2026-05-01 15:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910001602, 910001501, 910001202, 910000002, 910000201, '闭环测试-春秋运动校服套装', 'M', 1, 1, '2', 'seed', '2026-05-01 15:05:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_after_sale`
(`id`, `after_sale_no`, `order_id`, `order_item_id`, `order_no`, `order_type`, `school_id`, `supplier_id`, `user_id`, `user_name`, `student_name`, `apply_type`, `target_size_id`, `target_size_name`, `reason`, `images_json`, `apply_num`, `apply_amount`, `status`, `audit_by`, `audit_time`, `handle_result`, `handle_remark`, `finish_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910001701, 'SEED-AS-20260501-001', 910001101, 910001201, 'SEED-ORDER-20260501-001', '1', 442, 223, 2184, 'csyh', '测试', '4', 910000103, 'L', '学生身高增长，申请M码调换为L码。', '["/seed/images/after_sale_size.jpg"]', 1, 0.00, '4', 'admin', '2026-05-01 16:45:00', '已完成尺码调换，补发L码。', '闭环测试售后处理完成。', '2026-05-01 17:20:00', 442, '0,100,200', 'seed', '2026-05-01 16:40:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_comment`
(`id`, `order_id`, `order_item_id`, `order_no`, `goods_id`, `goods_name`, `user_id`, `user_name`, `supplier_id`, `score`, `content`, `images_json`, `reply_content`, `reply_time`, `status`, `create_by`, `create_time`, `remark`)
VALUES
(910001801, 910001101, 910001202, 'SEED-ORDER-20260501-001', 910000002, '闭环测试-春秋运动校服套装', 2184, 'csyh', 223, 5, '闭环测试评价：面料舒适，尺码合适，发货及时。', '["/seed/images/comment_1.jpg"]', '感谢反馈，后续继续优化服务。', '2026-05-01 17:40:00', '0', 'seed', '2026-05-01 17:30:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_finance_flow`
(`id`, `flow_no`, `business_type`, `order_id`, `after_sale_id`, `school_id`, `supplier_id`, `user_id`, `amount`, `pay_channel`, `trade_no`, `status`, `trade_time`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910001901, 'SEED-FLOW-PAY-20260501-001', '1', 910001101, NULL, 442, 223, 2184, 300.00, 'wechat', 'WX-SEED-20260501-001', '1', '2026-05-01 12:10:00', 442, '0,100,200', 'seed', '2026-05-01 12:10:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910001902, 'SEED-FLOW-SETTLE-20260501-001', '3', 910001101, NULL, 442, 223, NULL, 300.00, 'system', 'SETTLE-SEED-20260501-001', '1', '2026-05-01 18:10:00', 442, '0,100,200', 'seed', '2026-05-01 18:10:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501'),
(910001903, 'SEED-FLOW-WITHDRAW-20260501-001', '4', NULL, NULL, NULL, 223, NULL, 282.00, 'bank', 'BANK-SEED-20260501-001', '1', '2026-05-01 18:40:00', 442, '0,100,200', 'seed', '2026-05-01 18:40:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_settlement_order`
(`id`, `settlement_no`, `school_id`, `school_name`, `supplier_id`, `supplier_name`, `settlement_period_start`, `settlement_period_end`, `order_count`, `settlement_amount`, `tax_amount`, `paid_amount`, `unpaid_amount`, `status`, `apply_time`, `audit_time`, `auditor_name`, `audit_remark`, `dept_id`, `ancestors`, `create_by`, `create_time`, `remark`)
VALUES
(910002001, 'SEED-SETTLE-20260501-001', 442, '石家庄市麒麟小学', 223, '测试一', '2026-05-01 00:00:00', '2026-05-01 23:59:59', 1, 300.00, 18.00, 282.00, 0.00, '3', '2026-05-01 18:00:00', '2026-05-01 18:20:00', 'admin', '结算审核通过，闭环测试数据。', 442, '0,100,200', 'seed', '2026-05-01 18:00:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_settlement_order_item`
(`id`, `settlement_id`, `order_id`, `order_no`, `finance_flow_id`, `business_type`, `settlement_amount`, `source_time`, `create_by`, `create_time`, `remark`)
VALUES
(910002101, 910002001, 910001101, 'SEED-ORDER-20260501-001', 910001901, '1', 300.00, '2026-05-01 12:10:00', 'seed', '2026-05-01 18:02:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

INSERT INTO `xfpt_withdraw_apply`
(`id`, `withdraw_no`, `supplier_id`, `apply_amount`, `bank_info_json`, `status`, `apply_time`, `audit_time`, `pay_time`, `audit_remark`, `finance_flow_id`, `create_by`, `create_time`, `remark`)
VALUES
(910002201, 'SEED-WITHDRAW-20260501-001', 223, 282.00, '{"accountName":"测试一","bankName":"中国建设银行石家庄分行","bankNo":"6227000000000000001"}', '3', '2026-05-01 18:25:00', '2026-05-01 18:30:00', '2026-05-01 18:40:00', '提现审核通过并打款，闭环测试数据。', 910001903, 'seed', '2026-05-01 18:25:00', 'SEED_SUPPLIER_CLOSED_LOOP_20260501');

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
