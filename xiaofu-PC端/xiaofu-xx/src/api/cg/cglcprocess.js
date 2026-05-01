import request from '@/utils/request'

// 查询采购流程列表
export function listCglcprocess(query) {
  return request({
    url: '/school/cglcprocess/list',
    method: 'get',
    params: query
  })
}

// 查询采购流程详细
export function getCglcprocess(id) {
  return request({
    url: '/school/cglcprocess/' + id,
    method: 'get'
  })
}

// 新增采购流程
export function addCglcprocess(data) {
  return request({
    url: '/school/cglcprocess',
    method: 'post',
    data: data
  })
}

// 修改采购流程
export function updateCglcprocess(data) {
  return request({
    url: '/school/cglcprocess',
    method: 'put',
    data: data
  })
}

// 删除采购流程
export function delCglcprocess(id) {
  return request({
    url: '/school/cglcprocess/' + id,
    method: 'delete'
  })
}
