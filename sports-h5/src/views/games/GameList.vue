<template>
  <tabbar-layout>
    <div class="game-list">
      <van-nav-bar title="赛事" />
      
      <!-- 赛事列表 -->
      <div class="game-content">
        <van-empty v-if="games.length === 0" description="暂无赛事" />
        <div v-else class="game-items">
          <div v-for="game in games" :key="game.id" class="game-item" @click="goToGameDetail(game)">
            <div class="game-header">
              <span class="game-status" :class="game.status">{{ game.statusText }}</span>
              <span class="game-time">{{ game.startTime }}</span>
            </div>
            <div class="game-body">
              <div class="game-title">{{ game.title }}</div>
              <div class="game-info">
                <span class="info-item">
                  <van-icon name="location-o" />
                  {{ game.location }}
                </span>
                <span class="info-item">
                  <van-icon name="friends-o" />
                  {{ game.playerCount }}/{{ game.maxPlayers }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </tabbar-layout>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import TabbarLayout from '@/components/layout/TabbarLayout.vue'

export default {
  name: 'GameList',
  components: {
    TabbarLayout
  },
  setup() {
    const router = useRouter()
    const games = ref([])

    onMounted(() => {
      // TODO: 加载赛事列表数据
      games.value = [
        {
          id: 1,
          title: '2025年春季乒乓球联赛',
          status: 'registering',
          statusText: '报名中',
          startTime: '2025-03-01 09:00',
          location: '上海市体育中心',
          playerCount: 16,
          maxPlayers: 32
        }
      ]
    })

    const goToGameDetail = (game) => {
      router.push(`/games/${game.id}`)
    }

    return {
      games,
      goToGameDetail
    }
  }
}
</script>

<style scoped>
.game-list {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.game-content {
  padding: 12px;
}

.game-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
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

.game-time {
  color: #666;
  font-size: 14px;
}

.game-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.game-info {
  display: flex;
  gap: 16px;
}

.info-item {
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.info-item .van-icon {
  margin-right: 4px;
  font-size: 16px;
}
</style> 