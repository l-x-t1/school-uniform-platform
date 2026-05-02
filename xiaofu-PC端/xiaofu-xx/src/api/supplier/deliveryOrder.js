import request from '@/utils/request'

// 查询发货单列表
export function listDeliveryOrder(query) {
  return request({
    url: '/supplier/deliveryOrder/list',
    method: 'get',
    params: query
  })
}

// 查询发货单详细
export function getDeliveryOrder(id) {
  return request({
    url: '/supplier/deliveryOrder/' + id,
    method: 'get'
  })
}

// 新增发货单
export function addDeliveryOrder(data) {
  return request({
    url: '/supplier/deliveryOrder',
    method: 'post',
    data: data
  })
}

// 修改发货单
export function updateDeliveryOrder(data) {
  return request({
    url: '/supplier/deliveryOrder',
    method: 'put',
    data: data
  })
}

// 删除发货单
export function delDeliveryOrder(id) {
  return request({
    url: '/supplier/deliveryOrder/' + id,
    method: 'delete'
  })
}
