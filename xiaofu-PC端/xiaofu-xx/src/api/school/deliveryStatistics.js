import request from '@/utils/request'

// 查询发货单统计列表
export function listDeliveryStatistics(query) {
  return request({
    url: '/school/deliveryStatistics/list',
    method: 'get',
    params: query
  })
}
