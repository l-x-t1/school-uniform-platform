import request from '@/utils/request'

// 查询统购公告列表
export function listGroupNotice(query) {
  return request({
    url: '/school/groupNotice/list',
    method: 'get',
    params: query
  })
}

// 查询统购公告详细
export function getGroupNotice(id) {
  return request({
    url: '/school/groupNotice/' + id,
    method: 'get'
  })
}

// 新增统购公告
export function addGroupNotice(data) {
  return request({
    url: '/school/groupNotice',
    method: 'post',
    data: data
  })
}

// 修改统购公告
export function updateGroupNotice(data) {
  return request({
    url: '/school/groupNotice',
    method: 'put',
    data: data
  })
}

// 删除统购公告
export function delGroupNotice(id) {
  return request({
    url: '/school/groupNotice/' + id,
    method: 'delete'
  })
}
