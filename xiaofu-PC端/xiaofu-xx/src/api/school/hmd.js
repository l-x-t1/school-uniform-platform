import request from '@/utils/request'

// 查询黑名单列表
export function listHmd(query) {
  return request({
    url: '/school/hmd/list',
    method: 'get',
    params: query
  })
}

// 查询黑名单详细
export function getHmd(id) {
  return request({
    url: '/school/hmd/' + id,
    method: 'get'
  })
}

// 新增黑名单
export function addHmd(data) {
  return request({
    url: '/school/hmd',
    method: 'post',
    data: data
  })
}

// 修改黑名单
export function updateHmd(data) {
  return request({
    url: '/school/hmd',
    method: 'put',
    data: data
  })
}

// 删除黑名单
export function delHmd(id) {
  return request({
    url: '/school/hmd/' + id,
    method: 'delete'
  })
}
