import request from '@/utils/request'

// 查询学生尺码扩展列表
export function listStudentSizeExt(query) {
  return request({
    url: '/supplier/studentSizeExt/list',
    method: 'get',
    params: query
  })
}

// 查询学生尺码扩展详细
export function getStudentSizeExt(id) {
  return request({
    url: '/supplier/studentSizeExt/' + id,
    method: 'get'
  })
}

// 新增学生尺码扩展
export function addStudentSizeExt(data) {
  return request({
    url: '/supplier/studentSizeExt',
    method: 'post',
    data: data
  })
}

// 修改学生尺码扩展
export function updateStudentSizeExt(data) {
  return request({
    url: '/supplier/studentSizeExt',
    method: 'put',
    data: data
  })
}

// 删除学生尺码扩展
export function delStudentSizeExt(id) {
  return request({
    url: '/supplier/studentSizeExt/' + id,
    method: 'delete'
  })
}
