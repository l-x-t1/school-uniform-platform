import request from '@/utils/request'

// 查询轮播图列表
export function listLbimage(query) {
  return request({
    url: '/system/lbimage/list',
    method: 'get',
    params: query
  })
}

// 查询轮播图详细
export function getLbimage(id) {
  return request({
    url: '/system/lbimage/' + id,
    method: 'get'
  })
}

// 新增轮播图
export function addLbimage(data) {
  return request({
    url: '/system/lbimage',
    method: 'post',
    data: data
  })
}

// 修改轮播图
export function updateLbimage(data) {
  return request({
    url: '/system/lbimage',
    method: 'put',
    data: data
  })
}

// 删除轮播图
export function delLbimage(id) {
  return request({
    url: '/system/lbimage/' + id,
    method: 'delete'
  })
}
