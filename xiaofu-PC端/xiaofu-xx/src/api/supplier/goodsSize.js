import request from '@/utils/request'

// 查询商品尺码列表
export function listGoodsSize(query) {
  return request({
    url: '/supplier/goodsSize/list',
    method: 'get',
    params: query
  })
}

// 查询商品尺码详细
export function getGoodsSize(id) {
  return request({
    url: '/supplier/goodsSize/' + id,
    method: 'get'
  })
}

// 新增商品尺码
export function addGoodsSize(data) {
  return request({
    url: '/supplier/goodsSize',
    method: 'post',
    data: data
  })
}

// 修改商品尺码
export function updateGoodsSize(data) {
  return request({
    url: '/supplier/goodsSize',
    method: 'put',
    data: data
  })
}

// 删除商品尺码
export function delGoodsSize(id) {
  return request({
    url: '/supplier/goodsSize/' + id,
    method: 'delete'
  })
}
