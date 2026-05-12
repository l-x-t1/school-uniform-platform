import request from '@/utils/request'
import { getUserInfo } from '@/utils/auth'

function getCurrentUserId() {
  const userInfo = getUserInfo() || {}
  return Number(userInfo.userId || userInfo.id || 1)
}

function normalizeAddress(item = {}) {
  return {
    ...item,
    id: item.id || item.addressId,
    name: item.receiverName || item.name || item.receiver || '',
    receiver: item.receiverName || item.name || item.receiver || '',
    phone: item.receiverPhone || item.phone || item.mobile || '',
    mobile: item.receiverPhone || item.phone || item.mobile || '',
    province: item.provinceName || item.province || '',
    city: item.cityName || item.city || '',
    district: item.districtName || item.district || '',
    detailAddress: item.detailAddress || item.address || '',
    fullAddress: `${item.provinceName || item.province || ''}${item.cityName || item.city || ''}${item.districtName || item.district || ''}${item.detailAddress || item.address || ''}`,
    isDefault: String(item.isDefault ?? item.defaultFlag ?? '0') === '1',
    tag: String(item.addressType || '') === '2' ? '学校' : '家'
  }
}

function buildAddressPayload(data = {}) {
  const tag = data.tag || ''
  const addressType = data.addressType || (tag === '学校' ? '2' : '1')
  return {
    id: data.id,
    userId: Number(data.userId || getCurrentUserId()),
    receiverName: data.receiverName || data.name || data.receiver || '',
    receiverPhone: data.receiverPhone || data.phone || data.mobile || '',
    provinceName: data.provinceName || data.province || '',
    cityName: data.cityName || data.city || '',
    districtName: data.districtName || data.district || '',
    detailAddress: data.detailAddress || data.address || '',
    addressType,
    isDefault: data.isDefault === true || data.isDefault === 1 || data.isDefault === '1' ? '1' : '0',
    remark: data.remark || '',
    delFlag: data.delFlag || '0'
  }
}

export function listAddress(params = {}) {
  return request({
    url: '/school/userAddress/list',
    method: 'GET',
    params: {
      userId: getCurrentUserId(),
      ...params
    }
  }).then((res) => ({
    ...res,
    rows: (res.rows || []).map(normalizeAddress)
  }))
}

export function getAddress(id) {
  return request({
    url: `/school/userAddress/${id}`,
    method: 'GET'
  }).then((res) => ({
    ...res,
    data: normalizeAddress(res.data || res)
  }))
}

export function addAddress(data) {
  return request({
    url: '/school/userAddress',
    method: 'POST',
    data: buildAddressPayload(data)
  })
}

export function updateAddress(data) {
  return request({
    url: '/school/userAddress',
    method: 'PUT',
    data: buildAddressPayload(data)
  })
}

export function deleteAddress(id) {
  return request({
    url: `/school/userAddress/${id}`,
    method: 'DELETE'
  })
}

export async function setDefaultAddress(id) {
  const listRes = await listAddress()
  const rows = listRes.rows || []
  const tasks = rows.map((item) =>
    updateAddress({
      ...item,
      isDefault: String(item.id) === String(id) ? '1' : '0'
    })
  )
  await Promise.all(tasks)
  return { code: 200, msg: '设置成功' }
}
