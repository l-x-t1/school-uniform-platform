import request from '@/utils/request'

// 查询提现申请列表
export function listWithdrawApply(query) {
  return request({
    url: '/supplier/withdrawApply/list',
    method: 'get',
    params: query
  })
}

// 查询提现申请详细
export function getWithdrawApply(id) {
  return request({
    url: '/supplier/withdrawApply/' + id,
    method: 'get'
  })
}

// 新增提现申请
export function addWithdrawApply(data) {
  return request({
    url: '/supplier/withdrawApply',
    method: 'post',
    data: data
  })
}

// 修改提现申请
export function updateWithdrawApply(data) {
  return request({
    url: '/supplier/withdrawApply',
    method: 'put',
    data: data
  })
}

// 删除提现申请
export function delWithdrawApply(id) {
  return request({
    url: '/supplier/withdrawApply/' + id,
    method: 'delete'
  })
}
