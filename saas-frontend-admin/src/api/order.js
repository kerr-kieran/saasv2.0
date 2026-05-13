import request from './request'

export function getOrderPage(params) {
  return request({
    url: '/admin/order/page',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/admin/order/${id}`,
    method: 'get'
  })
}
