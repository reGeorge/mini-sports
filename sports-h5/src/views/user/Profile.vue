<template>
  <tabbar-layout>
    <div class="profile">
      <div class="profile-content">
        <van-nav-bar title="我的" />
        
        <!-- 用户基本信息卡片 -->
        <div class="user-card">
          <div class="user-info">
            <van-image
              round
              width="60"
              height="60"
              src="/images/avatar/fzd.png"
            />
            <div class="user-detail">
              <div class="nickname">{{ userInfo.nickname }}</div>
              <div class="roles">
                <van-tag 
                  v-for="role in userInfo.roles" 
                  :key="role.id"
                  :type="role.code === 'ROLE_ADMIN' ? 'danger' : 'primary'"
                  size="small"
                  class="role-tag"
                >
                  {{ role.name }}
                </van-tag>
              </div>
              <div class="view-profile" @click="goToUserDetail">查看资料</div>
            </div>
          </div>
          <div class="points-info">
            <div class="points-label">积分</div>
            <div class="points-value">{{ userInfo.points || 0 }} ({{ userInfo.level || 'Y5' }})</div>
          </div>
        </div>

        <!-- 功能按钮区 -->
        <div class="action-buttons">
          <van-button block class="action-btn" @click="goToMyEvents">
            <template #icon>
              <van-icon name="medal-o" />
            </template>
            我参加的赛事
          </van-button>

          <!-- 管理员控制台入口 -->
          <van-button 
            v-if="hasAdminRole" 
            block 
            class="action-btn admin-btn" 
            @click="goToAdminHome"
          >
            <template #icon>
              <van-icon name="setting-o" />
            </template>
            管理员控制台
          </van-button>
        </div>
      </div>

      <!-- 退出登录按钮 -->
      <div class="logout-wrapper">
        <van-button type="danger" block @click="handleLogout">退出登录</van-button>
      </div>
    </div>
  </tabbar-layout>
</template>

<script>
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import TabbarLayout from '@/components/layout/TabbarLayout.vue'
import { getUserRoles } from '@/api/user'
import { getUserPermissions } from '@/api/permission'
import { hasRole, hasPermission } from '@/utils/permission'

export default {
  name: 'Profile',
  components: {
    TabbarLayout
  },
  setup() {
    const router = useRouter()
    const userInfo = ref({})
    const hasAdminRole = ref(false)

    // 检查用户信息和角色的函数
    const checkUserInfo = async () => {
      const storedUserInfo = localStorage.getItem('userInfo')
      if (storedUserInfo) {
        const parsedUserInfo = JSON.parse(storedUserInfo)
        
        try {
          // 从后端重新获取用户的角色和权限信息
          const [rolesRes, permissionsRes] = await Promise.all([
            getUserRoles(parsedUserInfo.id),
            getUserPermissions(parsedUserInfo.id)
          ])
          
          parsedUserInfo.roles = rolesRes.data
          
          // 更新 localStorage 中的用户信息和权限
          localStorage.setItem('userInfo', JSON.stringify(parsedUserInfo))
          localStorage.setItem('userPermissions', JSON.stringify(permissionsRes.data))
          
          // 更新页面显示
          userInfo.value = parsedUserInfo
          hasAdminRole.value = hasRole('ROLE_ADMIN')
        } catch (error) {
          console.error('获取用户信息失败:', error)
          showToast('获取用户信息失败')
        }
      } else {
        router.push('/login')
      }
    }

    // 页面首次加载时检查
    onMounted(() => {
      checkUserInfo()
    })

    // 每次页面激活时检查
    onActivated(() => {
      checkUserInfo()
    })

    const goToUserDetail = () => {
      router.push('/user/detail')
    }

    const goToMyEvents = () => {
      router.push('/user/events')
    }

    const goToEventRegister = () => {
      router.push('/event/register')
    }

    const goToNotices = () => {
      router.push('/notices')
    }

    const goToNoticeDetail = () => {
      router.push('/notice/detail')
    }

    const goToGroupDetail = () => {
      showToast('功能开发中')
    }

    const goToAdminHome = () => {
      router.push('/admin')
    }

    const handleLogout = () => {
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      showToast('退出成功')
      router.push('/login')
    }

    return {
      userInfo,
      hasAdminRole,
      goToUserDetail,
      goToMyEvents,
      goToEventRegister,
      goToNotices,
      goToNoticeDetail,
      goToGroupDetail,
      handleLogout,
      goToAdminHome
    }
  }
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding: 16px;
  padding-bottom: 80px;
  position: relative;
}

.profile-content {
  margin-bottom: 60px;
}

.user-card {
  background: white;
  padding: 20px;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.user-detail {
  margin-left: 15px;
}

.nickname {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 2px;
}

.roles {
  display: flex;
  gap: 4px;
  margin: 4px 0;
}

.role-tag {
  font-size: 10px;
  padding: 0 4px;
  line-height: 16px;
  border-radius: 2px;
}

.view-profile {
  color: #666;
  font-size: 14px;
  margin-top: 4px;
}

.points-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-top: 1px solid #eee;
}

.points-label {
  color: #666;
  font-size: 14px;
}

.points-value {
  color: #ff6b01;
  font-weight: bold;
}

.action-buttons {
  padding: 15px;
  background: white;
  margin-bottom: 10px;
}

.action-btn {
  margin-bottom: 10px;
  background: #f5f5f5;
  border: none;
  color: #333;
  height: 44px;
  font-size: 14px;
}

.action-btn:last-child {
  margin-bottom: 0;
}

.notice-section {
  background: white;
}

.notice-icon {
  margin-right: 5px;
  color: #666;
}

:deep(.van-cell) {
  align-items: center;
}

:deep(.van-tabbar) {
  border-top: 1px solid #eee;
}

:deep(.van-tabbar-item) {
  color: #666;
  font-size: 12px;
}

:deep(.van-tabbar-item--active) {
  color: #1989fa;
}

:deep(.van-tabbar-item__icon) {
  font-size: 20px;
}

.logout-wrapper {
  position: fixed;
  bottom: 80px;  /* 留出底部导航栏的空间 */
  left: 16px;
  right: 16px;
  z-index: 10;
}

:deep(.van-button--danger) {
  width: 100%;
}

.admin-btn {
  background: #1989fa !important;
  color: white !important;
}
</style> 