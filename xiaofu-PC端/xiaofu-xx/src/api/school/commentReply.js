import request from '@/utils/request'

// 查询商品评论回复列表
export function listCommentReply(query) {
  return request({
    url: '/school/commentReply/list',
    method: 'get',
    params: query
  })
}

// 查询商品评论回复详细
export function getCommentReply(id) {
  return request({
    url: '/school/commentReply/' + id,
    method: 'get'
  })
}

// 新增商品评论回复
export function addCommentReply(data) {
  return request({
    url: '/school/commentReply',
    method: 'post',
    data: data
  })
}

// 修改商品评论回复
export function updateCommentReply(data) {
  return request({
    url: '/school/commentReply',
    method: 'put',
    data: data
  })
}

// 删除商品评论回复
export function delCommentReply(id) {
  return request({
    url: '/school/commentReply/' + id,
    method: 'delete'
  })
}
