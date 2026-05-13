import request from './request'

export function getInventoryPage(params) {
  return request({
    url: '/admin/inventory/page',
    method: 'get',
    params
  })
}

export function adjustInventory(data) {
  return request({
    url: '/admin/inventory/adjust',
    method: 'post',
    data
  })
}

export function getInventoryLog(params) {
  return request({
    url: '/admin/inventory/log',
    method: 'get',
    params
  })
}
