import request from '@/utils/request'

// 查询采购汇总信息列表
export function listSummary(query) {
  return request({
    url: '/school/summary/list',
    method: 'get',
    params: query
  })
}

// 查询采购汇总信息详细
export function getSummary(id) {
  return request({
    url: '/school/summary/' + id,
    method: 'get'
  })
}

// 新增采购汇总信息
export function addSummary(data) {
  return request({
    url: '/school/summary',
    method: 'post',
    data: data
  })
}

// 修改采购汇总信息
export function updateSummary(data) {
  return request({
    url: '/school/summary',
    method: 'put',
    data: data
  })
}

// 删除采购汇总信息
export function delSummary(id) {
  return request({
    url: '/school/summary/' + id,
    method: 'delete'
  })
}
