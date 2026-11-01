import request from '@/utils/request'

export function listApplyCenter(query) {
  return request({
    url: '/school/supplierApplyPortal/notice/list',
    method: 'get',
    params: query
  })
}

export function getApplyContext(noticeId) {
  return request({
    url: '/school/supplierApplyPortal/context/' + noticeId,
    method: 'get'
  })
}

export function saveApplyDraft(data) {
  return request({
    url: '/school/supplierApplyPortal/saveDraft',
    method: 'post',
    data
  })
}

export function submitApply(data) {
  return request({
    url: '/school/supplierApplyPortal/submit',
    method: 'post',
    data
  })
}

export function withdrawApply(noticeId) {
  return request({
    url: '/school/supplierApplyPortal/withdraw/' + noticeId,
    method: 'post'
  })
}

export function listMyApply(query) {
  return request({
    url: '/school/supplierApplyPortal/my/list',
    method: 'get',
    params: query
  })
}

export function getMyApply(id) {
  return request({
    url: '/school/supplierApplyPortal/my/' + id,
    method: 'get'
  })
}
