import request from '@/utils/request'

// 查询学校供应商合作商品列表
export function listSchoolSupplierGoodsRel(query) {
  return request({
    url: '/school/schoolSupplierGoodsRel/list',
    method: 'get',
    params: query
  })
}

// 查询学校供应商合作商品详细
export function getSchoolSupplierGoodsRel(id) {
  return request({
    url: '/school/schoolSupplierGoodsRel/' + id,
    method: 'get'
  })
}

// 新增学校供应商合作商品
export function addSchoolSupplierGoodsRel(data) {
  return request({
    url: '/school/schoolSupplierGoodsRel',
    method: 'post',
    data: data
  })
}

// 修改学校供应商合作商品
export function updateSchoolSupplierGoodsRel(data) {
  return request({
    url: '/school/schoolSupplierGoodsRel',
    method: 'put',
    data: data
  })
}

// 删除学校供应商合作商品
export function delSchoolSupplierGoodsRel(id) {
  return request({
    url: '/school/schoolSupplierGoodsRel/' + id,
    method: 'delete'
  })
}
