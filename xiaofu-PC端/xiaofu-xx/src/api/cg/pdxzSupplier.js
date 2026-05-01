import request from '@/utils/request'

// 查询评定小组确定供应商列表
export function listPdxzSupplier(query) {
  return request({
    url: '/school/pdxzSupplier/list',
    method: 'get',
    params: query
  })
}

// 查询评定小组确定供应商详细
export function getPdxzSupplier(id) {
  return request({
    url: '/school/pdxzSupplier/' + id,
    method: 'get'
  })
}

// 新增评定小组确定供应商
export function addPdxzSupplier(data) {
  return request({
    url: '/school/pdxzSupplier',
    method: 'post',
    data: data
  })
}

// 修改评定小组确定供应商
export function updatePdxzSupplier(data) {
  return request({
    url: '/school/pdxzSupplier',
    method: 'put',
    data: data
  })
}

// 删除评定小组确定供应商
export function delPdxzSupplier(id) {
  return request({
    url: '/school/pdxzSupplier/' + id,
    method: 'delete'
  })
}


//查询厂商列表
export function getCompanyInfo(teamId) {
  return request({
    url: '/school/pdxzSupplier/getSupplie?teamId=' + teamId,
    method: 'get',
  })
}

