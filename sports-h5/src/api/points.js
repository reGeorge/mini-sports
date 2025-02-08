import request from '@/utils/request'

export function searchUserPoints(nickname) {
  return request({
    url: '/points/search',
    method: 'get',
    params: { nickname }
  })
}

export function getPointsRanking(limit = 10) {
  return request({
    url: '/points/ranking',
    method: 'get',
    params: { limit }
  })
}

export function getUserPoints(userId) {
  return request({
    url: `/points/user/${userId}`,
    method: 'get'
  })
}

export function getPointsRecords(userId, page = 1, size = 20) {
  return request({
    url: '/points/records',
    method: 'get',
    params: { userId, page, size }
  })
}