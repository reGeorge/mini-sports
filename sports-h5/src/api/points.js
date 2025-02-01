import request from '@/utils/request'

export function searchUserPoints(nickname) {
  return request({
    url: '/points/search',
    method: 'get',
    params: { nickname }
  })
} 