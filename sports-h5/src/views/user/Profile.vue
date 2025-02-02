<template>
  <tabbar-layout>
    <div class="profile">
      <van-nav-bar title="我的" />
      
      <!-- 用户基本信息卡片 -->
      <div class="user-card">
        <div class="user-info">
          <van-image
            round
            width="60"
            height="60"
            :src="userInfo.avatarUrl || '/images/avatar/fzd.png'"
          />
          <div class="user-detail">
            <div class="nickname">{{ userInfo.nickname }}</div>
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
      </div>

    
    </div>
  </tabbar-layout>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import TabbarLayout from '@/components/layout/TabbarLayout.vue'

export default {
  name: 'Profile',
  components: {
    TabbarLayout
  },
  setup() {
    const router = useRouter()
    const userInfo = ref({})

    onMounted(() => {
      const storedUserInfo = localStorage.getItem('userInfo')
      if (storedUserInfo) {
        userInfo.value = JSON.parse(storedUserInfo)
      } else {
        router.push('/login')
      }
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

    const handleLogout = () => {
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
      showToast('退出成功')
      router.push('/login')
    }

    return {
      userInfo,
      goToUserDetail,
      goToMyEvents,
      goToEventRegister,
      goToNotices,
      goToNoticeDetail,
      goToGroupDetail,
      handleLogout
    }
  }
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 50px;
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
  margin-bottom: 5px;
}

.view-profile {
  color: #666;
  font-size: 14px;
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
</style> 