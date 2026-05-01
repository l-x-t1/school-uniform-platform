import request from '@/utils/request'

// 查询企业备选公告列表
export function listRwggzs(query) {
  return request({
    url: '/school/rwggzs/list',
    method: 'get',
    params: query
  })
}

// 查询企业备选公告详细
export function getRwggzs(id) {
  return request({
    url: '/school/rwggzs/' + id,
    method: 'get'
  })
}

// 新增企业备选公告
export function addRwggzs(data) {
  return request({
    url: '/school/rwggzs',
    method: 'post',
    data: data
  })
}

// 修改企业备选公告
export function updateRwggzs(data) {
  return request({
    url: '/school/rwggzs',
    method: 'put',
    data: data
  })
}

// 删除企业备选公告
export function delRwggzs(id) {
  return request({
    url: '/school/rwggzs/' + id,
    method: 'delete'
  })
}


//补充公告
export function buchong(data) {
  return request({
    url: '/system/xfptnotice/releaseSupplement',
    method: 'post',
    params: data
  })
}

//获取补充列表
export function getBcList(data) {
  return request({
    url: '/system/xfptnotice/selectReleaseSupplement',
    method: 'get',
    params: data
  })
}

