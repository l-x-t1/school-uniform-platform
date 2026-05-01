import request from '@/utils/request'

// 查询选用组织委托招标机构确定企业模式列表
export function listMode(query) {
  return request({
    url: '/school/mode/list',
    method: 'get',
    params: query
  })
}

// 查询选用组织委托招标机构确定企业模式详细
export function getMode(id) {
  return request({
    url: '/school/mode/' + id,
    method: 'get'
  })
}

// 新增选用组织委托招标机构确定企业模式
export function addMode(data) {
  return request({
    url: '/school/mode',
    method: 'post',
    data: data
  })
}

// 修改选用组织委托招标机构确定企业模式
export function updateMode(data) {
  return request({
    url: '/school/mode',
    method: 'put',
    data: data
  })
}

// 删除选用组织委托招标机构确定企业模式
export function delMode(id) {
  return request({
    url: '/school/mode/' + id,
    method: 'delete'
  })
}
