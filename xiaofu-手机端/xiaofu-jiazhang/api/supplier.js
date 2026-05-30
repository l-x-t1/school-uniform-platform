import request from '@/utils/request'

export function getSupplierMessage(id) {
  return request({
    url: `/school/supplierMessage/${id}`,
    method: 'GET'
  })
}
