import config from '@/config'

function resolveStoredUserId(userInfo = {}) {
  const simId = uni.getStorageSync('simId')
  const rawUserId = userInfo.userId ?? userInfo.id ?? simId
  const parsedUserId = Number(rawUserId)
  return Number.isNaN(parsedUserId) ? 1 : parsedUserId
}

function normalizeUserInfo(userInfo = {}) {
  const phone = userInfo.phone || userInfo.phonenumber || userInfo.parm1 || uni.getStorageSync('phone') || ''
  const userId = resolveStoredUserId(userInfo)
  return {
    ...userInfo,
    id: userInfo.id ?? userId,
    userId: userInfo.userId ?? userId,
    phone,
    nickName: userInfo.nickName || userInfo.name || '家长用户',
    userType: userInfo.userType || 'parent'
  }
}

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
  const userInfo = uni.getStorageSync('userInfo')
  if (userInfo) {
    return normalizeUserInfo(userInfo)
  }
  if (uni.getStorageSync('simId')) {
    return normalizeUserInfo({})
  }
  return null
}

export function setUserInfo(userInfo) {
  uni.setStorageSync('userInfo', normalizeUserInfo(userInfo))
}

export function clearLoginState() {
  removeToken()
  uni.removeStorageSync('isLoggedIn')
  uni.removeStorageSync('userInfo')
  uni.removeStorageSync('simId')
  uni.removeStorageSync('isImprove')
  uni.removeStorageSync('phone')
  uni.removeStorageSync('studentList')
  uni.removeStorageSync('selectedStudent')
  uni.removeStorageSync('selectedAddress')
  uni.removeStorageSync('addressList')
  uni.removeStorageSync('cartList')
  uni.removeStorageSync('confirmOrderItems')
  uni.removeStorageSync('devOrders')
  uni.removeStorageSync('commentedOrders')
  uni.removeStorageSync('pendingCommentOrder')
  uni.removeStorageSync('selectedOrder')
}
