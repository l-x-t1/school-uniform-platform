import request from '@/utils/request'

// 查询结算单列表
export function listSettlementOrder(query) {
  return request({
    url: '/supplier/settlementOrder/list',
    method: 'get',
    params: query
  })
}

// 查询结算单详细
export function getSettlementOrder(id) {
  return request({
    url: '/supplier/settlementOrder/' + id,
    method: 'get'
  })
}

// 新增结算单
export function addSettlementOrder(data) {
  return request({
    url: '/supplier/settlementOrder',
    method: 'post',
    data: data
  })
}

// 修改结算单
export function updateSettlementOrder(data) {
  return request({
    url: '/supplier/settlementOrder',
    method: 'put',
    data: data
  })
}

// 删除结算单
export function delSettlementOrder(id) {
  return request({
    url: '/supplier/settlementOrder/' + id,
    method: 'delete'
  })
}
