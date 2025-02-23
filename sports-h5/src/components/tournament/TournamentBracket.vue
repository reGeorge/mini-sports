<template>
  <div class="tournament-bracket">
    <!-- 淘汰赛对阵图 -->
    <div class="bracket-container">
      <div v-for="(round, roundIndex) in rounds" :key="roundIndex" class="round">
        <div class="round-title">{{ getRoundTitle(roundIndex) }}</div>
        <div class="matches">
          <div v-for="match in round" :key="match.id" class="match">
            <div class="match-wrapper" @click="handleMatchClick(match)">
              <div class="player" :class="{ 'winner': match.winner === 'PLAYER1' }">
                <span class="player-name">{{ match.player1Name || '轮空' }}</span>
                <span class="score">{{ match.player1Score || 0 }}</span>
              </div>
              <div class="player" :class="{ 'winner': match.winner === 'PLAYER2' }">
                <span class="player-name">{{ match.player2Name || '轮空' }}</span>
                <span class="score">{{ match.player2Score || 0 }}</span>
              </div>
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
import { ref, computed, watch } from 'vue'
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
  if (match.status === 'FINISHED' || !match.player1Name || !match.player2Name) return
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

// 按轮次分组比赛
const rounds = computed(() => {
  if (!props.matches) return []
  
  const roundsMap = {}
  props.matches.forEach(match => {
    const round = match.round || 1
    if (!roundsMap[round]) {
      roundsMap[round] = []
    }
    roundsMap[round].push(match)
  })
  return Object.values(roundsMap)
})

// 获取轮次标题
const getRoundTitle = (index) => {
  const totalRounds = rounds.value.length
  if (index === totalRounds - 1) {
    return '决赛'
  } else if (index === totalRounds - 2) {
    return '半决赛'
  } else if (index === totalRounds - 3) {
    return '四分之一决赛'
  } else {
    return `第${index + 1}轮`
  }
}
</script>

<style scoped>
.tournament-bracket {
  padding: 20px;
  overflow-x: auto;
}

.bracket-container {
  display: flex;
  gap: 40px;
  min-width: fit-content;
}

.round {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
}

.round-title {
  text-align: center;
  font-weight: bold;
  color: #666;
  padding: 8px 0;
}

.matches {
  display: flex;
  flex-direction: column;
  gap: 30px;
  position: relative;
}

.match {
  position: relative;
}

.match-wrapper {
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  overflow: hidden;
  width: 200px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
}

.match-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.match-wrapper:active {
  transform: translateY(0);
}

.player {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #f5f5f5;
}

.player:last-child {
  border-bottom: none;
}

.player.winner {
  background-color: #e8f5e9;
  font-weight: bold;
}

.player-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.score {
  font-weight: bold;
  min-width: 24px;
  text-align: right;
}

/* 连接线样式 */
.match {
  display: flex;
  align-items: center;
}

/* 水平连接线 */
.match::after {
  content: '';
  position: absolute;
  left: 100%;
  top: 50%;
  width: 40px;
  height: 2px;
  background-color: #e5e5e5;
  transform: translateY(-50%);
}

/* 垂直连接线 */
.matches .match:nth-child(odd)::before {
  content: '';
  position: absolute;
  left: calc(100% + 40px);
  top: 50%;
  width: 2px;
  height: calc(100% + 30px);
  background-color: #e5e5e5;
}

/* 调整每轮的垂直间距 */
.round:nth-child(2) .matches {
  margin-top: 45px;
}

.round:nth-child(3) .matches {
  margin-top: 105px;
}

/* 最后一轮不需要连接线 */
.round:last-child .match::after,
.round:last-child .match::before {
  display: none;
}

/* 每轮最后一个比赛不需要垂直连接线 */
.matches .match:last-child::before {
  display: none;
}

/* 确保决赛垂直居中 */
.round:last-child {
  justify-content: center;
  margin-top: auto;
  margin-bottom: auto;
}

/* 调整第一轮的间距 */
.round:first-child .matches {
  margin-top: 0;
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