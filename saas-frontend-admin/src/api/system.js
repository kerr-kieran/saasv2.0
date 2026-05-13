import request from './request'

// --- User management ---
export function getSystemUserPage(params) {
  return request({
    url: '/admin/system/user/page',
    method: 'get',
    params
  })
}

export function createSystemUser(data) {
  return request({
    url: '/admin/system/user',
    method: 'post',
    data
  })
}

export function updateSystemUser(id, data) {
  return request({
    url: `/admin/system/user/${id}`,
    method: 'put',
    data
  })
}

export function deleteSystemUser(id) {
  return request({
    url: `/admin/system/user/${id}`,
    method: 'delete'
  })
}

export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/admin/system/user/${userId}/roles`,
    method: 'put',
    data: { roleIds }
  })
}

// --- Role management ---
export function getRolePage(params) {
  return request({
    url: '/admin/system/role/page',
    method: 'get',
    params
  })
}

export function getAllRoles() {
  return request({
    url: '/admin/system/role/all',
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/admin/system/role',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/admin/system/role/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/admin/system/role/${id}`,
    method: 'delete'
  })
}

// --- Permission management ---
export function getPermissionTree() {
  return request({
    url: '/admin/system/permission/tree',
    method: 'get'
  })
}

export function getPermissionPage(params) {
  return request({
    url: '/admin/system/permission/page',
    method: 'get',
    params
  })
}

export function createPermission(data) {
  return request({
    url: '/admin/system/permission',
    method: 'post',
    data
  })
}

export function updatePermission(id, data) {
  return request({
    url: `/admin/system/permission/${id}`,
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/admin/system/permission/${id}`,
    method: 'delete'
  })
}

// --- Category management ---
export function getCategoryTree() {
  return request({
    url: '/admin/product/category/tree',
    method: 'get'
  })
}

export function createCategory(data) {
  return request({
    url: '/admin/product/category',
    method: 'post',
    data
  })
}

export function updateCategory(id, data) {
  return request({
    url: `/admin/product/category/${id}`,
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/admin/product/category/${id}`,
    method: 'delete'
  })
}
