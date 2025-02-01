<template>
  <div class="game-detail">
    <van-nav-bar
      title="赛事详情"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <div class="detail-content" v-if="gameInfo">
      <!-- 赛事标题 -->
      <div class="game-title">
        <h2>{{ gameInfo.title }}</h2>
        <span class="game-status" :class="gameInfo.status">{{ gameInfo.statusText }}</span>
      </div>

      <!-- 基本信息 -->
      <div class="info-card">
        <div class="info-item">
          <van-icon name="clock-o" />
          <span class="label">比赛时间：</span>
          <span class="value">{{ gameInfo.startTime }}</span>
        </div>
        <div class="info-item">
          <van-icon name="location-o" />
          <span class="label">比赛地点：</span>
          <span class="value">{{ gameInfo.location }}</span>
        </div>
        <div class="info-item">
          <van-icon name="friends-o" />
          <span class="label">报名人数：</span>
          <span class="value">{{ gameInfo.playerCount }}/{{ gameInfo.maxPlayers }}</span>
        </div>
        <div class="info-item">
          <van-icon name="medal-o" />
          <span class="label">比赛类型：</span>
          <span class="value">{{ gameInfo.type }}</span>
        </div>
      </div>

      <!-- 比赛说明 -->
      <div class="description-card">
        <h3>比赛说明</h3>
        <p>{{ gameInfo.description || '暂无说明' }}</p>
      </div>

      <!-- 参赛要求 -->
      <div class="requirements-card">
        <h3>参赛要求</h3>
        <ul>
          <li v-for="(req, index) in gameInfo.requirements" :key="index">
            {{ req }}
          </li>
        </ul>
      </div>

      <!-- 报名按钮 -->
      <div class="action-bar">
        <van-button 
          type="primary" 
          block 
          :disabled="!canRegister"
          @click="handleRegister"
        >
          {{ registerButtonText }}
        </van-button>
      </div>
    </div>

    <van-empty v-else description="赛事信息加载中..." />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'

export default {
  name: 'GameDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const gameInfo = ref(null)

    onMounted(() => {
      // TODO: 从后端获取赛事详情
      gameInfo.value = {
        id: route.params.id,
        title: '2025年春季乒乓球联赛',
        status: 'registering',
        statusText: '报名中',
        startTime: '2025-03-01 09:00',
        location: '上海市体育中心',
        playerCount: 16,
        maxPlayers: 32,
        type: '单打',
        description: '本次比赛采用单淘汰赛制，每场比赛采用五局三胜制。',
        requirements: [
          '参赛选手积分不低于1000分',
          '需提供有效身份证件',
          '自备球拍和运动装备'
        ]
      }
    })

    const canRegister = computed(() => {
      if (!gameInfo.value) return false
      return gameInfo.value.status === 'registering' && 
             gameInfo.value.playerCount < gameInfo.value.maxPlayers
    })

    const registerButtonText = computed(() => {
      if (!gameInfo.value) return '报名'
      if (gameInfo.value.status !== 'registering') return '报名已截止'
      if (gameInfo.value.playerCount >= gameInfo.value.maxPlayers) return '名额已满'
      return '立即报名'
    })

    const onClickLeft = () => {
      router.back()
    }

    const handleRegister = () => {
      // TODO: 实现报名功能
      showToast('报名功能开发中')
    }

    return {
      gameInfo,
      canRegister,
      registerButtonText,
      onClickLeft,
      handleRegister
    }
  }
}
</script>

<style scoped>
.game-detail {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 80px;
}

.detail-content {
  padding: 16px;
}

.game-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.game-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.game-status {
  font-size: 14px;
  padding: 2px 8px;
  border-radius: 4px;
}

.game-status.registering {
  color: #1989fa;
  background-color: #e6f3ff;
}

.info-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .van-icon {
  font-size: 16px;
  margin-right: 8px;
  color: #666;
}

.info-item .label {
  color: #666;
  margin-right: 8px;
}

.description-card,
.requirements-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

h3 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: bold;
}

ul {
  margin: 0;
  padding-left: 20px;
}

li {
  color: #666;
  margin-bottom: 8px;
}

.action-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 16px;
  background: white;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.05);
}
</style> 