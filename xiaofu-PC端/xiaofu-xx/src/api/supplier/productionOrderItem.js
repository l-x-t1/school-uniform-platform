import request from '@/utils/request'

// 查询生产单明细列表
export function listProductionOrderItem(query) {
  return request({
    url: '/supplier/productionOrderItem/list',
    method: 'get',
    params: query
  })
}

// 查询生产单明细详细
export function getProductionOrderItem(id) {
  return request({
    url: '/supplier/productionOrderItem/' + id,
    method: 'get'
  })
}

// 新增生产单明细
export function addProductionOrderItem(data) {
  return request({
    url: '/supplier/productionOrderItem',
    method: 'post',
    data: data
  })
}

// 修改生产单明细
export function updateProductionOrderItem(data) {
  return request({
    url: '/supplier/productionOrderItem',
    method: 'put',
    data: data
  })
}

// 删除生产单明细
export function delProductionOrderItem(id) {
  return request({
    url: '/supplier/productionOrderItem/' + id,
    method: 'delete'
  })
}
