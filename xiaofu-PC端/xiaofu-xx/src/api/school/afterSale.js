import request from '@/utils/request'

// 查询售后主列表
export function listAfterSale(query) {
  return request({
    url: '/school/afterSale/list',
    method: 'get',
    params: query
  })
}

// 查询售后主详细
export function getAfterSale(id) {
  return request({
    url: '/school/afterSale/' + id,
    method: 'get'
  })
}

// 新增售后主
export function addAfterSale(data) {
  return request({
    url: '/school/afterSale',
    method: 'post',
    data: data
  })
}

// 修改售后主
export function updateAfterSale(data) {
  return request({
    url: '/school/afterSale',
    method: 'put',
    data: data
  })
}

// 删除售后主
export function delAfterSale(id) {
  return request({
    url: '/school/afterSale/' + id,
    method: 'delete'
  })
}
