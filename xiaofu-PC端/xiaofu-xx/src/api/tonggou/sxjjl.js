import request from '@/utils/request'

// 查询尺码调换记录列表
export function listSizeExchange(query) {
  return request({
    url: '/school/sizeExchange/list',
    method: 'get',
    params: query
  })
}
