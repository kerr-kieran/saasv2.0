import request from './request'

/**
 * 分页查询商品列表
 */
export function getProductPage(params) {
  return request({
    url: '/product/page',
    method: 'get',
    params
  })
}

/**
 * 获取商品详情
 */
export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

/**
 * 获取分类树
 */
export function getCategoryTree() {
  return request({
    url: '/category/tree',
    method: 'get'
  })
}
