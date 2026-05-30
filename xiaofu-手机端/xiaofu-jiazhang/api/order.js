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

function mapStatusToQuery(status) {
  const base = { userId: getCurrentUserId() }
  switch (status) {
    case 'unpaid':
      return { ...base, payStatus: '0', orderStatus: '0' }
    case 'unshipped':
      return { ...base, payStatus: '1', orderStatus: '2' }
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

function formatPrice(value) {
  return Number(value || 0).toFixed(2)
}

function getOrderTypeText(orderType) {
  return String(orderType || '1') === '2' ? '零售购买' : '统购购买'
}

function getCurrentTime() {
  const now = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

function normalizeOrderItem(item = {}) {
  const price = item.unitPrice || item.price || item.salePrice || '0.00'
  const quantity = Number(item.buyNum || item.quantity || item.num || 1)
  const orderType = String(item.saleType || item.orderType || '1')
  return {
    id: item.id || item.goodsId,
    orderItemId: item.orderItemId || item.id,
    goodsId: item.goodsId || item.uniformId || item.id,
    uniformId: item.goodsId || item.uniformId || item.id,
    name: item.goodsName || item.name || '校服',
    image: item.mainImage || item.image || item.productImage || item.coverImage || '/static/qjxf.png',
    price,
    quantity,
    size: item.sizeName || item.size || '-',
    sizeName: item.sizeName || item.size || '-',
    color: item.colorName || item.color || item.spec || '-',
    supplierId: item.supplierId,
    supplierName: item.supplierName || '',
    noticeId: item.noticeId || null,
    saleType: orderType,
    orderType,
    orderTypeText: getOrderTypeText(orderType),
    itemAmount: formatPrice(item.itemAmount || Number(price) * quantity)
  }
}

function normalizeOrder(order = {}) {
  const items = order.xfptOrderItemList || order.orderItems || order.items || []
  return {
    ...order,
    status: mapBackendStatus(order.orderStatus, order.payStatus),
    orderItems: items.map(normalizeOrderItem),
    items: items.map(normalizeOrderItem),
    products: items.map(normalizeOrderItem),
    address: {
      name: order.receiverName || '家长',
      phone: order.receiverPhone || '',
      fullAddress: order.receiverAddress || '',
      tag: String(order.receiveType || '') === '2' ? '家庭' : '学校'
    }
  }
}

function resolveOrderItemId(item = {}) {
  if (item.orderItemId) {
    return item.orderItemId
  }
  return null
}

function buildOrderItemsPayload(items = [], options = {}) {
  const {
    orderType = '1',
    deptId,
    operatorName = getCurrentUserName()
  } = options

  return items.map((item) => {
    const price = item.unitPrice || item.price || item.salePrice || '0.00'
    const quantity = Number(item.buyNum || item.quantity || item.num || 1)
    return {
      id: resolveOrderItemId(item),
      orderId: item.orderId || null,
      goodsId: item.goodsId || item.uniformId || item.productId || item.id,
      sizeId: item.sizeId || item.skuId || null,
      goodsName: item.goodsName || item.name || item.productName || item.uniformName || '校服',
      sizeName: item.sizeName || item.size || '-',
      mainImage: item.mainImage || item.image || item.productImage || item.coverImage || '/static/qjxf.png',
      saleType: String(item.saleType || item.orderType || orderType || '1'),
      unitPrice: price,
      buyNum: quantity,
      itemAmount: formatPrice(item.itemAmount || Number(price || 0) * quantity),
      deptId: item.deptId || item.schoolId || deptId || 1,
      createBy: item.createBy || operatorName,
      updateBy: operatorName,
      remark: item.remark || '',
      delFlag: item.delFlag || '0'
    }
  })
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
  const totalAmount = items.reduce((sum, item) => sum + Number(item.price || item.unitPrice || 0) * Number(item.quantity || 1), 0)

  const orderType = String(data.orderType || items[0]?.orderType || items[0]?.saleType || '2')
  const noticeId = orderType === '1'
    ? Number(data.noticeId || items[0]?.noticeId || 0) || null
    : null
  const deptId = Number(selectedStudent.deptId || selectedStudent.schoolId || 1)
  return {
    orderNo: `APP${Date.now()}`,
    orderType,
    noticeId,
    schoolId: Number(selectedStudent.schoolId || 1),
    schoolName: selectedStudent.school || selectedStudent.schoolName || '测试学校',
    supplierId: Number(items[0]?.supplierId || 1),
    supplierName: items[0]?.supplierName || '测试供应商',
    userId,
    userName,
    studentId: Number(data.studentId || selectedStudent.id || 1),
    studentName: selectedStudent.name || selectedStudent.studentName || '测试学生',
    gradeName: selectedStudent.grade || selectedStudent.gradeName || '',
    className: selectedStudent.className || '',
    addressId: Number(data.addressId || selectedAddress.id || 1),
    receiveType: '2',
    receiverName: selectedAddress.receiver || selectedAddress.name || '家长',
    receiverPhone: selectedAddress.phone || uni.getStorageSync('phone') || '',
    receiverAddress: selectedAddress.fullAddress || '',
    totalAmount: Number(totalAmount.toFixed(2)),
    payAmount: Number(totalAmount.toFixed(2)),
    refundAmount: 0,
    payStatus: '0',
    orderStatus: '0',
    deptId,
    createBy: userName,
    remark: data.remark || '',
    delFlag: '0',
    xfptOrderItemList: buildOrderItemsPayload(items, {
      orderType,
      deptId,
      operatorName: userName
    })
  }
}

function buildOrderUpdate(order = {}, patch = {}) {
  const orderItems = order.xfptOrderItemList || order.orderItems || order.items || []
  return {
    id: order.id,
    orderNo: order.orderNo,
    orderType: order.orderType || '2',
    noticeId: order.noticeId,
    schoolId: order.schoolId,
    schoolName: order.schoolName,
    supplierId: order.supplierId,
    supplierName: order.supplierName,
    userId: order.userId || getCurrentUserId(),
    userName: order.userName || getCurrentUserName(),
    studentId: order.studentId,
    studentName: order.studentName,
    gradeName: order.gradeName,
    className: order.className,
    addressId: order.addressId,
    receiveType: order.receiveType || '2',
    receiverName: order.receiverName,
    receiverPhone: order.receiverPhone,
    receiverAddress: order.receiverAddress,
    totalAmount: order.totalAmount,
    payAmount: order.payAmount,
    refundAmount: order.refundAmount || 0,
    payStatus: order.payStatus || '0',
    orderStatus: order.orderStatus || '0',
    payTime: order.payTime,
    cancelTime: order.cancelTime,
    finishTime: order.finishTime,
    deptId: order.deptId || 1,
    updateBy: getCurrentUserName(),
    remark: order.remark || '',
    delFlag: order.delFlag || '0',
    xfptOrderItemList: buildOrderItemsPayload(orderItems, {
      orderType: order.orderType || '2',
      deptId: order.deptId || order.schoolId || 1,
      operatorName: getCurrentUserName()
    }),
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

async function findOrderByOrderNo(orderNo) {
  if (!orderNo) {
    return null
  }
  const res = await request({
    url: '/school/orderInfo/list',
    method: 'GET',
    params: {
      userId: getCurrentUserId(),
      orderNo,
      pageNum: 1,
      pageSize: 10
    }
  })
  const rows = res.rows || []
  return rows.find((item) => String(item.orderNo || '') === String(orderNo)) || rows[0] || null
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
  }).then(async (res) => {
    const responseData = res.data || {}
    let orderData = {
      ...responseData,
      orderNo: responseData.orderNo || payload.orderNo
    }

    if (!orderData.id && !orderData.orderId) {
      const createdOrder = await findOrderByOrderNo(payload.orderNo)
      if (createdOrder) {
        orderData = {
          ...createdOrder,
          orderId: createdOrder.id || createdOrder.orderId,
          orderNo: createdOrder.orderNo || payload.orderNo
        }
      }
    }

    return {
      ...res,
      data: orderData
    }
  })
}

export async function cancelOrder(id) {
  const order = await fetchBackendOrder(id)
  return request({
    url: '/school/orderInfo',
    method: 'PUT',
    data: buildOrderUpdate(order, {
      payStatus: order.payStatus || '0',
      orderStatus: '5',
      cancelTime: getCurrentTime()
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
      orderStatus: '4',
      payTime: order.payTime || getCurrentTime(),
      finishTime: getCurrentTime()
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
      orderStatus: '2',
      payTime: getCurrentTime()
    })
  })
}

export async function deleteOrder(id) {
  return request({
    url: `/school/orderInfo/${id}`,
    method: 'DELETE'
  })
}
