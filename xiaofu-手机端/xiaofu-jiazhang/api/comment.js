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

export function listComments(params = {}) {
  return request({
    url: '/school/comment/list',
    method: 'GET',
    params: {
      userId: getCurrentUserId(),
      ...params
    }
  })
}

export function listAllComments(params = {}) {
  return request({
    url: '/school/comment/list',
    method: 'GET',
    params
  })
}

export function getComment(id) {
  return request({
    url: `/school/comment/${id}`,
    method: 'GET'
  })
}

export function createComment(order, product, form = {}) {
  const userId = getCurrentUserId()
  const userName = getCurrentUserName()
  const score = Number(form.score || 5)

  return request({
    url: '/school/comment',
    method: 'POST',
    data: {
      orderId: Number(order.id),
      orderItemId: Number(product.id),
      orderNo: order.orderNo,
      goodsId: Number(product.uniformId || product.goodsId || product.id || 0),
      goodsName: product.name || '校服',
      userId,
      userName,
      supplierId: Number(order.supplierId || product.supplierId || 0),
      score,
      content: form.content || '',
      imagesJson: JSON.stringify(form.images || []),
      status: '0',
      createBy: userName,
      delFlag: '0'
    }
  })
}
