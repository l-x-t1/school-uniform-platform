import request from '@/utils/request'

// 查询资金流水列表
export function listFinanceFlow(query) {
  return request({
    url: '/school/financeFlow/list',
    method: 'get',
    params: query
  })
}

// 查询每日入账金额列表
export function listDailyIncome(query) {
  return request({
    url: '/school/financeFlow/dailyIncome/list',
    method: 'get',
    params: query
  })
}

// 查询每日入账金额详情
export function listDailyIncomeDetail(query) {
  return request({
    url: '/school/financeFlow/dailyIncome/detail',
    method: 'get',
    params: query
  })
}

// 查询资金流水详细
export function getFinanceFlow(id) {
  return request({
    url: '/school/financeFlow/' + id,
    method: 'get'
  })
}

// 新增资金流水
export function addFinanceFlow(data) {
  return request({
    url: '/school/financeFlow',
    method: 'post',
    data: data
  })
}

// 修改资金流水
export function updateFinanceFlow(data) {
  return request({
    url: '/school/financeFlow',
    method: 'put',
    data: data
  })
}

// 删除资金流水
export function delFinanceFlow(id) {
  return request({
    url: '/school/financeFlow/' + id,
    method: 'delete'
  })
}
