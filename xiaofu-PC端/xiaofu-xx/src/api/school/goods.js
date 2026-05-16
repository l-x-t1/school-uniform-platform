import request from '@/utils/request'

// 查询商品信息列表
export function listGoods(query) {
  return request({
    url: '/school/goods/list',
    method: 'get',
    params: query
  })
}

// 查询商品信息详细
export function getGoods(id) {
  return request({
    url: '/school/goods/' + id,
    method: 'get'
  })
}

// 新增商品信息
export function addGoods(data) {
  return request({
    url: '/school/goods',
    method: 'post',
    data: data
  })
}

// 修改商品信息
export function updateGoods(data) {
  return request({
    url: '/school/goods',
    method: 'put',
    data: data
  })
}

// 导入商品信息
export function importGoods(data, updateSupport) {
  return request({
    url: '/school/goods/importData?updateSupport=' + updateSupport,
    method: 'post',
    data: data
  })
}

// 删除商品信息
export function delGoods(id) {
  return request({
    url: '/school/goods/' + id,
    method: 'delete'
  })
}
