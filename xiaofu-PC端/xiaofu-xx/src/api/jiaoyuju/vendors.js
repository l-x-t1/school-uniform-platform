import request from '@/utils/request'

// 获取厂商列表
export function getVendorsList(params) {
  return request({
    url: '/school/supplierMessage/list',
    method: 'get',
    params: params
  })
}

// 添加解除黑名单/school/supplierMessage
export function addOrRemoveBlackList(params) {
  return request({
    url: '/school/supplierMessage',
    method: 'put',
    data: params
  })
}

// 查询服务学校列表
export function getSchoolList(params) {
  return request({
    url: '/school/supplierMessage/schoolList',
    method: 'get',
    params: params
  })
}

// 获取供应商详情
export function getVendorDetail(params) {
  return request({
    url: '/school/supplierMessage/' + params.id,
    method: 'get'
  })
}

// 导出数据
export function exportData(params) {
  return request({
    url: '/school/supplierMessage/export',
    method: 'get',
    params: params
  })
}

// 修改供应商信息
export function updateSupplierMessage(data) {
  return request({
    url: '/school/supplierMessage',
    method: 'put',
    data: data
  })
}

// 删除供应商信息
export function deleteSupplierMessage(id) {
  return request({
    url: '/school/supplierMessage/' + id,
    method: 'delete'
  })
}

// 获取供应商绑定用户
export function getSupplierUsers(supplierId) {
  return request({
    url: '/school/supplierMessage/' + supplierId + '/users',
    method: 'get'
  })
}

// 获取可绑定用户
export function getAvailableUsers(keyword) {
  return request({
    url: '/school/supplierMessage/availableUsers',
    method: 'get',
    params: { keyword }
  })
}

// 绑定用户
export function bindSupplierUser(data) {
  return request({
    url: '/school/supplierMessage/bindUser',
    method: 'post',
    data: data
  })
}

// 解绑用户
export function unbindSupplierUser(data) {
  return request({
    url: '/school/supplierMessage/unbindUser',
    method: 'delete',
    data: data
  })
}
