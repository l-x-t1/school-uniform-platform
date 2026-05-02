import request from '@/utils/request'

// 查询结算明细列表
export function listSettlementOrderItem(query) {
  return request({
    url: '/supplier/settlementOrderItem/list',
    method: 'get',
    params: query
  })
}

// 查询结算明细详细
export function getSettlementOrderItem(id) {
  return request({
    url: '/supplier/settlementOrderItem/' + id,
    method: 'get'
  })
}

// 新增结算明细
export function addSettlementOrderItem(data) {
  return request({
    url: '/supplier/settlementOrderItem',
    method: 'post',
    data: data
  })
}

// 修改结算明细
export function updateSettlementOrderItem(data) {
  return request({
    url: '/supplier/settlementOrderItem',
    method: 'put',
    data: data
  })
}

// 删除结算明细
export function delSettlementOrderItem(id) {
  return request({
    url: '/supplier/settlementOrderItem/' + id,
    method: 'delete'
  })
}
