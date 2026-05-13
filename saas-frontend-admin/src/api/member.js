import request from './request'

export function getMemberPage(params) {
  return request({
    url: '/admin/member/page',
    method: 'get',
    params
  })
}

export function getMemberDetail(id) {
  return request({
    url: `/admin/member/${id}`,
    method: 'get'
  })
}
