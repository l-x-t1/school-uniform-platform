import request from '@/utils/request'
import { getUserInfo } from '@/utils/auth'

function getCurrentUserId() {
  const userInfo = getUserInfo() || {}
  return Number(userInfo.userId || userInfo.id || 1)
}

function normalizeCart(item = {}) {
  return {
    ...item,
    id: item.id || item.cartId,
    uniformId: item.goodsId || item.uniformId || item.id,
    name: item.goodsName || item.name || '校服',
    image: item.mainImage || item.image || '/static/qjxf.png',
    price: item.unitPrice || item.price || '0.00',
    quantity: item.buyNum || item.quantity || 1,
    size: item.sizeName || item.size || '',
    selected: item.selected !== false
  }
}

function buildCartPayload(data = {}) {
  return {
    id: data.id,
    userId: Number(data.userId || getCurrentUserId()),
    goodsId: Number(data.uniformId || data.goodsId || data.id || 1),
    goodsName: data.name || data.goodsName || '校服',
    mainImage: data.image || data.mainImage || '',
    unitPrice: Number(data.price || data.unitPrice || 0),
    buyNum: Number(data.quantity || data.buyNum || 1),
    sizeName: data.size || data.sizeName || '',
    delFlag: data.delFlag || '0'
  }
}

export function listCart(params = {}) {
  return request({
    url: '/school/cartItem/list',
    method: 'GET',
    params: {
      userId: getCurrentUserId(),
      ...params
    }
  }).then((res) => ({
    ...res,
    rows: (res.rows || []).map(normalizeCart)
  }))
}

export function addCart(data) {
  return request({
    url: '/school/cartItem',
    method: 'POST',
    data: buildCartPayload(data)
  })
}

export function updateCart(data) {
  return request({
    url: '/school/cartItem',
    method: 'PUT',
    data: buildCartPayload(data)
  })
}

export function deleteCart(ids) {
  return request({
    url: `/school/cartItem/${ids}`,
    method: 'DELETE'
  })
}
