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

function normalizeRefundInfo(item = {}, normalized = {}) {
  const applyType = String(normalized.applyType || item.applyType || item.apply_type || '2')
  const status = String(normalized.status || item.status || '1')
  const applyAmount = Number(normalized.applyAmount || item.applyAmount || item.apply_amount || 0)
  let actualRefundAmount = Number(normalized.actualRefundAmount || item.actualRefundAmount || item.actual_refund_amount || 0)
  let refundStatus = String(normalized.refundStatus || item.refundStatus || item.refund_status || '0')

  if (['1', '2'].includes(applyType)) {
    if (!actualRefundAmount && applyAmount > 0) {
      actualRefundAmount = applyAmount
    }
    if ((refundStatus === '0' || !refundStatus) && (status === '4' || actualRefundAmount > 0)) {
      refundStatus = '2'
    }
  }

  return {
    actualRefundAmount,
    refundStatus
  }
}

function normalizeAfterSale(item = {}) {
  const normalized = {
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
    returnTrackingNo: item.returnTrackingNo || item.return_tracking_no || '',
    reason: item.reason || '',
    imagesJson: item.imagesJson || item.images_json || '[]',
    applyNum: Number(item.applyNum || item.apply_num || 1),
    applyAmount: Number(item.applyAmount || item.apply_amount || 0),
    actualRefundAmount: Number(item.actualRefundAmount || item.actual_refund_amount || 0),
    refundStatus: String(item.refundStatus || item.refund_status || '0'),
    refundTime: item.refundTime || item.refund_time || '',
    status: String(item.status || '1'),
    auditBy: item.auditBy || item.audit_by || '',
    auditTime: item.auditTime || item.audit_time || '',
    handleResult: item.handleResult || item.handle_result || '',
    handleRemark: item.handleRemark || item.handle_remark || '',
    finishTime: item.finishTime || item.finish_time || '',
    deptId: item.deptId || item.dept_id
  }
  return {
    ...normalized,
    ...normalizeRefundInfo(item, normalized)
  }
}

export function createAfterSale(data = {}) {
  const applyType = String(data.applyType || '2')
  const isRefundType = ['1', '2'].includes(applyType)
  const applyAmount = Number(data.applyAmount || 0)
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
      returnTrackingNo: data.returnTrackingNo || '',
      reason: data.reason || '',
      imagesJson: data.imagesJson || '[]',
      applyNum: Number(data.applyNum || 1),
      applyAmount,
      actualRefundAmount: isRefundType ? applyAmount : 0,
      refundStatus: isRefundType ? '2' : '0',
      status: '4',
      handleRemark: data.handleRemark || '',
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
