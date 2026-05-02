import request from '@/utils/request'

// 查询统购公告商品列表
export function listGroupNoticeItem(query) {
  return request({
    url: '/supplier/groupNoticeItem/list',
    method: 'get',
    params: query
  })
}

// 查询统购公告商品详细
export function getGroupNoticeItem(id) {
  return request({
    url: '/supplier/groupNoticeItem/' + id,
    method: 'get'
  })
}

// 新增统购公告商品
export function addGroupNoticeItem(data) {
  return request({
    url: '/supplier/groupNoticeItem',
    method: 'post',
    data: data
  })
}

// 修改统购公告商品
export function updateGroupNoticeItem(data) {
  return request({
    url: '/supplier/groupNoticeItem',
    method: 'put',
    data: data
  })
}

// 删除统购公告商品
export function delGroupNoticeItem(id) {
  return request({
    url: '/supplier/groupNoticeItem/' + id,
    method: 'delete'
  })
}
