import request from '@/utils/request'

// 查询利旧回收列表
export function listLjhs(query) {
  return request({
    url: '/school/ljhs/list',
    method: 'get',
    params: query
  })
}

// 查询利旧回收详细
export function getLjhs(id) {
  return request({
    url: '/school/ljhs/' + id,
    method: 'get'
  })
}

// 新增利旧回收
export function addLjhs(data) {
  return request({
    url: '/school/ljhs',
    method: 'post',
    data: data
  })
}

// 修改利旧回收
export function updateLjhs(data) {
  return request({
    url: '/school/ljhs',
    method: 'put',
    data: data
  })
}

// 删除利旧回收
export function delLjhs(id) {
  return request({
    url: '/school/ljhs/' + id,
    method: 'delete'
  })
}

// 确认回收
export function confirmLjhs(data) {
  return request({
    url: '/school/ljhs/confirm',
    method: 'post',
    data: data
  })
}

