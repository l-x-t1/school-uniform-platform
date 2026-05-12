import request from '@/utils/request'

const statsBaseUrl = '/school'

function statsRequest(config) {
  return request({
    headers: {
      silentError: true,
      ...(config.headers || {})
    },
    ...config
  })
}

export const getPersonMoney = () => {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/peopleCount`,
    method: 'get'
  })
}

export function getOrderCount() {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/getCount`,
    method: 'get'
  })
}

export function getOrderAmount() {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/getMoney`,
    method: 'get'
  })
}

export function getSchoolTopList(query) {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/school/topList`,
    params: query,
    method: 'get'
  })
}

export function getJyjTopList(query) {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/jyj/topList`,
    params: query,
    method: 'get'
  })
}

export function isChangePassword(userName) {
  return request({
    url: `/system/user/isPwdExpire?username=${userName}`,
    method: 'get'
  })
}

export function getSchoolOrderCount(query) {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/getSchool5YearCountData`,
    params: query,
    method: 'get'
  })
}

export function getSchoolOrderAmount(query) {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/getSchool5YearMoneyData`,
    params: query,
    method: 'get'
  })
}

export function getSchoolUniformType() {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/schoolUniformType`,
    method: 'get'
  })
}

export function getPlatformUsage(query) {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/platformUsage`,
    params: query,
    method: 'get'
  })
}

export function getPriceTrendData(query) {
  return statsRequest({
    url: `${statsBaseUrl}/index/statistics/priceTrendData`,
    params: query,
    method: 'get'
  })
}
