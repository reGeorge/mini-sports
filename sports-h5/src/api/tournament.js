import request from '@/utils/request'

// 获取赛事列表
export function getTournaments(params) {
  return request({
    url: '/tournaments',
    method: 'get',
    params
  })
}

// 获取赛事详情
export function getTournamentById(id) {
  return request({
    url: `/tournaments/${id}`,
    method: 'get'
  })
}

// 创建赛事
export function createTournament(data) {
  return request({
    url: '/tournaments',
    method: 'post',
    data
  })
}

// 更新赛事
export function updateTournament(id, data) {
  return request({
    url: `/tournaments/${id}`,
    method: 'put',
    data
  })
}

// 删除赛事
export function deleteTournament(id) {
  return request({
    url: `/tournaments/${id}`,
    method: 'delete'
  })
}

// 更新赛事状态
export function updateTournamentStatus(id, status) {
  return request({
    url: `/tournaments/${id}/status`,
    method: 'put',
    params: { status }
  })
} 

// 开始赛事
export function startTournament(id) {
  return request({
    url: `/tournaments/${id}/start`,
    method: 'post'
  })
}

// 获取赛事阶段信息
export function getTournamentStages(tournamentId) {
  return request({
    url: `/tournaments/${tournamentId}/stages`,
    method: 'get'
  })
}