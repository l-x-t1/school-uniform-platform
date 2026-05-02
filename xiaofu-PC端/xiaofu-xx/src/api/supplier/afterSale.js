import request from '@/utils/request'

// 查询售后单列表
export function listAfterSale(query) {
  return request({
    url: '/supplier/afterSale/list',
    method: 'get',
    params: query
  })
}

// 查询售后单详细
export function getAfterSale(id) {
  return request({
    url: '/supplier/afterSale/' + id,
    method: 'get'
  })
}

// 新增售后单
export function addAfterSale(data) {
  return request({
    url: '/supplier/afterSale',
    method: 'post',
    data: data
  })
}

// 修改售后单
export function updateAfterSale(data) {
  return request({
    url: '/supplier/afterSale',
    method: 'put',
    data: data
  })
}

// 删除售后单
export function delAfterSale(id) {
  return request({
    url: '/supplier/afterSale/' + id,
    method: 'delete'
  })
}
