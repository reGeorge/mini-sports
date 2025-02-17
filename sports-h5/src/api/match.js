import request from '@/utils/request'

// 创建比赛记录
export function createMatch(data) {
  return request({
    url: '/api/matches',
    method: 'post',
    data
  })
}

// 更新比赛记录
export function updateMatch(id, data) {
  return request({
    url: `/api/matches/${id}`,
    method: 'put',
    data
  })
}

// 获取比赛记录详情
export function getMatch(id) {
  return request({
    url: `/api/matches/${id}`,
    method: 'get'
  })
}

// 获取比赛的所有比赛记录
export function getMatchesByTournament(tournamentId) {
  return request({
    url: `/api/matches/tournament/${tournamentId}`,
    method: 'get'
  })
}

// 获取阶段的比赛记录
export function getMatchesByStage(stageId) {
  return request({
    url: `/api/matches/stage/${stageId}`,
    method: 'get'
  })
}

// 获取分组的比赛记录
export function getMatchesByGroup(groupId) {
  return request({
    url: `/api/matches/group/${groupId}`,
    method: 'get'
  })
}

// 获取选手的比赛记录
export function getMatchesByPlayer(playerId) {
  return request({
    url: `/api/matches/player/${playerId}`,
    method: 'get'
  })
}

// 删除比赛记录
export function deleteMatch(id) {
  return request({
    url: `/api/matches/${id}`,
    method: 'delete'
  })
}

// 更新比赛结果
export function updateMatchResult(id, player1Score, player2Score) {
  return request({
    url: `/api/matches/${id}/result`,
    method: 'put',
    params: {
      player1Score,
      player2Score
    }
  })
}

// 更新比赛状态
export function updateMatchStatus(id, status) {
  return request({
    url: `/api/matches/${id}/status`,
    method: 'put',
    params: {
      status
    }
  })
}