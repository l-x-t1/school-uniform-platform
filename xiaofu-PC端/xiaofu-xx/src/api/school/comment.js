import request from '@/utils/request'

// 查询商品评价列表
export function listComment(query) {
  return request({
    url: '/school/comment/list',
    method: 'get',
    params: query
  })
}

// 查询评价管理列表
export function listEvaluationComment(query) {
  return request({
    url: '/school/comment/evaluation/list',
    method: 'get',
    params: query
  })
}

// 查询评价管理详细
export function getEvaluationComment(id) {
  return request({
    url: '/school/comment/evaluation/' + id,
    method: 'get'
  })
}

// 回复评价
export function replyEvaluationComment(id, data) {
  return request({
    url: '/school/comment/evaluation/' + id + '/reply',
    method: 'post',
    data: data
  })
}

// 隐藏或恢复评价
export function updateEvaluationCommentStatus(id, data) {
  return request({
    url: '/school/comment/evaluation/' + id + '/status',
    method: 'put',
    data: data
  })
}

// 查询商品评价详细
export function getComment(id) {
  return request({
    url: '/school/comment/' + id,
    method: 'get'
  })
}

// 新增商品评价
export function addComment(data) {
  return request({
    url: '/school/comment',
    method: 'post',
    data: data
  })
}

// 修改商品评价
export function updateComment(data) {
  return request({
    url: '/school/comment',
    method: 'put',
    data: data
  })
}

// 删除商品评价
export function delComment(id) {
  return request({
    url: '/school/comment/' + id,
    method: 'delete'
  })
}
