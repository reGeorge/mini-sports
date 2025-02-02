import request from '@/utils/request'

// 获取所有权限
export function getPermissions() {
  return request({
    url: '/permission',
    method: 'get'
  })
}

// 获取单个权限
export function getPermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/permission/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}

// 获取用户权限
export function getUserPermissions(userId) {
  return request({
    url: `/permission/user/${userId}`,
    method: 'get'
  })
} 