import request from '@/utils/request'

// 查询发货单明细列表
export function listDeliveryOrderItem(query) {
  return request({
    url: '/school/deliveryOrderItem/list',
    method: 'get',
    params: query
  })
}

// 查询发货单明细详细
export function getDeliveryOrderItem(id) {
  return request({
    url: '/school/deliveryOrderItem/' + id,
    method: 'get'
  })
}

// 新增发货单明细
export function addDeliveryOrderItem(data) {
  return request({
    url: '/school/deliveryOrderItem',
    method: 'post',
    data: data
  })
}

// 修改发货单明细
export function updateDeliveryOrderItem(data) {
  return request({
    url: '/school/deliveryOrderItem',
    method: 'put',
    data: data
  })
}

// 删除发货单明细
export function delDeliveryOrderItem(id) {
  return request({
    url: '/school/deliveryOrderItem/' + id,
    method: 'delete'
  })
}
