import config from '@/config'

export function getToken() {
  return uni.getStorageSync(config.tokenKey) || ''
}

export function setToken(token) {
  uni.setStorageSync(config.tokenKey, token)
}

export function removeToken() {
  uni.removeStorageSync(config.tokenKey)
}

export function getUserInfo() {
  return uni.getStorageSync('userInfo') || null
}

export function setUserInfo(userInfo) {
  uni.setStorageSync('userInfo', userInfo)
}

export function clearLoginState() {
  removeToken()
  uni.removeStorageSync('isLoggedIn')
  uni.removeStorageSync('userInfo')
  uni.removeStorageSync('simId')
  uni.removeStorageSync('isImprove')
  uni.removeStorageSync('phone')
  uni.removeStorageSync('devOrders')
  uni.removeStorageSync('pendingCommentOrder')
  uni.removeStorageSync('selectedOrder')
}
