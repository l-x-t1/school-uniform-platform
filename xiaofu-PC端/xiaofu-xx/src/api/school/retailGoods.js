import request from '@/utils/request'

// 查询学校端零售商品列表
export function listRetailGoods(query) {
  return request({
    url: '/school/retailGoods/list',
    method: 'get',
    params: query
  })
}

// 查询学校端零售商品详情
export function getRetailGoods(id) {
  return request({
    url: '/school/retailGoods/' + id,
    method: 'get'
  })
}

// 修改学校端零售商品上下架状态
export function updateRetailGoodsStatus(id, status) {
  return request({
    url: '/school/retailGoods/' + id + '/status',
    method: 'put',
    data: { status }
  })
}
