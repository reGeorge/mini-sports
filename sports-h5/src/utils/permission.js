/**
 * 检查用户是否有指定权限
 * @param {string} permissionCode - 权限代码
 * @returns {boolean} - 是否有权限
 */
export function hasPermission(permissionCode) {
  if (!permissionCode) return false
  try {
    const permissions = JSON.parse(localStorage.getItem('userPermissions') || '[]')
    return Array.isArray(permissions) && permissions.some(permission => permission && permission.code === permissionCode)
  } catch (error) {
    console.error('Error parsing permissions:', error)
    return false
  }
}

/**
 * 检查用户是否有指定角色
 * @param {string} roleCode - 角色代码
 * @returns {boolean} - 是否有角色
 */
export function hasRole(roleCode) {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const roles = userInfo.roles || []
  return roles.some(role => role.code === roleCode)
}

/**
 * 获取用户所有权限代码
 * @returns {string[]} - 权限代码数组
 */
export function getUserPermissionCodes() {
  const permissions = JSON.parse(localStorage.getItem('userPermissions') || '[]')
  return permissions.map(permission => permission.code)
}

/**
 * 检查用户是否有任意一个指定权限
 * @param {string[]} permissionCodes - 权限代码数组
 * @returns {boolean} - 是否有权限
 */
export function hasAnyPermission(permissionCodes) {
  const userPermissions = getUserPermissionCodes()
  return permissionCodes.some(code => userPermissions.includes(code))
}

/**
 * 检查用户是否有所有指定权限
 * @param {string[]} permissionCodes - 权限代码数组
 * @returns {boolean} - 是否有所有权限
 */
export function hasAllPermissions(permissionCodes) {
  const userPermissions = getUserPermissionCodes()
  return permissionCodes.every(code => userPermissions.includes(code))
}