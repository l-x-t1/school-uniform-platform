-- Supplier menu hierarchy and role permission fix.
-- Target role: 100 / 供应商(changshang)
-- This script is idempotent for sys_menu rows. It does not delete business data.

UPDATE sys_menu
SET menu_name = '供应商端',
    parent_id = 0,
    order_num = 10,
    path = 'supplierPortal',
    component = NULL,
    route_name = '',
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = NULL,
    icon = 'shopping',
    update_by = 'codex',
    update_time = NOW()
WHERE menu_id = 2300;

INSERT INTO sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (2450, '商品管理', 2300, 1, 'goodsManage', NULL, '', 1, 0, 'M', '0', '0', NULL, 'shopping', 'codex', NOW(), 'codex', NOW(), '商品管理目录'),
    (2451, '统购报名', 2300, 2, 'noticeApply', NULL, '', 1, 0, 'M', '0', '0', NULL, 'form', 'codex', NOW(), 'codex', NOW(), '统购报名目录'),
    (2452, '合作管理', 2300, 3, 'cooperation', NULL, '', 1, 0, 'M', '0', '0', NULL, 'peoples', 'codex', NOW(), 'codex', NOW(), '合作管理目录'),
    (2453, '订单履约', 2300, 4, 'orderFulfillment', NULL, '', 1, 0, 'M', '0', '0', NULL, 'list', 'codex', NOW(), 'codex', NOW(), '订单履约目录'),
    (2454, '售后评价', 2300, 5, 'afterSaleService', NULL, '', 1, 0, 'M', '0', '0', NULL, 'message', 'codex', NOW(), 'codex', NOW(), '售后评价目录'),
    (2455, '财务结算', 2300, 6, 'financeSettlement', NULL, '', 1, 0, 'M', '0', '0', NULL, 'money', 'codex', NOW(), 'codex', NOW(), '财务结算目录')
ON DUPLICATE KEY UPDATE
    menu_name = VALUES(menu_name),
    parent_id = VALUES(parent_id),
    order_num = VALUES(order_num),
    path = VALUES(path),
    component = VALUES(component),
    route_name = VALUES(route_name),
    menu_type = VALUES(menu_type),
    visible = VALUES(visible),
    status = VALUES(status),
    perms = VALUES(perms),
    icon = VALUES(icon),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark);

INSERT INTO sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (2301, '商品信息', 2450, 1, 'goods', 'supplier/goods/index', '', 1, 0, 'C', '0', '0', 'supplier:goods:list', '#', 'codex', NOW(), 'codex', NOW(), '商品信息菜单'),
    (2460, '商品尺码', 2450, 2, 'goodsSize', 'supplier/goodsSize/index', '', 1, 0, 'C', '0', '0', 'supplier:goodsSize:list', '#', 'codex', NOW(), 'codex', NOW(), '商品尺码菜单'),
    (2313, '商品上下架日志', 2450, 3, 'goodsPublishLog', 'supplier/goodsPublishLog/index', '', 1, 0, 'C', '0', '0', 'supplier:goodsPublishLog:list', '#', 'codex', NOW(), 'codex', NOW(), '商品上下架日志菜单'),
    (2461, '统购公告', 2451, 1, 'groupNotice', 'supplier/groupNotice/index', '', 1, 0, 'C', '0', '0', 'supplier:groupNotice:list', '#', 'codex', NOW(), 'codex', NOW(), '统购公告菜单'),
    (2462, '统购公告商品', 2451, 2, 'groupNoticeItem', 'supplier/groupNoticeItem/index', '', 1, 0, 'C', '0', '0', 'supplier:groupNoticeItem:list', '#', 'codex', NOW(), 'codex', NOW(), '统购公告商品菜单'),
    (2463, '供应商报名', 2451, 3, 'supplierApply', 'supplier/supplierApply/index', '', 1, 0, 'C', '0', '0', 'supplier:supplierApply:list', '#', 'codex', NOW(), 'codex', NOW(), '供应商报名菜单'),
    (2314, '学校供应商合作关系', 2452, 1, 'schoolSupplierRel', 'supplier/schoolSupplierRel/index', '', 1, 0, 'C', '0', '0', 'supplier:schoolSupplierRel:list', '#', 'codex', NOW(), 'codex', NOW(), '学校供应商合作关系菜单'),
    (2464, '学校供应商合作商品', 2452, 2, 'schoolSupplierGoodsRel', 'supplier/schoolSupplierGoodsRel/index', '', 1, 0, 'C', '0', '0', 'supplier:schoolSupplierGoodsRel:list', '#', 'codex', NOW(), 'codex', NOW(), '学校供应商合作商品菜单'),
    (2302, '购物车明细', 2453, 1, 'cartItem', 'supplier/cartItem/index', '', 1, 0, 'C', '0', '0', 'supplier:cartItem:list', '#', 'codex', NOW(), 'codex', NOW(), '购物车明细菜单'),
    (2303, '订单信息', 2453, 2, 'orderInfo', 'supplier/orderInfo/index', '', 1, 0, 'C', '0', '0', 'supplier:orderInfo:list', '#', 'codex', NOW(), 'codex', NOW(), '订单信息菜单'),
    (2304, '订单明细', 2453, 3, 'orderItem', 'supplier/orderItem/index', '', 1, 0, 'C', '0', '0', 'supplier:orderItem:list', '#', 'codex', NOW(), 'codex', NOW(), '订单明细菜单'),
    (2309, '生产单', 2453, 4, 'productionOrder', 'supplier/productionOrder/index', '', 1, 0, 'C', '0', '0', 'supplier:productionOrder:list', '#', 'codex', NOW(), 'codex', NOW(), '生产单菜单'),
    (2465, '生产单明细', 2453, 5, 'productionOrderItem', 'supplier/productionOrderItem/index', '', 1, 0, 'C', '0', '0', 'supplier:productionOrderItem:list', '#', 'codex', NOW(), 'codex', NOW(), '生产单明细菜单'),
    (2307, '发货单', 2453, 6, 'deliveryOrder', 'supplier/deliveryOrder/index', '', 1, 0, 'C', '0', '0', 'supplier:deliveryOrder:list', '#', 'codex', NOW(), 'codex', NOW(), '发货单菜单'),
    (2308, '发货单明细', 2453, 7, 'deliveryOrderItem', 'supplier/deliveryOrderItem/index', '', 1, 0, 'C', '0', '0', 'supplier:deliveryOrderItem:list', '#', 'codex', NOW(), 'codex', NOW(), '发货单明细菜单'),
    (2306, '售后单', 2454, 1, 'afterSale', 'supplier/afterSale/index', '', 1, 0, 'C', '0', '0', 'supplier:afterSale:list', '#', 'codex', NOW(), 'codex', NOW(), '售后单菜单'),
    (2305, '商品评价', 2454, 2, 'comment', 'supplier/comment/index', '', 1, 0, 'C', '0', '0', 'supplier:comment:list', '#', 'codex', NOW(), 'codex', NOW(), '商品评价菜单'),
    (2466, '学生尺码扩展', 2454, 3, 'studentSizeExt', 'supplier/studentSizeExt/index', '', 1, 0, 'C', '0', '0', 'supplier:studentSizeExt:list', '#', 'codex', NOW(), 'codex', NOW(), '学生尺码扩展菜单'),
    (2467, '用户收货地址', 2454, 4, 'userAddress', 'supplier/userAddress/index', '', 1, 0, 'C', '0', '0', 'supplier:userAddress:list', '#', 'codex', NOW(), 'codex', NOW(), '用户收货地址菜单'),
    (2311, '财务流水', 2455, 1, 'financeFlow', 'supplier/financeFlow/index', '', 1, 0, 'C', '0', '0', 'supplier:financeFlow:list', '#', 'codex', NOW(), 'codex', NOW(), '财务流水菜单'),
    (2310, '结算单', 2455, 2, 'settlementOrder', 'supplier/settlementOrder/index', '', 1, 0, 'C', '0', '0', 'supplier:settlementOrder:list', '#', 'codex', NOW(), 'codex', NOW(), '结算单菜单'),
    (2315, '结算明细', 2455, 3, 'settlementOrderItem', 'supplier/settlementOrderItem/index', '', 1, 0, 'C', '0', '0', 'supplier:settlementOrderItem:list', '#', 'codex', NOW(), 'codex', NOW(), '结算明细菜单'),
    (2312, '提现申请', 2455, 4, 'withdrawApply', 'supplier/withdrawApply/index', '', 1, 0, 'C', '0', '0', 'supplier:withdrawApply:list', '#', 'codex', NOW(), 'codex', NOW(), '提现申请菜单')
ON DUPLICATE KEY UPDATE
    menu_name = VALUES(menu_name),
    parent_id = VALUES(parent_id),
    order_num = VALUES(order_num),
    path = VALUES(path),
    component = VALUES(component),
    route_name = VALUES(route_name),
    menu_type = VALUES(menu_type),
    visible = VALUES(visible),
    status = VALUES(status),
    perms = VALUES(perms),
    icon = VALUES(icon),
    update_by = VALUES(update_by),
    update_time = VALUES(update_time),
    remark = VALUES(remark);

UPDATE sys_menu SET parent_id = 2306, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:afterSale:%';
UPDATE sys_menu SET parent_id = 2302, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:cartItem:%';
UPDATE sys_menu SET parent_id = 2305, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:comment:%';
UPDATE sys_menu SET parent_id = 2307, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:deliveryOrder:%';
UPDATE sys_menu SET parent_id = 2308, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:deliveryOrderItem:%';
UPDATE sys_menu SET parent_id = 2311, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:financeFlow:%';
UPDATE sys_menu SET parent_id = 2301, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:goods:%';
UPDATE sys_menu SET parent_id = 2313, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:goodsPublishLog:%';
UPDATE sys_menu SET parent_id = 2460, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:goodsSize:%';
UPDATE sys_menu SET parent_id = 2461, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:groupNotice:%';
UPDATE sys_menu SET parent_id = 2462, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:groupNoticeItem:%';
UPDATE sys_menu SET parent_id = 2303, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:orderInfo:%';
UPDATE sys_menu SET parent_id = 2304, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:orderItem:%';
UPDATE sys_menu SET parent_id = 2309, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:productionOrder:%';
UPDATE sys_menu SET parent_id = 2465, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:productionOrderItem:%';
UPDATE sys_menu SET parent_id = 2464, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:schoolSupplierGoodsRel:%';
UPDATE sys_menu SET parent_id = 2314, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:schoolSupplierRel:%';
UPDATE sys_menu SET parent_id = 2310, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:settlementOrder:%';
UPDATE sys_menu SET parent_id = 2315, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:settlementOrderItem:%';
UPDATE sys_menu SET parent_id = 2466, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:studentSizeExt:%';
UPDATE sys_menu SET parent_id = 2463, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:supplierApply:%';
UPDATE sys_menu SET parent_id = 2467, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:userAddress:%';
UPDATE sys_menu SET parent_id = 2312, route_name = '', update_by = 'codex', update_time = NOW() WHERE menu_type = 'F' AND perms LIKE 'supplier:withdrawApply:%';

INSERT INTO sys_role_menu(role_id, menu_id)
SELECT 100, m.menu_id
FROM sys_menu m
WHERE (m.menu_id = 2300
       OR m.parent_id IN (2300, 2450, 2451, 2452, 2453, 2454, 2455)
       OR (m.menu_type = 'F' AND m.perms LIKE 'supplier:%'))
  AND NOT EXISTS (
      SELECT 1
      FROM sys_role_menu rm
      WHERE rm.role_id = 100
        AND rm.menu_id = m.menu_id
  );
