import request from '@/utils/request'

// 创建比赛记录
export function createMatch(data) {
  return request({
    url: '/matches',
    method: 'post',
    data
  })
}

// 更新比赛记录
export function updateMatch(id, data) {
  return request({
    url: `/matches/${id}`,
    method: 'put',
    data
  })
}

// 获取比赛记录详情
export function getMatch(id) {
  return request({
    url: `/matches/${id}`,
    method: 'get'
  })
}

// 获取比赛的所有比赛记录
export function getMatchesByTournament(tournamentId) {
  return request({
    url: `/matches/tournament/${tournamentId}`,
    method: 'get'
  })
}

// 获取阶段的比赛记录
export function getMatchesByStage(stageId) {
  return request({
    url: `/matches/stage/${stageId}`,
    method: 'get'
  })
}

// 获取分组的比赛记录
export function getMatchesByGroup(groupId) {
  return request({
    url: `/matches/group/${groupId}`,
    method: 'get'
  })
}

// 获取选手的比赛记录
export function getMatchesByPlayer(playerId) {
  return request({
    url: `/matches/player/${playerId}`,
    method: 'get'
  })
}

// 删除比赛记录
export function deleteMatch(id) {
  return request({
    url: `/matches/${id}`,
    method: 'delete'
  })
}

// 更新比赛结果
export function updateMatchResult(id, player1Score, player2Score) {
  return request({
    url: `/matches/${id}/result`,
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
    url: `/matches/${id}/status`,
    method: 'put',
    params: {
      status
    }
  })
}

// 更新比赛比分
export function updateMatchScore(data) {
  return request({
    url: `/tournaments/${data.tournamentId}/matches/${data.matchId}/score`,
    method: 'put',
    data: {
      player1Score: data.player1Score,
      player2Score: data.player2Score
    }
  })
}

// 获取比赛详情
export function getMatchDetail(matchId) {
  return request({
    url: `/matches/${matchId}`,
    method: 'get'
  })
}