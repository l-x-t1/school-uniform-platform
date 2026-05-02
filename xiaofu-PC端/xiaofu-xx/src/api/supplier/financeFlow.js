import request from '@/utils/request'

// 查询财务流水列表
export function listFinanceFlow(query) {
  return request({
    url: '/supplier/financeFlow/list',
    method: 'get',
    params: query
  })
}

// 查询财务流水详细
export function getFinanceFlow(id) {
  return request({
    url: '/supplier/financeFlow/' + id,
    method: 'get'
  })
}

// 新增财务流水
export function addFinanceFlow(data) {
  return request({
    url: '/supplier/financeFlow',
    method: 'post',
    data: data
  })
}

// 修改财务流水
export function updateFinanceFlow(data) {
  return request({
    url: '/supplier/financeFlow',
    method: 'put',
    data: data
  })
}

// 删除财务流水
export function delFinanceFlow(id) {
  return request({
    url: '/supplier/financeFlow/' + id,
    method: 'delete'
  })
}
