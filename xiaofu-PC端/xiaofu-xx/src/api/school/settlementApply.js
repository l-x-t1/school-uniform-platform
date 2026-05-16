import request from '@/utils/request'

// 查询申请结算列表
export function listSettlementApply(query) {
  return request({
    url: '/school/settlementApply/list',
    method: 'get',
    params: query
  })
}

// 查询申请结算详细
export function getSettlementApply(id) {
  return request({
    url: '/school/settlementApply/' + id,
    method: 'get'
  })
}

// 审核申请结算
export function auditSettlementApply(data) {
  return request({
    url: '/school/settlementApply/audit',
    method: 'put',
    data: data
  })
}
