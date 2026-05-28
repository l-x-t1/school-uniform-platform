import request from '@/utils/request'

// 查询教育局结算管理列表
export function listEduSettlementManage(query) {
  return request({
    url: '/school/eduSettlementManage/list',
    method: 'get',
    params: query
  })
}

// 查询教育局结算管理详细
export function getEduSettlementManage(id) {
  return request({
    url: '/school/eduSettlementManage/' + id,
    method: 'get'
  })
}

// 查询教育局结算统计
export function getEduSettlementStatistics(query) {
  return request({
    url: '/school/eduSettlementManage/statistics',
    method: 'get',
    params: query
  })
}
