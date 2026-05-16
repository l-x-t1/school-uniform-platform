import config from '@/config'
import { getToken, clearLoginState } from '@/utils/auth'

function buildUrl(url) {
  if (/^https?:\/\//.test(url)) return url
  return `${config.baseUrl}${url}`
}

function appendQuery(url, params = {}) {
  const keys = Object.keys(params).filter((key) => params[key] !== '' && params[key] !== undefined && params[key] !== null)
  if (!keys.length) return url

  const query = keys
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&')

  return `${url}${url.includes('?') ? '&' : '?'}${query}`
}

function normalizeResponse(res) {
  const data = res.data || {}
  const code = data.code === undefined ? res.statusCode : data.code

  if (res.statusCode >= 200 && res.statusCode < 300 && (code === 200 || code === 0 || code === res.statusCode)) {
    return Promise.resolve(data)
  }

  const message = data.msg || data.message || `请求失败(${code})`
  return Promise.reject({ code, message, response: res })
}

export default function request(options = {}) {
  const method = (options.method || 'GET').toUpperCase()
  const token = getToken()
  const header = {
    'Content-Type': 'application/json;charset=UTF-8',
    ...(options.header || {})
  }

  if (token) {
    header.Authorization = token.startsWith(config.tokenPrefix) ? token : `${config.tokenPrefix}${token}`
  }

  const rawUrl = buildUrl(options.url)
  const url = method === 'GET' ? appendQuery(rawUrl, options.params) : rawUrl

  return new Promise((resolve, reject) => {
    uni.request({
      url,
      method,
      data: method === 'GET' ? undefined : options.data,
      header,
      timeout: options.timeout || 15000,
      success: (res) => {
        normalizeResponse(res)
          .then(resolve)
          .catch((error) => {
            if (error.code === 401) {
              clearLoginState()
              uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
              setTimeout(() => {
                uni.reLaunch({ url: '/pages/login/login' })
              }, 800)
            }
            reject(error)
          })
      },
      fail: (error) => {
        reject({
          code: 'NETWORK_ERROR',
          message: error.errMsg || '网络连接失败',
          response: error
        })
      }
    })
  })
}
