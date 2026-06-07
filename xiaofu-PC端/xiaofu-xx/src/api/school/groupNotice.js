import request from '@/utils/request'

// 查询统购公告列表
export function listGroupNotice(query) {
  return request({
    url: '/school/groupNotice/list',
    method: 'get',
    params: query
  })
}

// 查询统购公告详细
export function getGroupNotice(id) {
  return request({
    url: '/school/groupNotice/' + id,
    method: 'get'
  })
}

// 供应商查询学校发布的统购公告列表
export function listSupplierGroupNotice(query) {
  return request({
    url: '/school/groupNotice/supplier/list',
    method: 'get',
    params: query
  })
}

// 供应商查询学校发布的统购公告详情
export function getSupplierGroupNotice(id) {
  return request({
    url: '/school/groupNotice/supplier/' + id,
    method: 'get'
  })
}

// 查询统购公告可选合作供应商
export function listGroupNoticeSupplierOptions(query) {
  return request({
    url: '/school/groupNotice/supplierOptions',
    method: 'get',
    params: query
  })
}

// 查询统购公告可选合作商品
export function listGroupNoticeGoodsOptions(query) {
  return request({
    url: '/school/groupNotice/goodsOptions',
    method: 'get',
    params: query
  })
}

// 查询统购公告可选商品尺码
export function listGroupNoticeGoodsSizeOptions(query) {
  return request({
    url: '/school/groupNotice/goodsSizeOptions',
    method: 'get',
    params: query
  })
}

// 新增统购公告
export function addGroupNotice(data) {
  return request({
    url: '/school/groupNotice',
    method: 'post',
    data: data
  })
}

// 修改统购公告
export function updateGroupNotice(data) {
  return request({
    url: '/school/groupNotice',
    method: 'put',
    data: data
  })
}

// 删除统购公告
export function delGroupNotice(id) {
  return request({
    url: '/school/groupNotice/' + id,
    method: 'delete'
  })
}

// 查询统购订购进度列表
export function listGroupNoticeProgress(query) {
  return request({
    url: '/school/groupNotice/progress/list',
    method: 'get',
    params: query
  })
}

// 查询统购订购进度详情
export function getGroupNoticeProgressDetail(orderId, supplierId) {
  return request({
    url: '/school/groupNotice/progress/' + orderId,
    method: 'get',
    params: { supplierId }
  })
}

// 供应商查看统购订单
export function viewGroupNoticeProgressOrder(orderId) {
  return request({
    url: '/school/groupNotice/progress/order/' + orderId,
    method: 'get'
  })
}

// 供应商开始制作统购订单
export function startGroupNoticeProgressOrder(orderId) {
  return request({
    url: '/school/groupNotice/progress/order/' + orderId + '/start',
    method: 'put'
  })
}

// 供应商完成制作统购订单
export function finishGroupNoticeProgressOrder(orderId) {
  return request({
    url: '/school/groupNotice/progress/order/' + orderId + '/finish',
    method: 'put'
  })
}

// 供应商填写统购订单物流单号
export function fillGroupNoticeProgressLogistics(orderId, logisticsNo) {
  return request({
    url: '/school/groupNotice/progress/order/' + orderId + '/logistics',
    method: 'put',
    data: { logisticsNo }
  })
}
