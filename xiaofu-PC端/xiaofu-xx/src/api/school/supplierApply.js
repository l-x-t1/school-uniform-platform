import request from '@/utils/request'

// 查询供应商报名列表
export function listSupplierApply(query) {
  return request({
    url: '/school/supplierApply/list',
    method: 'get',
    params: query
  })
}

// 查询供应商报名详细
export function getSupplierApply(id) {
  return request({
    url: '/school/supplierApply/' + id,
    method: 'get'
  })
}

// 新增供应商报名
export function addSupplierApply(data) {
  return request({
    url: '/school/supplierApply',
    method: 'post',
    data: data
  })
}

// 修改供应商报名
export function updateSupplierApply(data) {
  return request({
    url: '/school/supplierApply',
    method: 'put',
    data: data
  })
}

// 删除供应商报名
export function delSupplierApply(id) {
  return request({
    url: '/school/supplierApply/' + id,
    method: 'delete'
  })
}
