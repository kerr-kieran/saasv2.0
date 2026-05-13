import request from './request'

/**
 * 获取收货地址列表
 */
export function getAddresses() {
  return request({
    url: '/member/address',
    method: 'get'
  })
}

/**
 * 新增收货地址
 */
export function addAddress(data) {
  return request({
    url: '/member/address',
    method: 'post',
    data
  })
}

/**
 * 更新收货地址
 */
export function updateAddress(id, data) {
  return request({
    url: `/member/address/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除收货地址
 */
export function deleteAddress(id) {
  return request({
    url: `/member/address/${id}`,
    method: 'delete'
  })
}

/**
 * 获取会员资料
 */
export function getProfile() {
  return request({
    url: '/member/profile',
    method: 'get'
  })
}
