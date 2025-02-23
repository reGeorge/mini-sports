<template>
  <div class="match-history">
    <!-- 赛事列表 -->
    <div v-if="!selectedTournament" class="tournament-list">
      <van-nav-bar title="我参加的赛事" left-arrow @click-left="goBack" />
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadTournaments"
      >
        <div v-for="tournament in tournaments" :key="tournament.id" class="tournament-item" @click="selectTournament(tournament)">
          <div class="tournament-title">{{ tournament.title }}</div>
          <div class="tournament-info">
            <span>{{ tournament.type === 'SINGLES' ? '单打' : '双打' }}</span>
            <span>{{ formatDate(tournament.startTime) }}</span>
            <span :class="getStatusClass(tournament.status)">{{ getStatusText(tournament.status) }}</span>
          </div>
        </div>
      </van-list>
    </div>

    <!-- 比赛历史详情 -->
    <div v-else class="match-detail">
      <van-nav-bar 
        :title="selectedTournament.title" 
        left-arrow 
        @click-left="backToList" 
      />
      
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

      <!-- 比赛记录表格 -->
      <van-tabs v-model:active="activeTab" sticky>
        <!-- 小组赛 -->
        <van-tab title="小组赛">
          <van-table :columns="matchColumns" :data="groupMatches">
            <template #result="{ row }">
              <span :class="getResultClass(row)">{{ getResultText(row) }}</span>
            </template>
            <template #points="{ row }">
              <div :class="getPointsClass(row)">
                <van-icon :name="getPointsIcon(row)" />
                <span>{{ getPointsText(row) }}</span>
              </div>
            </template>
          </van-table>
        </van-tab>

        <!-- 淘汰赛 -->
        <van-tab title="淘汰赛">
          <van-table :columns="matchColumns" :data="knockoutMatches">
            <template #result="{ row }">
              <span :class="getResultClass(row)">{{ getResultText(row) }}</span>
            </template>
            <template #points="{ row }">
              <div :class="getPointsClass(row)">
                <van-icon :name="getPointsIcon(row)" />
                <span>{{ getPointsText(row) }}</span>
              </div>
            </template>
          </van-table>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserMatchHistory, getUserMatchStats } from '@/api/match'
import { getUserTournaments } from '@/api/tournament'
import { showToast } from 'vant'

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10
const tournaments = ref([])
const selectedTournament = ref(null)
const stats = ref({})
const activeTab = ref(0)
const groupMatches = ref([])
const knockoutMatches = ref([])

// 表格列定义
const matchColumns = [
  { title: '时间', key: 'createdAt', formatter: formatDate },
  { title: '对手', key: 'opponentName' },
  { title: '比分', key: 'score', formatter: (row) => `${row.player1Score}:${row.player2Score}` },
  { title: '结果', key: 'result', slot: 'result' },
  { title: '积分变更', key: 'points', slot: 'points' }
]

// 加载赛事列表
const loadTournaments = async () => {
  try {
    loading.value = true
    const res = await getUserTournaments({
      pageNum: pageNum.value,
      pageSize
    })
    
    tournaments.value.push(...res.data.list)
    
    if (res.data.list.length < pageSize) {
      finished.value = true
    } else {
      pageNum.value++
    }
  } catch (error) {
    console.error('加载赛事列表失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 选择赛事
const selectTournament = (tournament) => {
  selectedTournament.value = tournament
  loadMatchHistory(tournament.id)
}

// 返回列表
const backToList = () => {
  selectedTournament.value = null
  router.push('/match/history')
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 加载比赛历史
const loadMatchHistory = async (tournamentId) => {
  try {
    loading.value = true
    const [historyRes, statsRes] = await Promise.all([
      getUserMatchHistory({
        tournamentId,
        pageNum: 1,
        pageSize: 100
      }),
      getUserMatchStats(tournamentId)
    ])
    
    // 分离小组赛和淘汰赛数据
    const matches = historyRes.data.list || []
    groupMatches.value = matches.filter(m => m.groupName)
    knockoutMatches.value = matches.filter(m => !m.groupName)
    
    stats.value = statsRes.data
  } catch (error) {
    console.error('加载比赛历史失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 获取比赛结果文本
const getResultText = (match) => {
  const currentUserId = localStorage.getItem('userId')
  return match.winnerId === currentUserId ? '胜' : '负'
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

// 获取赛事状态样式
const getStatusClass = (status) => {
  return {
    'status-ongoing': status === 'ONGOING',
    'status-finished': status === 'FINISHED'
  }
}

// 获取赛事状态文本
const getStatusText = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'REGISTERING': '报名中',
    'ONGOING': '进行中',
    'FINISHED': '已结束'
  }
  return statusMap[status] || status
}

onMounted(() => {
  const tournamentId = route.params.tournamentId
  if (tournamentId) {
    // 如果URL中包含赛事ID,直接加载该赛事的比赛历史
    loadMatchHistory(tournamentId)
  } else {
    // 否则加载赛事列表
    loadTournaments()
  }
})
</script>

<style scoped>
.match-history {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.tournament-list {
  padding-bottom: 20px;
}

.tournament-item {
  background: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.tournament-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.tournament-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
}

.status-ongoing {
  color: #1989fa;
}

.status-finished {
  color: #07c160;
}

.stats-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin: 12px;
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

.win {
  color: #07c160;
}

.lose {
  color: #ee0a24;
}

.points-increase {
  color: #07c160;
  display: flex;
  align-items: center;
  gap: 4px;
}

.points-decrease {
  color: #ee0a24;
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.van-table) {
  margin: 12px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.van-tabs__wrap) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style> 