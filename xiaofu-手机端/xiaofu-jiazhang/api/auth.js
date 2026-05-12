import request from '@/utils/request'

export function sendSmsCode() {
  return Promise.resolve({
    code: 200,
    msg: '开发环境验证码已发送',
    data: {
      devCode: '123456'
    }
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

export function mobileLogin() {
  return request({
    url: '/school/simOpen/devLogin',
    method: 'GET'
  }).then(normalizeLoginResult)
}

export function wechatPhoneLogin() {
  return request({
    url: '/school/simOpen/devLogin',
    method: 'GET'
  }).then(normalizeLoginResult)
}

export function logout() {
  return Promise.resolve({
    code: 200,
    msg: '退出成功'
  })
}
