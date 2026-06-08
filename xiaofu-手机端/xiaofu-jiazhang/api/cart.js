import request from '@/utils/request'
import { getUserInfo } from '@/utils/auth'

function getCurrentUserId() {
  const userInfo = getUserInfo() || {}
  return Number(userInfo.userId || userInfo.id || 1)
}

function getCurrentStudentId() {
  const selectedStudent = uni.getStorageSync('selectedStudent') || {}
  const studentList = uni.getStorageSync('studentList') || []
  const fallbackStudent = studentList[0] || {}
  return Number(selectedStudent.id || selectedStudent.studentId || fallbackStudent.id || fallbackStudent.studentId || 0)
}

function formatPrice(value) {
  const num = Number(value || 0)
  return Number.isNaN(num) ? '0.00' : num.toFixed(2)
}

function getOrderTypeText(orderType) {
  return String(orderType || '1') === '2' ? '零售购买' : '统购购买'
}

function normalizeCart(item = {}) {
  return {
    ...item,
    id: item.id || item.cartId,
    userId: Number(item.userId || getCurrentUserId()),
    studentId: Number(item.studentId || 0),
    uniformId: item.goodsId || item.uniformId || item.id,
    goodsId: item.goodsId || item.uniformId || item.id,
    skuId: item.sizeId || item.skuId,
    sizeId: item.sizeId || item.skuId,
    name: item.goodsName || item.name || '校服',
    image: item.mainImage || item.image || '/static/qjxf.png',
    mainImage: item.mainImage || item.image || '/static/qjxf.png',
    price: formatPrice(item.unitPrice || item.price || 0),
    unitPrice: formatPrice(item.unitPrice || item.price || 0),
    quantity: Number(item.buyNum || item.quantity || 1),
    buyNum: Number(item.buyNum || item.quantity || 1),
    size: item.sizeName || item.size || '',
    sizeName: item.sizeName || item.size || '',
    supplierId: item.supplierId ? Number(item.supplierId) : null,
    supplierName: item.supplierName || '',
    schoolId: item.schoolId ? Number(item.schoolId) : null,
    schoolName: item.schoolName || '',
    selected: String(item.checkedFlag ?? (item.selected ? '1' : '0') ?? '1') === '1',
    checkedFlag: String(item.checkedFlag ?? (item.selected ? '1' : '0') ?? '1'),
    orderType: String(item.orderType || item.saleType || '1'),
    orderTypeText: getOrderTypeText(item.orderType || item.saleType || '1'),
    noticeId: item.noticeId ? Number(item.noticeId) : null
  }
}

function buildAddCartPayload(data = {}) {
  return {
    userId: Number(data.userId || getCurrentUserId()),
    studentId: Number(data.studentId || getCurrentStudentId() || 0),
    goodsId: Number(data.uniformId || data.goodsId || data.id || 0),
    sizeId: Number(data.skuId || data.sizeId || 0),
    goodsName: data.name || data.goodsName || '校服',
    sizeName: data.size || data.sizeName || '',
    mainImage: data.image || data.mainImage || '',
    unitPrice: Number(data.price || data.unitPrice || 0),
    buyNum: Number(data.quantity || data.buyNum || 1),
    checkedFlag: String(data.checkedFlag ?? (data.selected === false ? '0' : '1')),
    orderType: String(data.orderType || data.saleType || '1'),
    noticeId: data.noticeId ? Number(data.noticeId) : null,
    delFlag: data.delFlag || '0'
  }
}

function buildUpdateCartPayload(data = {}) {
  const payload = {}
  if (data.id !== undefined && data.id !== null && data.id !== '') payload.id = Number(data.id)
  if (data.userId !== undefined) payload.userId = Number(data.userId || getCurrentUserId())
  if (data.studentId !== undefined) payload.studentId = Number(data.studentId || 0)
  if (data.uniformId !== undefined || data.goodsId !== undefined) payload.goodsId = Number(data.uniformId || data.goodsId || 0)
  if (data.skuId !== undefined || data.sizeId !== undefined) payload.sizeId = Number(data.skuId || data.sizeId || 0)
  if (data.name !== undefined || data.goodsName !== undefined) payload.goodsName = data.name || data.goodsName || '校服'
  if (data.size !== undefined || data.sizeName !== undefined) payload.sizeName = data.size || data.sizeName || ''
  if (data.image !== undefined || data.mainImage !== undefined) payload.mainImage = data.image || data.mainImage || ''
  if (data.price !== undefined || data.unitPrice !== undefined) payload.unitPrice = Number(data.price || data.unitPrice || 0)
  if (data.quantity !== undefined || data.buyNum !== undefined) payload.buyNum = Number(data.quantity || data.buyNum || 1)
  if (data.checkedFlag !== undefined || data.selected !== undefined) payload.checkedFlag = String(data.checkedFlag ?? (data.selected ? '1' : '0'))
  if (data.orderType !== undefined || data.saleType !== undefined) payload.orderType = String(data.orderType || data.saleType || '1')
  if (data.noticeId !== undefined) payload.noticeId = data.noticeId ? Number(data.noticeId) : null
  if (data.delFlag !== undefined) payload.delFlag = data.delFlag || '0'
  return payload
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
    data: buildAddCartPayload(data)
  })
}

export function updateCart(data) {
  return request({
    url: '/school/cartItem',
    method: 'PUT',
    data: buildUpdateCartPayload(data)
  })
}

export function deleteCart(ids) {
  return request({
    url: `/school/cartItem/${ids}`,
    method: 'DELETE'
  })
}

export async function upsertCart(data = {}) {
  const res = await listCart()
  const rows = res.rows || []
  const goodsId = String(data.uniformId || data.goodsId || data.id || '')
  const sizeId = String(data.skuId || data.sizeId || '')
  const orderType = String(data.orderType || data.saleType || '1')
  const studentId = String(data.studentId || getCurrentStudentId() || '0')
  const existed = rows.find((item) =>
    String(item.uniformId || item.goodsId || item.id || '') === goodsId &&
    String(item.skuId || item.sizeId || '') === sizeId &&
    String(item.orderType || '1') === orderType &&
    String(item.studentId || '0') === studentId
  )

  if (existed && existed.id) {
    const quantity = Number(existed.quantity || existed.buyNum || 1) + Number(data.quantity || data.buyNum || 1)
    return updateCart({
      ...existed,
      id: existed.id,
      quantity,
      selected: true,
      checkedFlag: '1'
    })
  }

  return addCart(data)
}
