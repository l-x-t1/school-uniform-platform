import request from '@/utils/request'
import { getUserInfo } from '@/utils/auth'

function getCurrentUserId() {
  const userInfo = getUserInfo() || {}
  return Number(userInfo.userId || userInfo.id || 1)
}

function getCurrentUserName() {
  const userInfo = getUserInfo() || {}
  return userInfo.nickName || userInfo.userName || '家长用户'
}

function buildAfterSaleNo() {
  return `AS${Date.now()}`
}

function normalizeAfterSale(item = {}) {
  return {
    ...item,
    id: item.id || item.afterSaleId,
    afterSaleNo: item.afterSaleNo || item.after_sale_no || '',
    orderId: item.orderId || item.order_id,
    orderItemId: item.orderItemId || item.order_item_id,
    orderNo: item.orderNo || item.order_no || '',
    orderType: String(item.orderType || item.order_type || '2'),
    schoolId: item.schoolId || item.school_id,
    supplierId: item.supplierId || item.supplier_id,
    userId: item.userId || item.user_id,
    userName: item.userName || item.user_name || '',
    studentName: item.studentName || item.student_name || '',
    applyType: String(item.applyType || item.apply_type || '2'),
    targetSizeId: item.targetSizeId || item.target_size_id,
    targetSizeName: item.targetSizeName || item.target_size_name || '',
    reason: item.reason || '',
    imagesJson: item.imagesJson || item.images_json || '[]',
    applyNum: Number(item.applyNum || item.apply_num || 1),
    applyAmount: Number(item.applyAmount || item.apply_amount || 0),
    actualRefundAmount: Number(item.actualRefundAmount || item.actual_refund_amount || 0),
    refundStatus: String(item.refundStatus || item.refund_status || '0'),
    refundTime: item.refundTime || item.refund_time || '',
    status: String(item.status || '0'),
    auditBy: item.auditBy || item.audit_by || '',
    auditTime: item.auditTime || item.audit_time || '',
    handleResult: item.handleResult || item.handle_result || '',
    handleRemark: item.handleRemark || item.handle_remark || '',
    finishTime: item.finishTime || item.finish_time || '',
    deptId: item.deptId || item.dept_id
  }
}

export function createAfterSale(data = {}) {
  return request({
    url: '/school/afterSale',
    method: 'POST',
    data: {
      afterSaleNo: data.afterSaleNo || buildAfterSaleNo(),
      orderId: data.orderId,
      orderItemId: data.orderItemId,
      orderNo: data.orderNo,
      orderType: data.orderType || '2',
      schoolId: data.schoolId,
      supplierId: data.supplierId,
      userId: Number(data.userId || getCurrentUserId()),
      userName: data.userName || getCurrentUserName(),
      studentName: data.studentName || '',
      applyType: String(data.applyType || '2'),
      targetSizeId: data.targetSizeId,
      targetSizeName: data.targetSizeName || '',
      reason: data.reason || '',
      imagesJson: data.imagesJson || '[]',
      applyNum: Number(data.applyNum || 1),
      applyAmount: Number(data.applyAmount || 0),
      actualRefundAmount: 0,
      refundStatus: '0',
      status: '0',
      deptId: data.deptId,
      delFlag: '0'
    }
  })
}

export function listAfterSale(params = {}) {
  return request({
    url: '/school/afterSale/list',
    method: 'GET',
    params: {
      userId: getCurrentUserId(),
      ...params
    }
  }).then((res) => ({
    ...res,
    rows: (res.rows || []).map(normalizeAfterSale)
  }))
}

export function getAfterSale(id) {
  return request({
    url: `/school/afterSale/${id}`,
    method: 'GET'
  }).then((res) => ({
    ...res,
    data: normalizeAfterSale(res.data || res)
  }))
}
