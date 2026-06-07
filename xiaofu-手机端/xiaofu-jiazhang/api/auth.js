import request from '@/utils/request'

export function sendSmsCode(data) {
  return request({
    url: '/school/mobile/sendSmsCode',
    method: 'POST',
    data
  })
}

function normalizeLoginResult(res) {
  const data = res.data || res
  return {
    ...res,
    data: {
      token: data.access_token || data.token || '',
      access_token: data.access_token || data.token || '',
      simId: data.user?.id,
      isImprove: String(data.is_improve ?? '0') === '1',
      user: data.user || {},
      userInfo: data.user || {}
    }
  }
}

export function mobileLogin(data) {
  return request({
    url: '/school/mobile/login',
    method: 'POST',
    data
  }).then(normalizeLoginResult)
}

export function wechatPhoneLogin(data) {
  return request({
    url: '/school/mobile/wechat-phone-login',
    method: 'POST',
    data
  }).then(normalizeLoginResult)
}

export function logout() {
  return Promise.resolve({
    code: 200,
    msg: '退出成功'
  })
}
