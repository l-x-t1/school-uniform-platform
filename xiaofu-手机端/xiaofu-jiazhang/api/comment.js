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

export function createComment(data = {}) {
  return request({
    url: '/school/comment',
    method: 'POST',
    data: {
      orderId: data.orderId,
      orderItemId: data.orderItemId,
      orderNo: data.orderNo,
      goodsId: data.goodsId,
      goodsName: data.goodsName || '',
      userId: Number(data.userId || getCurrentUserId()),
      userName: data.userName || getCurrentUserName(),
      supplierId: data.supplierId,
      score: Number(data.score || 5),
      content: data.content || '',
      imagesJson: data.imagesJson || '[]',
      status: '0',
      delFlag: '0'
    }
  })
}
