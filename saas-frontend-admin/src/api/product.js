import request from './request'

export function getProductPage(params) {
  return request({
    url: '/admin/product/page',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'get'
  })
}

export function createProduct(data) {
  return request({
    url: '/admin/product',
    method: 'post',
    data
  })
}

export function updateProduct(id, data) {
  return request({
    url: `/admin/product/${id}`,
    method: 'put',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}
