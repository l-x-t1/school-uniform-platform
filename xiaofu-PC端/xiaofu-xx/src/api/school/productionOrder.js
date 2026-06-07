import request from '@/utils/request'

// query production order list
export function listProductionOrder(query) {
  return request({
    url: '/school/productionOrder/list',
    method: 'get',
    params: query
  })
}

// query production order detail
export function getProductionOrder(id) {
  return request({
    url: '/school/productionOrder/' + id,
    method: 'get'
  })
}

// query production order selectable notices
export function listProductionNoticeOptions() {
  return request({
    url: '/school/productionOrder/noticeOptions',
    method: 'get'
  })
}

// query production order notice detail
export function getProductionNoticeDetail(noticeId) {
  return request({
    url: '/school/productionOrder/noticeDetail/' + noticeId,
    method: 'get'
  })
}

// add production order
export function addProductionOrder(data) {
  return request({
    url: '/school/productionOrder',
    method: 'post',
    data: data
  })
}

// update production order
export function updateProductionOrder(data) {
  return request({
    url: '/school/productionOrder',
    method: 'put',
    data: data
  })
}

// delete production order
export function delProductionOrder(id) {
  return request({
    url: '/school/productionOrder/' + id,
    method: 'delete'
  })
}

// finish production order
export function finishProductionOrder(id) {
  return request({
    url: '/school/productionOrder/finish/' + id,
    method: 'put'
  })
}

// build delivery order draft from production order
export function generateDeliveryOrder(id) {
  return request({
    url: '/school/productionOrder/generateDelivery/' + id,
    method: 'post'
  })
}
