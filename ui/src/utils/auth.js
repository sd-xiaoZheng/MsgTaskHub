import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

export function getToken() {
  return sessionStorage.getItem('token') || Cookies.get(TokenKey)
}

export function setToken(token) {
  // 设置会话缓存，浏览器是独享的，所以每个标签页的缓存token值都是不一样的
  sessionStorage.setItem('token', token)
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  sessionStorage.removeItem('token')
  return Cookies.remove(TokenKey)
}
