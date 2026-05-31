import request from '@/utils/request'

// 查询学校端统购上下架列表
export function listTonggouGoods(query) {
  return request({
    url: '/school/tonggouGoods/list',
    method: 'get',
    params: query
  })
}

// 查询学校端统购上下架详情
export function getTonggouGoods(id) {
  return request({
    url: '/school/tonggouGoods/' + id,
    method: 'get'
  })
}

// 修改学校端统购上下架状态
export function updateTonggouGoodsStatus(id, status) {
  return request({
    url: '/school/tonggouGoods/' + id + '/status',
    method: 'put',
    data: { status }
  })
}
