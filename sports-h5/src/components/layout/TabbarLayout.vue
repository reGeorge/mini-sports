<template>
  <div class="tabbar-layout">
    <div class="content">
      <slot></slot>
    </div>
    <van-tabbar route fixed placeholder>
      <van-tabbar-item replace to="/games" icon="flag-o">赛事</van-tabbar-item>
      <van-tabbar-item replace to="/points" icon="search">查积分</van-tabbar-item>
      <van-tabbar-item replace to="/profile" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'TabbarLayout',
  setup() {
    const active = ref(0)
    return { active }
  }
}
</script>

<style scoped>
.tabbar-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  overflow-y: auto;
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