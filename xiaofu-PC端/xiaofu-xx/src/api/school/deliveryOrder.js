import request from '@/utils/request'

export function listDeliveryOrder(query) {
  return request({
    url: '/school/deliveryOrder/list',
    method: 'get',
    params: query
  })
}

export function listDeliveryOrderStatistics(query) {
  return request({
    url: '/school/deliveryOrder/statistics/list',
    method: 'get',
    params: query
  })
}

export function listDeliveryOrderStatisticsSuppliers() {
  return request({
    url: '/school/deliveryOrder/statistics/suppliers',
    method: 'get'
  })
}

export function getDeliveryOrder(id) {
  return request({
    url: '/school/deliveryOrder/' + id,
    method: 'get'
  })
}

export function addDeliveryOrder(data) {
  return request({
    url: '/school/deliveryOrder',
    method: 'post',
    data: data
  })
}

export function updateDeliveryOrder(data) {
  return request({
    url: '/school/deliveryOrder',
    method: 'put',
    data: data
  })
}

export function delDeliveryOrder(id) {
  return request({
    url: '/school/deliveryOrder/' + id,
    method: 'delete'
  })
}
