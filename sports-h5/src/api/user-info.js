import request from '@/utils/request'

// 获取用户完整信息（包括角色、权限和积分）
export function getUserCompleteInfo(userId) {
  return request({
    url: `/user/${userId}/complete-info`,
    method: 'get'
  })
}