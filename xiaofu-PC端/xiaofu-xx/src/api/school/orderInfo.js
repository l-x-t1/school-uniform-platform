import request from '@/utils/request'

// 查询订单信息列表
export function listOrderInfo(query) {
  return request({
    url: '/school/orderInfo/list',
    method: 'get',
    params: query
  })
}

// 查询订单信息详细
export function getOrderInfo(id) {
  return request({
    url: '/school/orderInfo/' + id,
    method: 'get'
  })
}

// 新增订单信息
export function addOrderInfo(data) {
  return request({
    url: '/school/orderInfo',
    method: 'post',
    data: data
  })
}

// 修改订单信息
export function updateOrderInfo(data) {
  return request({
    url: '/school/orderInfo',
    method: 'put',
    data: data
  })
}

// 零售订单发货
export function shipOrderInfo(id) {
  return request({
    url: '/school/orderInfo/' + id + '/ship',
    method: 'put'
  })
}

// 删除订单信息
export function delOrderInfo(id) {
  return request({
    url: '/school/orderInfo/' + id,
    method: 'delete'
  })
}
