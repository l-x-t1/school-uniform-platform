import request from '@/utils/request'

// 查询家长绑定学生信息列表
export function listSchoolStudentInfo(query) {
  return request({
    url: '/school/schoolStudentInfo/list',
    method: 'get',
    params: query
  })
}

// 查询家长绑定学生信息详细
export function getSchoolStudentInfo(id) {
  return request({
    url: '/school/schoolStudentInfo/' + id,
    method: 'get'
  })
}

// 新增家长绑定学生信息
export function addSchoolStudentInfo(data) {
  return request({
    url: '/school/schoolStudentInfo',
    method: 'post',
    data: data
  })
}

// 修改家长绑定学生信息
export function updateSchoolStudentInfo(data) {
  return request({
    url: '/school/schoolStudentInfo',
    method: 'put',
    data: data
  })
}

// 删除家长绑定学生信息
export function delSchoolStudentInfo(id) {
  return request({
    url: '/school/schoolStudentInfo/' + id,
    method: 'delete'
  })
}
