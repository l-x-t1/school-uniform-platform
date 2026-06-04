import request from '@/utils/request'

// 查询供应商提现申请列表
export function listWithdrawApply(query) {
  return request({
    url: '/school/withdrawApply/list',
    method: 'get',
    params: query
  })
}

// 查询供应商提现申请详细
export function getWithdrawApply(id) {
  return request({
    url: '/school/withdrawApply/' + id,
    method: 'get'
  })
}

// 新增供应商提现申请
export function addWithdrawApply(data) {
  return request({
    url: '/school/withdrawApply',
    method: 'post',
    data: data
  })
}

// 修改供应商提现申请
export function updateWithdrawApply(data) {
  return request({
    url: '/school/withdrawApply',
    method: 'put',
    data: data
  })
}

// 学校端审核供应商提现申请
export function auditWithdrawApply(data) {
  return request({
    url: '/school/withdrawApply/audit',
    method: 'put',
    data: data
  })
}

// 删除供应商提现申请
export function delWithdrawApply(id) {
  return request({
    url: '/school/withdrawApply/' + id,
    method: 'delete'
  })
}
