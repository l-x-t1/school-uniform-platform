import request from '@/utils/request'

// 查询购物车明细列表
export function listCartItem(query) {
  return request({
    url: '/supplier/cartItem/list',
    method: 'get',
    params: query
  })
}

// 查询购物车明细详细
export function getCartItem(id) {
  return request({
    url: '/supplier/cartItem/' + id,
    method: 'get'
  })
}

// 新增购物车明细
export function addCartItem(data) {
  return request({
    url: '/supplier/cartItem',
    method: 'post',
    data: data
  })
}

// 修改购物车明细
export function updateCartItem(data) {
  return request({
    url: '/supplier/cartItem',
    method: 'put',
    data: data
  })
}

// 删除购物车明细
export function delCartItem(id) {
  return request({
    url: '/supplier/cartItem/' + id,
    method: 'delete'
  })
}
