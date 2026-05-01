import request from '@/utils/request'

// 查询学校校服采购工作总结列表
export function listCglcgzzj(query) {
  return request({
    url: '/school/cglcgzzj/list',
    method: 'get',
    params: query
  })
}

// 查询学校校服采购工作总结详细
export function getCglcgzzj(id) {
  return request({
    url: '/school/cglcgzzj/' + id,
    method: 'get'
  })
}

// 新增学校校服采购工作总结
export function addCglcgzzj(data) {
  return request({
    url: '/school/cglcgzzj',
    method: 'post',
    data: data
  })
}

// 修改学校校服采购工作总结
export function updateCglcgzzj(data) {
  return request({
    url: '/school/cglcgzzj',
    method: 'put',
    data: data
  })
}

// 删除学校校服采购工作总结
export function delCglcgzzj(id) {
  return request({
    url: '/school/cglcgzzj/' + id,
    method: 'delete'
  })
}
