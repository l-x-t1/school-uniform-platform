import request from "@/utils/request";
import { encrypt } from '@/utils/jsencrypt'

// 登录方法
export function login(username, password, code, uuid) {
  password = encrypt(password)
  return request({
    url: "/auth/login",
    headers: {
      isToken: false,
      repeatSubmit: false,
    },
    method: "post",
    data: { username, password, code, uuid },
  });
}

// 注册方法
export function register(data) {
  return request({
    url: "/auth/register",
    headers: {
      isToken: false,
    },
    method: "post",
    data: data,
  });
}

// 刷新方法
export function refreshToken() {
  return request({
    url: "/auth/refresh",
    method: "post",
  });
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: "/system/user/getInfo",
    method: "get",
  });
}

// 退出方法
export function logout() {
  return request({
    url: "/auth/logout",
    method: "delete",
  });
}

// 获取验证码
export function getCodeImg() {
  return request({
    url: "/code",
    headers: {
      isToken: false,
    },
    method: "get",
    timeout: 20000,
  });
}

//获取登录页面通知列表
export function getNoticeList() {
  return request({
    url: "/system/open/link/noticeList?bs=ZCFG",
    method: "get",
  });
}


//添加手机号  参数phone 手机号  userId用户id
export function addPhone(data) {
  return request({
    url: "/system/sms/addPhone",
    method: "get",
    params: data
  })
}


//获取验证码   参数:phone 手机号 
export function getPhoneCode(data) {
  return request({
    url: "/system/sms/sendSms",
    method: "post",
    params: data
  })
}


//验证码登录   get请求   参数:phone 手机号   code验证码
export function loginByCode(data) {
  return request({
    url: "/system/sms/smsLogin",
    method: "get",
    params: data
  })
}


//重置密码   参数:phone 手机号   code验证码   password新密码
export function resetPassword(data) {
  const password = encrypt(data.password)
  return request({
    url: "/system/user/resetPwdSendSms",
    method: "post",
    data: {
      phone: data.phone,
      code: data.code,
      password: password
    }
  })
}
