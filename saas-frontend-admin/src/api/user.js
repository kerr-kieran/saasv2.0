import request from './request'

export function login(username, password) {
  return request({
    url: '/admin/auth/login',
    method: 'post',
    data: { username, password }
  })
}

export function getUserInfo() {
  return request({
    url: '/admin/auth/userinfo',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/admin/auth/logout',
    method: 'post'
  })
}
