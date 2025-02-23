<template>
  <div class="match-history">
    <!-- 统计数据卡片 -->
    <div class="stats-card">
      <van-row>
        <van-col span="8">
          <div class="stat-item">
            <div class="stat-value">{{ stats.totalMatches || 0 }}</div>
            <div class="stat-label">总场次</div>
          </div>
        </van-col>
        <van-col span="8">
          <div class="stat-item">
            <div class="stat-value">{{ stats.wins || 0 }}</div>
            <div class="stat-label">胜场</div>
          </div>
        </van-col>
        <van-col span="8">
          <div class="stat-item">
            <div class="stat-value">{{ (stats.winRate || 0).toFixed(1) }}%</div>
            <div class="stat-label">胜率</div>
          </div>
        </van-col>
      </van-row>
      <van-row style="margin-top: 20px;">
        <van-col span="12">
          <div class="stat-item">
            <div class="stat-value">{{ (stats.avgScore || 0).toFixed(1) }}</div>
            <div class="stat-label">场均得分</div>
          </div>
        </van-col>
        <van-col span="12">
          <div class="stat-item">
            <div class="stat-value">{{ (stats.avgLost || 0).toFixed(1) }}</div>
            <div class="stat-label">场均失分</div>
          </div>
        </van-col>
      </van-row>
    </div>

    <!-- 比赛历史列表 -->
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div v-for="match in matchList" :key="match.id" class="match-item">
        <div class="match-header">
          <span class="match-time">{{ formatDate(match.createdAt) }}</span>
          <span :class="['match-result', getResultClass(match)]">
            {{ getResultText(match) }}
          </span>
        </div>
        <div class="match-content">
          <div class="player" :class="{ 'winner': match.winner === 'PLAYER1' }">
            <span class="player-name">{{ match.player1Name }}</span>
            <span class="score">{{ match.player1Score }}</span>
          </div>
          <div class="vs">VS</div>
          <div class="player" :class="{ 'winner': match.winner === 'PLAYER2' }">
            <span class="player-name">{{ match.player2Name }}</span>
            <span class="score">{{ match.player2Score }}</span>
          </div>
        </div>
        <div class="match-footer">
          <div class="points-info" :class="getPointsClass(match)">
            <van-icon :name="getPointsIcon(match)" />
            <span>{{ getPointsText(match) }}</span>
          </div>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserMatchHistory, getUserMatchStats } from '@/api/match'
import { showToast } from 'vant'

const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10
const matchList = ref([])
const stats = ref({})

// 加载比赛历史
const onLoad = async () => {
  try {
    loading.value = true
    const res = await getUserMatchHistory({
      pageNum: pageNum.value,
      pageSize
    })
    
    matchList.value.push(...res.data.list)
    
    if (res.data.list.length < pageSize) {
      finished.value = true
    } else {
      pageNum.value++
    }
  } catch (error) {
    console.error('加载比赛历史失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getUserMatchStats()
    stats.value = res.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
    showToast('加载统计数据失败')
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取比赛结果文本
const getResultText = (match) => {
  const currentUserId = localStorage.getItem('userId')
  if (match.winnerId === currentUserId) {
    return '胜'
  }
  return '负'
}

// 获取结果样式类
const getResultClass = (match) => {
  const currentUserId = localStorage.getItem('userId')
  return match.winnerId === currentUserId ? 'win' : 'lose'
}

// 获取积分变更样式
const getPointsClass = (match) => {
  return {
    'points-increase': match.pointsChange > 0,
    'points-decrease': match.pointsChange < 0
  }
}

// 获取积分图标
const getPointsIcon = (match) => {
  return match.pointsChange > 0 ? 'arrow-up' : 'arrow-down'
}

// 获取积分变更文本
const getPointsText = (match) => {
  const change = match.pointsChange > 0 ? `+${match.pointsChange}` : match.pointsChange
  return `积分${change} (${match.pointsBalance})`
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.match-history {
  padding: 16px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.stats-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.match-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.match-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.match-time {
  color: #666;
  font-size: 14px;
}

.match-result {
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 4px;
}

.match-result.win {
  color: #07c160;
  background: #e8f5e9;
}

.match-result.lose {
  color: #ee0a24;
  background: #fee;
}

.match-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.player {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-radius: 4px;
}

.player.winner {
  background-color: #e8f5e9;
}

.player-name {
  font-size: 16px;
  color: #333;
}

.score {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.vs {
  margin: 0 12px;
  color: #999;
  font-size: 14px;
}

.match-footer {
  margin-top: 12px;
  padding-top: 8px;
  border-top: 1px solid #f5f5f5;
}

.points-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.points-increase {
  color: #07c160;
}

.points-decrease {
  color: #ee0a24;
}

:deep(.van-icon) {
  font-size: 16px;
}
</style> 