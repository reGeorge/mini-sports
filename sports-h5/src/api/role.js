import request from '@/utils/request'

// 获取角色列表
export function getRoles() {
  return request({
    url: '/role',
    method: 'get'
  })
}

// 获取单个角色
export function getRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

// 获取角色权限
export function getRolePermissions(roleId) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'get'
  })
}

// 更新角色权限
export function updateRolePermissions(roleId, permissionIds) {
  return request({
    url: `/role/${roleId}/permissions`,
    method: 'put',
    data: { permissionIds }
  })
}

// 分配用户角色
export function assignUserRole(userId, roleId) {
  return request({
    url: '/role/assign',
    method: 'post',
    params: { userId, roleId }
  })
}

// 移除用户角色
export function removeUserRole(userId, roleId) {
  return request({
    url: '/role/assign',
    method: 'delete',
    params: { userId, roleId }
  })
} 