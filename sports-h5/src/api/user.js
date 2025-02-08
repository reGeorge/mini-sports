import request from '@/utils/request'

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

// 搜索用户
export function searchUsers(nickname) {
  return request({
    url: '/user/search',
    method: 'get',
    params: { nickname }
  })
}

// 获取用户角色
export function getUserRoles(userId) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'get'
  })
}

// 分配用户角色
export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/user/${userId}/roles`,
    method: 'put',
    data: roleIds
  })
}

// 管理员注册
export function registerAdmin(data) {
  return request({
    url: '/user/register/admin',
    method: 'post',
    data
  })
}

// 更新用户积分
export function updateUserPoints(data) {
  return request({
    url: `/user/points/update`,
    method: 'post',
    data
  })
}