import request from '@/utils/request'

// 获取赛事报名列表
export function getRegistrations(tournamentId) {
  return request({
    url: `/tournaments/${tournamentId}/registrations`,
    method: 'get'
  })
}

// 报名赛事
export function register(tournamentId) {
  return request({
    url: `/tournaments/${tournamentId}/registrations/register`,
    method: 'post'
  })
}

// 取消报名
export function cancelRegistration(tournamentId) {
  return request({
    url: `/tournaments/${tournamentId}/registrations/register`,
    method: 'delete'
  })
}

// 审核报名
export function auditRegistration(tournamentId, userId, status) {
  return request({
    url: `/tournaments/${tournamentId}/registrations/${userId}/audit`,
    method: 'put',
    data: { status }
  })
} 