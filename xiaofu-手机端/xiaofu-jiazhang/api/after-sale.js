import request from '@/utils/request'
import { getUserInfo } from '@/utils/auth'

function getCurrentUser() {
  return getUserInfo() || {}
}

function getCurrentUserId() {
  const user = getCurrentUser()
  return Number(user.userId || user.id || 1)
}

function getCurrentUserName() {
  const user = getCurrentUser()
  return user.nickName || user.userName || '家长用户'
}

function buildAfterSaleNo() {
  return `AS${Date.now()}`
}

export function listAfterSales(params = {}) {
  return request({
    url: '/school/afterSale/list',
    method: 'GET',
    params: {
      userId: getCurrentUserId(),
      ...params
    }
  })
}

export function getAfterSale(id) {
  return request({
    url: `/school/afterSale/${id}`,
    method: 'GET'
  })
}

export function createAfterSale(order, product, form = {}) {
  const userId = getCurrentUserId()
  const userName = getCurrentUserName()
  const quantity = Number(form.applyNum || product.quantity || 1)
  const unitPrice = Number(product.price || 0)
  const applyType = form.applyType || '2'
  const isRefundType = applyType === '1' || applyType === '2'
  const applyAmount = Number((unitPrice * quantity).toFixed(2))

  return request({
    url: '/school/afterSale',
    method: 'POST',
    data: {
      afterSaleNo: buildAfterSaleNo(),
      orderId: Number(order.id),
      orderItemId: Number(product.id),
      orderNo: order.orderNo,
      orderType: order.orderType || '2',
      schoolId: Number(order.schoolId || 0),
      supplierId: Number(order.supplierId || product.supplierId || 0),
      userId,
      userName,
      studentName: order.studentName || '',
      applyType,
      targetSizeId: form.targetSizeId ? Number(form.targetSizeId) : null,
      targetSizeName: form.targetSizeName || '',
      returnTrackingNo: form.returnTrackingNo || '',
      reason: form.reason || '',
      imagesJson: JSON.stringify(form.images || []),
      applyNum: quantity,
      applyAmount,
      actualRefundAmount: isRefundType ? applyAmount : 0,
      refundStatus: isRefundType ? '2' : '0',
      status: '4',
      deptId: Number(order.deptId || 1),
      createBy: userName,
      delFlag: '0'
    }
  })
}
