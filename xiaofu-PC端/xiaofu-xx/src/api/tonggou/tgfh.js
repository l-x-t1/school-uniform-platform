import request from '@/utils/request'

// 查询发货单统计列表
export function listDeliveryStatistics(query) {
  return request({
    url: '/school/deliveryOrder/statistics/list',
    method: 'get',
    params: query
  })
}

// 查询发货单统计详情
export function getDeliveryStatistics(id) {
  return request({
    url: '/school/deliveryOrder/statistics/' + id,
    method: 'get'
  })
}
