import request from '@/utils/request'

export function listSupplierQualityReport(query) {
  return request({
    url: '/school/supplierFeature/qualityReport/list',
    method: 'get',
    params: query
  })
}

export function getSupplierQualityReport(id) {
  return request({
    url: '/school/supplierFeature/qualityReport/' + id,
    method: 'get'
  })
}

export function listSupplierQualityReportFiles(query) {
  return request({
    url: '/school/supplierFeature/qualityReport/fileList',
    method: 'get',
    params: query
  })
}

export function addSupplierQualityReport(data) {
  return request({
    url: '/school/supplierFeature/qualityReport',
    method: 'post',
    data
  })
}

export function listSupplierServiceSchool(query) {
  return request({
    url: '/school/supplierFeature/serviceSchool/list',
    method: 'get',
    params: query
  })
}

export function getCurrentSupplier() {
  return request({
    url: '/school/supplierFeature/currentSupplier',
    method: 'get'
  })
}
