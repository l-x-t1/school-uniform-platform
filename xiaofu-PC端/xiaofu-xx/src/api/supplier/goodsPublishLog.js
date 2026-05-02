import request from '@/utils/request'

// 查询商品上下架日志列表
export function listGoodsPublishLog(query) {
  return request({
    url: '/supplier/goodsPublishLog/list',
    method: 'get',
    params: query
  })
}

// 查询商品上下架日志详细
export function getGoodsPublishLog(id) {
  return request({
    url: '/supplier/goodsPublishLog/' + id,
    method: 'get'
  })
}

// 新增商品上下架日志
export function addGoodsPublishLog(data) {
  return request({
    url: '/supplier/goodsPublishLog',
    method: 'post',
    data: data
  })
}

// 修改商品上下架日志
export function updateGoodsPublishLog(data) {
  return request({
    url: '/supplier/goodsPublishLog',
    method: 'put',
    data: data
  })
}

// 删除商品上下架日志
export function delGoodsPublishLog(id) {
  return request({
    url: '/supplier/goodsPublishLog/' + id,
    method: 'delete'
  })
}
