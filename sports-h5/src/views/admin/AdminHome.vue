<template>
  <div class="admin-home">
    <van-nav-bar
      title="管理员控制台"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <van-cell-group inset class="menu-group">
      <van-cell
        title="角色管理"
        is-link
        @click="router.push('/admin/roles')"
      >
        <template #label>
          <span class="menu-desc">管理系统角色和权限</span>
        </template>
      </van-cell>
      
      <van-cell
        title="用户角色分配"
        is-link
        @click="router.push('/admin/user-roles')"
      >
        <template #label>
          <span class="menu-desc">为用户分配系统角色</span>
        </template>
      </van-cell>
      
      <van-cell
        title="系统设置"
        is-link
        @click="router.push('/admin/settings')"
      >
        <template #label>
          <span class="menu-desc">管理系统基本设置</span>
        </template>
      </van-cell>
    </van-cell-group>

    <div class="admin-info">
      <van-cell-group inset>
        <van-cell title="当前管理员">
          <template #value>
            <span class="admin-name">{{ adminInfo.nickname }}</span>
          </template>
        </van-cell>
        <van-cell title="联系电话">
          <template #value>
            <span class="admin-phone">{{ adminInfo.phone }}</span>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div class="action-buttons">
      <van-button type="danger" block @click="handleLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const adminInfo = ref({
  nickname: '',
  phone: ''
})

onMounted(() => {
  // 从localStorage获取管理员信息
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    adminInfo.value = JSON.parse(userInfo)
  }
})

const handleLogout = () => {
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')
  showToast('退出成功')
  router.push('/login')
}

const onClickLeft = () => {
  router.push('/profile')
}
</script>

<style scoped>
.admin-home {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding: 16px;
  padding-bottom: 80px;  /* 为底部按钮留出空间 */
  position: relative;    /* 为绝对定位的子元素提供参考 */
}

.menu-group {
  margin-top: 20px;
}

.menu-desc {
  font-size: 12px;
  color: #666;
}

.admin-info {
  margin-top: 32px;
}

.admin-name,
.admin-phone {
  color: #666;
}

.action-buttons {
  position: fixed;
  bottom: 20px;
  left: 16px;
  right: 16px;
  z-index: 10;
}

:deep(.van-button--danger) {
  width: 100%;
}
</style> 