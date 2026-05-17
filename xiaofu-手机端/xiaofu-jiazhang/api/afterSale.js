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
  })
}
