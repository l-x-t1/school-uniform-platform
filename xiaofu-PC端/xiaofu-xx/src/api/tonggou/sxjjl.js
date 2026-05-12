import request from '@/utils/request'

// 查询尺码调换记录列表
export function listSizeExchange(query) {
  return request({
    url: '/school/sizeExchange/list',
    method: 'get',
    params: query
  })
}

// 查询尺码调换记录详情
export function getSizeExchange(id) {
  return request({
    url: '/school/sizeExchange/' + id,
    method: 'get'
  })
}
