import request from '@/utils/request'

// 查询学校供应商合作关系列表
export function listSchoolSupplierRel(query) {
  return request({
    url: '/supplier/schoolSupplierRel/list',
    method: 'get',
    params: query
  })
}

// 查询学校供应商合作关系详细
export function getSchoolSupplierRel(id) {
  return request({
    url: '/supplier/schoolSupplierRel/' + id,
    method: 'get'
  })
}

// 新增学校供应商合作关系
export function addSchoolSupplierRel(data) {
  return request({
    url: '/supplier/schoolSupplierRel',
    method: 'post',
    data: data
  })
}

// 修改学校供应商合作关系
export function updateSchoolSupplierRel(data) {
  return request({
    url: '/supplier/schoolSupplierRel',
    method: 'put',
    data: data
  })
}

// 删除学校供应商合作关系
export function delSchoolSupplierRel(id) {
  return request({
    url: '/supplier/schoolSupplierRel/' + id,
    method: 'delete'
  })
}
