import request from '@/utils/request'

// 查询一校为主模式（模式二）列表
export function listYxwzmode(query) {
  return request({
    url: '/school/yxwzmode/list',
    method: 'get',
    params: query
  })
}

// 查询一校为主模式（模式二）详细
export function getYxwzmode(id) {
  return request({
    url: '/school/yxwzmode/' + id,
    method: 'get'
  })
}

// 新增一校为主模式（模式二）
export function addYxwzmode(data) {
  return request({
    url: '/school/yxwzmode',
    method: 'post',
    data: data
  })
}

// 修改一校为主模式（模式二）
export function updateYxwzmode(data) {
  return request({
    url: '/school/yxwzmode',
    method: 'put',
    data: data
  })
}

// 删除一校为主模式（模式二）
export function delYxwzmode(id) {
  return request({
    url: '/school/yxwzmode/' + id,
    method: 'delete'
  })
}

// 撤回一校为主模式（模式二）
export function withdrawYxwzmode(data) {
  return request({
    url: '/school/yxwzmode/withdraw',
    method: 'post',
    params:data
  })
}
