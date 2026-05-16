import request from '@/utils/request'

// 查询结算管理列表
export function listSettlementManage(query) {
  return request({
    url: '/school/settlementManage/list',
    method: 'get',
    params: query
  })
}

// 查询结算管理详细
export function getSettlementManage(id) {
  return request({
    url: '/school/settlementManage/' + id,
    method: 'get'
  })
}

// 执行结算
export function settleSettlementManage(data) {
  return request({
    url: '/school/settlementManage/settle',
    method: 'put',
    data: data
  })
}
