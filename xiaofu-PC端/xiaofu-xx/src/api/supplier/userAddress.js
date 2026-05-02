import request from '@/utils/request'

// 查询用户收货地址列表
export function listUserAddress(query) {
  return request({
    url: '/supplier/userAddress/list',
    method: 'get',
    params: query
  })
}

// 查询用户收货地址详细
export function getUserAddress(id) {
  return request({
    url: '/supplier/userAddress/' + id,
    method: 'get'
  })
}

// 新增用户收货地址
export function addUserAddress(data) {
  return request({
    url: '/supplier/userAddress',
    method: 'post',
    data: data
  })
}

// 修改用户收货地址
export function updateUserAddress(data) {
  return request({
    url: '/supplier/userAddress',
    method: 'put',
    data: data
  })
}

// 删除用户收货地址
export function delUserAddress(id) {
  return request({
    url: '/supplier/userAddress/' + id,
    method: 'delete'
  })
}
