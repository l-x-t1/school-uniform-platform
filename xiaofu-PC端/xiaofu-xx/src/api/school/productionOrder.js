import request from '@/utils/request'

// 查询生产单列表
export function listProductionOrder(query) {
  return request({
    url: '/school/productionOrder/list',
    method: 'get',
    params: query
  })
}

// 查询生产单详细
export function getProductionOrder(id) {
  return request({
    url: '/school/productionOrder/' + id,
    method: 'get'
  })
}

// 新增生产单
export function addProductionOrder(data) {
  return request({
    url: '/school/productionOrder',
    method: 'post',
    data: data
  })
}

// 修改生产单
export function updateProductionOrder(data) {
  return request({
    url: '/school/productionOrder',
    method: 'put',
    data: data
  })
}

// 删除生产单
export function delProductionOrder(id) {
  return request({
    url: '/school/productionOrder/' + id,
    method: 'delete'
  })
}

// 生产完成
export function finishProductionOrder(id) {
  return request({
    url: '/school/productionOrder/finish/' + id,
    method: 'put'
  })
}

// 生成发货单
export function generateDeliveryOrder(id) {
  return request({
    url: '/school/productionOrder/generateDelivery/' + id,
    method: 'post'
  })
}
