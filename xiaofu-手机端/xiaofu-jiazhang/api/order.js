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

function formatPrice(value) {
  const num = Number(value || 0)
  return Number.isNaN(num) ? '0.00' : num.toFixed(2)
}

function mapStatusToQuery(status) {
  const base = { userId: getCurrentUserId() }
  switch (status) {
    case 'unpaid':
      return { ...base, payStatus: '0', orderStatus: '0' }
    case 'unshipped':
      return { ...base, payStatus: '1', orderStatus: '1' }
    case 'shipped':
      return { ...base, payStatus: '1', orderStatus: '3' }
    case 'completed':
      return { ...base, orderStatus: '4' }
    case 'cancelled':
      return { ...base, orderStatus: '5' }
    default:
      return base
  }
}

function mapBackendStatus(orderStatus, payStatus) {
  const order = String(orderStatus ?? '')
  const pay = String(payStatus ?? '')
  if (order === '5') return 'cancelled'
  if (order === '4') return 'completed'
  if (order === '3') return 'shipped'
  if (order === '2' || order === '1') return 'unshipped'
  if (order === '6') return 'afterSale'
  if (order === '0' || pay === '0') return 'unpaid'
  return 'unpaid'
}

function getOrderTypeText(orderType) {
  return String(orderType || '1') === '2' ? '零售订单' : '统购订单'
}

function getReceiveTypeText(receiveType) {
  return String(receiveType || '2') === '1' ? '学校收货' : '家庭收货'
}

function normalizeOrderItem(item = {}) {
  const price = formatPrice(item.unitPrice || item.price || 0)
  const quantity = Number(item.buyNum || item.quantity || 1)
  return {
    ...item,
    id: item.id || item.goodsId,
    uniformId: item.goodsId || item.uniformId || item.id,
    goodsId: item.goodsId || item.uniformId || item.id,
    skuId: item.sizeId || item.skuId,
    sizeId: item.sizeId || item.skuId,
    name: item.goodsName || item.name || '校服',
    goodsName: item.goodsName || item.name || '校服',
    image: item.mainImage || item.image || '/static/qjxf.png',
    mainImage: item.mainImage || item.image || '/static/qjxf.png',
    price,
    unitPrice: price,
    quantity,
    buyNum: quantity,
    size: item.sizeName || item.size || '-',
    sizeName: item.sizeName || item.size || '-',
    color: item.colorName || item.color || item.spec || '-',
    saleType: String(item.saleType || item.orderType || '1'),
    orderType: String(item.saleType || item.orderType || '1'),
    orderTypeText: getOrderTypeText(item.saleType || item.orderType || '1'),
    itemAmount: formatPrice(item.itemAmount || Number(price) * quantity)
  }
}

function normalizeOrder(order = {}) {
  const items = order.xfptOrderItemList || order.orderItems || order.items || []
  const normalizedItems = items.map(normalizeOrderItem)
  const productsAmount = normalizedItems.reduce((sum, item) => sum + Number(item.itemAmount || 0), 0)
  const totalAmount = Number(order.totalAmount ?? order.payAmount ?? productsAmount)
  const payAmount = Number(order.payAmount ?? totalAmount)
  const refundAmount = Number(order.refundAmount || 0)
  const shippingFee = Math.max(0, totalAmount - productsAmount)

  return {
    ...order,
    id: order.id || order.orderId,
    orderNo: order.orderNo || '',
    status: mapBackendStatus(order.orderStatus, order.payStatus),
    orderType: String(order.orderType || (normalizedItems[0]?.orderType || '1')),
    orderTypeText: getOrderTypeText(order.orderType || normalizedItems[0]?.orderType || '1'),
    noticeId: order.noticeId ? Number(order.noticeId) : null,
    schoolId: order.schoolId ? Number(order.schoolId) : null,
    schoolName: order.schoolName || '',
    supplierId: order.supplierId ? Number(order.supplierId) : null,
    supplierName: order.supplierName || '',
    userId: order.userId ? Number(order.userId) : getCurrentUserId(),
    userName: order.userName || getCurrentUserName(),
    studentId: order.studentId ? Number(order.studentId) : null,
    studentName: order.studentName || '',
    gradeName: order.gradeName || '',
    className: order.className || '',
    addressId: order.addressId ? Number(order.addressId) : null,
    receiveType: String(order.receiveType || '2'),
    receiveTypeText: getReceiveTypeText(order.receiveType),
    receiverName: order.receiverName || '家长',
    receiverPhone: order.receiverPhone || '',
    receiverAddress: order.receiverAddress || '',
    createTime: order.createTime || '',
    payTime: order.payTime || '',
    cancelTime: order.cancelTime || '',
    finishTime: order.finishTime || '',
    remark: order.remark || '',
    totalAmount: formatPrice(totalAmount),
    payAmount: formatPrice(payAmount),
    refundAmount: formatPrice(refundAmount),
    productsAmount: formatPrice(productsAmount),
    shippingFee: formatPrice(shippingFee),
    discount: '0.00',
    orderItems: normalizedItems,
    items: normalizedItems,
    products: normalizedItems,
    xfptOrderItemList: normalizedItems,
    address: {
      id: order.addressId || '',
      name: order.receiverName || '家长',
      receiver: order.receiverName || '家长',
      phone: order.receiverPhone || '',
      fullAddress: order.receiverAddress || '',
      tag: String(order.receiveType || '') === '1' ? '学校' : '家'
    },
    student: {
      id: order.studentId || '',
      name: order.studentName || '',
      studentName: order.studentName || '',
      school: order.schoolName || '',
      schoolName: order.schoolName || '',
      grade: order.gradeName || '',
      gradeName: order.gradeName || '',
      className: order.className || ''
    }
  }
}

function buildOrderPayload(data = {}) {
  const userId = getCurrentUserId()
  const userName = getCurrentUserName()
  const studentList = uni.getStorageSync('studentList') || []
  const cachedStudent = uni.getStorageSync('selectedStudent') || {}
  const selectedStudent =
    studentList.find((item) => String(item.id || item.studentId) === String(data.studentId)) ||
    cachedStudent ||
    {}
  const selectedAddress = uni.getStorageSync('selectedAddress') || {}
  const items = Array.isArray(data.items) ? data.items : []
  const firstItem = items[0] || {}
  const orderType = String(data.orderType || firstItem.orderType || firstItem.saleType || '1')
  const xfptOrderItemList = items.map((item) => {
    const unitPrice = Number(item.price || item.unitPrice || 0)
    const buyNum = Number(item.quantity || item.buyNum || 1)
    return {
      goodsId: Number(item.uniformId || item.goodsId || item.id || 0),
      sizeId: Number(item.skuId || item.sizeId || 0),
      goodsName: item.name || item.goodsName || '校服',
      sizeName: item.size || item.sizeName || '',
      mainImage: item.image || item.mainImage || '',
      saleType: String(item.orderType || item.saleType || orderType),
      unitPrice,
      buyNum,
      itemAmount: Number((unitPrice * buyNum).toFixed(2)),
      deptId: Number(item.deptId || selectedStudent.schoolId || selectedStudent.deptId || 1),
      createBy: userName,
      delFlag: '0'
    }
  })
  const productsAmount = xfptOrderItemList.reduce((sum, item) => sum + Number(item.itemAmount || 0), 0)
  const freightAmount = Number(data.freightAmount || 0)
  const totalAmount = Number((productsAmount + freightAmount).toFixed(2))

  return {
    orderNo: `APP${Date.now()}`,
    orderType,
    noticeId: firstItem.noticeId ? Number(firstItem.noticeId) : null,
    schoolId: Number(firstItem.schoolId || selectedStudent.schoolId || selectedStudent.deptId || 1),
    schoolName: firstItem.schoolName || selectedStudent.school || selectedStudent.schoolName || '测试学校',
    supplierId: Number(firstItem.supplierId || 1),
    supplierName: firstItem.supplierName || '测试供应商',
    userId,
    userName,
    studentId: Number(data.studentId || selectedStudent.id || selectedStudent.studentId || 1),
    studentName: selectedStudent.name || selectedStudent.studentName || '测试学生',
    gradeName: selectedStudent.grade || selectedStudent.gradeName || '',
    className: selectedStudent.className || '',
    addressId: Number(data.addressId || selectedAddress.id || 1),
    receiveType: String(data.receiveType || '2'),
    receiverName: selectedAddress.receiver || selectedAddress.name || '家长',
    receiverPhone: selectedAddress.phone || uni.getStorageSync('phone') || '',
    receiverAddress: selectedAddress.fullAddress || '',
    totalAmount,
    payAmount: totalAmount,
    refundAmount: 0,
    payStatus: '0',
    orderStatus: '0',
    deptId: Number(firstItem.deptId || selectedStudent.schoolId || selectedStudent.deptId || 1),
    createBy: userName,
    remark: data.remark || '',
    delFlag: '0',
    xfptOrderItemList
  }
}

function buildOrderUpdate(order = {}, patch = {}) {
  const normalized = normalizeOrder(order)
  return {
    id: normalized.id,
    orderNo: normalized.orderNo,
    orderType: normalized.orderType || '1',
    noticeId: normalized.noticeId,
    schoolId: normalized.schoolId,
    schoolName: normalized.schoolName,
    supplierId: normalized.supplierId,
    supplierName: normalized.supplierName,
    userId: normalized.userId || getCurrentUserId(),
    userName: normalized.userName || getCurrentUserName(),
    studentId: normalized.studentId,
    studentName: normalized.studentName,
    gradeName: normalized.gradeName,
    className: normalized.className,
    addressId: normalized.addressId,
    receiveType: normalized.receiveType || '2',
    receiverName: normalized.receiverName,
    receiverPhone: normalized.receiverPhone,
    receiverAddress: normalized.receiverAddress,
    totalAmount: Number(normalized.totalAmount || 0),
    payAmount: Number(normalized.payAmount || 0),
    refundAmount: Number(normalized.refundAmount || 0),
    payStatus: order.payStatus || '0',
    orderStatus: order.orderStatus || '0',
    deptId: normalized.schoolId || order.deptId || 1,
    updateBy: getCurrentUserName(),
    remark: normalized.remark || '',
    delFlag: order.delFlag || '0',
    xfptOrderItemList: (order.xfptOrderItemList || normalized.orderItems || []).map((item) => ({
      id: item.id,
      orderId: normalized.id,
      goodsId: Number(item.goodsId || item.uniformId || item.id || 0),
      sizeId: Number(item.sizeId || item.skuId || 0),
      goodsName: item.goodsName || item.name || '校服',
      sizeName: item.sizeName || item.size || '',
      mainImage: item.mainImage || item.image || '',
      saleType: String(item.saleType || item.orderType || normalized.orderType || '1'),
      unitPrice: Number(item.unitPrice || item.price || 0),
      buyNum: Number(item.buyNum || item.quantity || 1),
      itemAmount: Number(item.itemAmount || (Number(item.unitPrice || item.price || 0) * Number(item.buyNum || item.quantity || 1)).toFixed(2)),
      deptId: Number(item.deptId || normalized.schoolId || order.deptId || 1),
      delFlag: item.delFlag || '0'
    })),
    ...patch
  }
}

async function fetchBackendOrder(id) {
  const res = await request({
    url: `/school/orderInfo/${id}`,
    method: 'GET'
  })
  return res.data || res
}

export function listOrders(params = {}) {
  const { status, ...rest } = params
  const query = {
    ...mapStatusToQuery(status),
    ...rest
  }
  return request({
    url: '/school/orderInfo/list',
    method: 'GET',
    params: query
  }).then((res) => ({
    ...res,
    rows: (res.rows || []).map(normalizeOrder)
  }))
}

export function getOrder(id) {
  return request({
    url: `/school/orderInfo/${id}`,
    method: 'GET'
  }).then((res) => ({
    ...res,
    data: normalizeOrder(res.data || res)
  }))
}

export function createOrder(data) {
  const payload = buildOrderPayload(data)
  return request({
    url: '/school/orderInfo',
    method: 'POST',
    data: payload
  }).then((res) => ({
    ...res,
    data: {
      ...(res.data || {}),
      orderNo: payload.orderNo,
      xfptOrderItemList: payload.xfptOrderItemList,
      totalAmount: payload.totalAmount,
      payAmount: payload.payAmount,
      schoolName: payload.schoolName,
      studentName: payload.studentName,
      gradeName: payload.gradeName,
      className: payload.className,
      receiverName: payload.receiverName,
      receiverPhone: payload.receiverPhone,
      receiverAddress: payload.receiverAddress,
      orderType: payload.orderType,
      supplierName: payload.supplierName
    }
  }))
}

export async function cancelOrder(id) {
  const order = await fetchBackendOrder(id)
  return request({
    url: '/school/orderInfo',
    method: 'PUT',
    data: buildOrderUpdate(order, {
      payStatus: order.payStatus || '0',
      orderStatus: '5'
    })
  })
}

export async function confirmOrder(id) {
  const order = await fetchBackendOrder(id)
  return request({
    url: '/school/orderInfo',
    method: 'PUT',
    data: buildOrderUpdate(order, {
      payStatus: '1',
      orderStatus: '4'
    })
  })
}

export async function payOrder(id) {
  const order = await fetchBackendOrder(id)
  return request({
    url: '/school/orderInfo',
    method: 'PUT',
    data: buildOrderUpdate(order, {
      payStatus: '1',
      orderStatus: '2'
    })
  })
}
