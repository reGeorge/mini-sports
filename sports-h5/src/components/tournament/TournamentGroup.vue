<template>
  <div class="tournament-group">
    <!-- 小组赛积分表 -->
    <div class="group-container">
      <div class="group-header">
        <div class="header-cell">排名</div>
        <div class="header-cell player">选手</div>
        <div class="header-cell">胜</div>
        <div class="header-cell">负</div>
        <div class="header-cell">积分</div>
        <div class="header-cell">得分</div>
        <div class="header-cell">失分</div>
        <div class="header-cell">净胜分</div>
      </div>
      <div v-for="(player, index) in groupStandings" :key="player.id" class="group-row">
        <div class="cell rank">{{ index + 1 }}</div>
        <div class="cell player">{{ player.name }}</div>
        <div class="cell">{{ player.wins || 0 }}</div>
        <div class="cell">{{ player.losses || 0 }}</div>
        <div class="cell points">{{ player.points || 0 }}</div>
        <div class="cell">{{ player.totalScore || 0 }}</div>
        <div class="cell">{{ player.totalLost || 0 }}</div>
        <div class="cell" :class="{'positive': player.scoreDiff > 0, 'negative': player.scoreDiff < 0}">
          {{ player.scoreDiff > 0 ? '+' : ''}}{{ player.scoreDiff || 0 }}
        </div>
      </div>
    </div>

    <!-- 小组赛对阵表 -->
    <div class="matches-container">
      <div class="matches-header">对阵表</div>
      <div class="matches-grid">
        <div v-for="match in matches" :key="match.id" class="match-item" @click="handleMatchClick(match)">
          <div class="match-info">
            <div class="match-players">
              <div class="player" :class="{ 'winner': match.winner === 'PLAYER1' }">
                {{ match.player1Name }}
              </div>
              <div class="vs">vs</div>
              <div class="player" :class="{ 'winner': match.winner === 'PLAYER2' }">
                {{ match.player2Name }}
              </div>
            </div>
            <div class="match-score" v-if="match.status === 'FINISHED'">
              {{ match.player1Score }} : {{ match.player2Score }}
            </div>
            <div class="match-status" v-else>
              {{ match.status === 'PENDING' ? '未开始' : '进行中' }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 比分设置弹窗 -->
    <van-popup 
      v-model:show="showScoreDialog" 
      round 
      position="center"
      close-on-click-overlay
      teleport="body"
      :style="{ width: '90%', maxWidth: '320px', top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }"
      :overlay-style="{ background: 'rgba(0, 0, 0, 0.7)' }"
    >
      <div class="score-dialog">
        <div class="dialog-header">
          <span>设置比分</span>
          <van-icon name="cross" @click="showScoreDialog = false" />
        </div>
        <div class="dialog-content">
          <div class="score-row">
            <span class="player-name">{{ currentMatch?.player1Name }}</span>
            <van-field
              v-model="player1Score"
              type="number"
              input-align="center"
              :placeholder="'请输入比分'"
              class="score-input"
            >
              <template #button>
                <div class="score-buttons">
                  <van-icon name="arrow-up" @click="adjustScore(1, 1)" />
                  <van-icon name="arrow-down" @click="adjustScore(1, -1)" />
                </div>
              </template>
            </van-field>
          </div>
          <div class="score-row">
            <span class="player-name">{{ currentMatch?.player2Name }}</span>
            <van-field
              v-model="player2Score"
              type="number"
              input-align="center"
              :placeholder="'请输入比分'"
              class="score-input"
            >
              <template #button>
                <div class="score-buttons">
                  <van-icon name="arrow-up" @click="adjustScore(2, 1)" />
                  <van-icon name="arrow-down" @click="adjustScore(2, -1)" />
                </div>
              </template>
            </van-field>
          </div>
        </div>
        <div class="dialog-footer">
          <van-button plain block @click="showScoreDialog = false" style="margin-bottom: 12px">取消</van-button>
          <van-button type="primary" block @click="handleScoreSubmit">确认</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { showToast } from 'vant'
import { updateMatchScore } from '@/api/match'

const props = defineProps({
  matches: {
    type: Array,
    default: () => []
  },
  tournamentId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['score-updated'])

const showScoreDialog = ref(false)
const currentMatch = ref(null)
const player1Score = ref('0')
const player2Score = ref('0')

const handleMatchClick = (match) => {
  if (match.status === 'FINISHED') return
  currentMatch.value = match
  player1Score.value = String(match.player1Score || 0)
  player2Score.value = String(match.player2Score || 0)
  showScoreDialog.value = true
}

const adjustScore = (player, delta) => {
  const scoreRef = player === 1 ? player1Score : player2Score
  const currentScore = parseInt(scoreRef.value) || 0
  scoreRef.value = String(Math.max(0, currentScore + delta))
}

const handleScoreSubmit = async () => {
  try {
    const score1 = parseInt(player1Score.value) || 0
    const score2 = parseInt(player2Score.value) || 0
    
    if (score1 === score2) {
      showToast('比分不能相等')
      return
    }

    await updateMatchScore({
      tournamentId: props.tournamentId,
      matchId: currentMatch.value.id,
      player1Score: score1,
      player2Score: score2
    })

    showToast('比分更新成功')
    showScoreDialog.value = false
    
    // 触发更新事件
    emit('score-updated')
  } catch (error) {
    console.error('更新比分失败:', error)
    showToast('比分更新失败')
  }
}

// 计算小组积分榜
const groupStandings = computed(() => {
  const players = new Map()
  
  // 初始化所有选手数据
  props.matches.forEach(match => {
    if (!players.has(match.player1Id)) {
      players.set(match.player1Id, {
        id: match.player1Id,
        name: match.player1Name,
        wins: 0,
        losses: 0,
        points: 0
      })
    }
    if (!players.has(match.player2Id)) {
      players.set(match.player2Id, {
        id: match.player2Id,
        name: match.player2Name,
        wins: 0,
        losses: 0,
        points: 0
      })
    }
  })

  // 统计比赛结果
  props.matches.forEach(match => {
    if (match.status === 'FINISHED') {
      const player1 = players.get(match.player1Id)
      const player2 = players.get(match.player2Id)
      
      if (match.winner === 'PLAYER1') {
        player1.wins++
        player1.points += 2
        player2.losses++
      } else if (match.winner === 'PLAYER2') {
        player2.wins++
        player2.points += 2
        player1.losses++
      }
    }
  })

  // 转换为数组并排序
  return Array.from(players.values()).sort((a, b) => {
    if (b.points !== a.points) {
      return b.points - a.points
    }
    if (b.wins !== a.wins) {
      return b.wins - a.wins
    }
    return a.losses - b.losses
  })
})
</script>

<style scoped>
.tournament-group {
  padding: 16px;
}

.group-container {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 24px;
}

.group-header {
  display: flex;
  background-color: #f5f5f5;
  font-weight: bold;
  border-bottom: 1px solid #e5e5e5;
}

.header-cell {
  padding: 12px 8px;
  flex: 1;
  text-align: center;
  font-size: 14px;
}

.header-cell.player {
  flex: 2;
  text-align: left;
}

.group-row {
  display: flex;
  border-bottom: 1px solid #f5f5f5;
}

.group-row:last-child {
  border-bottom: none;
}

.cell {
  padding: 12px 8px;
  flex: 1;
  text-align: center;
}

.cell.player {
  flex: 2;
  text-align: left;
}

.cell.rank {
  color: #666;
  font-weight: bold;
}

.points {
  font-weight: bold;
  color: #1989fa;
}

.positive {
  color: #07c160;
}

.negative {
  color: #ee0a24;
}

.matches-container {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.matches-header {
  padding: 12px 16px;
  font-weight: bold;
  background-color: #f5f5f5;
  border-bottom: 1px solid #e5e5e5;
}

.matches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  padding: 16px;
}

.match-item {
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.match-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.match-item:active {
  transform: translateY(0);
}

.match-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.match-players {
  display: flex;
  align-items: center;
  gap: 8px;
}

.player {
  flex: 1;
  padding: 4px 8px;
}

.player.winner {
  font-weight: bold;
  color: #4caf50;
}

.vs {
  color: #999;
  font-size: 12px;
}

.match-score {
  text-align: center;
  font-weight: bold;
  color: #333;
}

.match-status {
  text-align: center;
  color: #999;
  font-size: 12px;
}

.score-dialog {
  padding: 20px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 18px;
  font-weight: 500;
}

.dialog-content {
  padding: 0 0 24px;
}

.score-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.score-row:last-child {
  margin-bottom: 0;
}

.player-name {
  flex: 1;
  margin-right: 16px;
  font-size: 16px;
}

.dialog-footer {
  padding-top: 0;
}

.score-input {
  width: 120px;
  :deep(.van-field__control) {
    height: 36px;
    font-size: 18px;
  }
}

.score-buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-left: 8px;
}

:deep(.van-icon) {
  font-size: 20px;
  color: #666;
  cursor: pointer;
  padding: 4px;
}

:deep(.van-icon:active) {
  opacity: 0.7;
}

:deep(.van-popup) {
  max-height: 90vh;
  overflow-y: auto;
}

:deep(.van-field__button) {
  min-width: auto;
}
</style> 