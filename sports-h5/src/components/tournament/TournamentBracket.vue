<template>
  <div class="tournament-bracket">
    <!-- 淘汰赛对阵图 -->
    <div class="bracket-container">
      <div v-for="(round, roundIndex) in rounds" :key="roundIndex" class="round">
        <div class="round-title">{{ getRoundTitle(roundIndex) }}</div>
        <div class="matches">
          <div v-for="(match, matchIndex) in round" :key="match.id" class="match-container">
            <div class="match">
              <div :id="roundIndex" class="match-wrapper" @click="handleMatchClick(match)">
                <div class="player" :class="{ 'winner': match.winner === 'PLAYER1' }">
                  <span class="player-name">{{ match.player1Name || '轮空' }}</span>
                  <span class="score">{{ match.player1Score || 0 }}</span>
                </div>
                <div class="player" :class="{ 'winner': match.winner === 'PLAYER2' }">
                  <span class="player-name">{{ match.player2Name || '轮空' }}</span>
                  <span class="score">{{ match.player2Score || 0 }}</span>
                </div>

                <!-- 连接点 -->
                <template v-if="roundIndex >= 0">
                  <div class="connector-point right" v-if="roundIndex < rounds.length - 1" :data-match-id="match.id"></div>
                  <div class="connector-point left" v-if="roundIndex > 0" :data-match-id="match.id"></div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- SVG 容器用于绘制连接线 -->
    <svg class="connections-svg" ref="svgContainer">
      <path
        v-for="(path, index) in connectionPaths"
        :key="index"
        :d="path"
        class="connector-path"
      />
    </svg>

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
import { ref, computed, watch, onMounted, nextTick } from 'vue'
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

// SVG 容器引用
const svgContainer = ref(null)

// 连接线路径数组
const connectionPaths = ref([])

// 更新连接线
// 该方法负责计算和绘制淘汰赛对阵图中的SVG连接线
// 1. 遍历每一轮比赛
// 2. 计算每场比赛的连接点坐标
// 3. 使用贝塞尔曲线绘制连接线，确保线条平滑美观
const updateConnections = async () => {
  await nextTick()
  connectionPaths.value = []
  
  rounds.value.forEach((round, roundIndex) => {
    if (roundIndex < rounds.value.length - 1) {
      // 遍历当前轮次的所有比赛，为每场比赛创建与下一轮的连接
      const currentRoundMatches = document.querySelectorAll(`.match-wrapper[id="${roundIndex}"]`)
      const nextRoundMatches = document.querySelectorAll(`.match-wrapper[id="${roundIndex + 1}"]`)
      
      currentRoundMatches.forEach((currentMatch, matchIndex) => {
        const nextMatchIndex = Math.floor(matchIndex / 2)
        if (nextMatchIndex < nextRoundMatches.length) {
          const startPoint = currentMatch.querySelector('.connector-point.right')
          const endPoint = nextRoundMatches[nextMatchIndex].querySelector('.connector-point.left')
          
          if (startPoint && endPoint) {
            const startRect = startPoint.getBoundingClientRect()
            const endRect = endPoint.getBoundingClientRect()
            const svgRect = svgContainer.value.getBoundingClientRect()
            
            const start = {
              x: startRect.left - svgRect.left + startRect.width,
              y: startRect.top - svgRect.top + startRect.height / 2
            }
            
            const end = {
              x: endRect.left - svgRect.left,
              y: endRect.top - svgRect.top + endRect.height / 2
            }
            
            const controlPoint = {
              x: start.x + (end.x - start.x) / 2,
              y: start.y
            }
            
            const path = `M ${start.x} ${start.y} C ${controlPoint.x} ${start.y}, ${controlPoint.x} ${end.y}, ${end.x} ${end.y}`
            connectionPaths.value.push(path)
          }
        }
      })
    }
    console.log(connectionPaths.value);
  })
}

// 监听窗口大小变化
window.addEventListener('resize', updateConnections)

// 组件挂载后更新连接线
onMounted(() => {
  updateConnections()
})

// 监听比赛数据变化
watch(() => props.matches, () => {
  nextTick(() => {
    updateConnections()
  })
})
</script>

<style scoped>
.tournament-bracket {
  padding: 20px;
  overflow-x: auto;
  width: 100%;
  background-color: #f5f5f5;
}

.bracket-container {
  display: flex;
  gap: 60px;
  min-width: fit-content;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
}

.round {
  display: flex;
  flex-direction: column;
  position: relative;
  min-width: 200px;
}

.round-title {
  text-align: center;
  font-weight: bold;
  color: #666;
  padding: 8px 0;
  margin-bottom: 20px;
}

.matches {
  display: flex;
  flex-direction: column;
  position: relative;
}

.match-container {
  position: relative;
  margin-bottom: 60px;
}

/* 第一轮特殊处理 */
.round:nth-child(1) .match-container {
  margin-bottom: 60px;
}

/* 第二轮特殊处理 */
.round:nth-child(2) .match-container {
  margin-bottom: 120px;
}

.round:nth-child(2) .matches {
  padding-top: 90px;
}

/* 第三轮特殊处理 */
.round:nth-child(3) .match-container {
  margin-bottom: 240px;
}

.round:nth-child(3) .matches {
  padding-top: 180px;
}

/* 第四轮特殊处理 */
.round:nth-child(4) .matches {
  padding-top: 300px;
}

/* 最后一个比赛不需要底部间距 */
.match-container:last-child {
  margin-bottom: 0;
}

.match {
  position: relative;
  height: 80px;
}

.match-wrapper {
  background: #fff;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  overflow: visible;
  width: 200px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 2;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  height: 40px;
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

/* 连接点样式 */
.connector-point {
  position: absolute;
  width: 6px;
  height: 6px;
  background-color: #1e88e5;
  border-radius: 50%;
  z-index: 2;
}

.connector-point.right {
  right: -1px;
  top: 50%;
  transform: translate(0, -50%);
}

.connector-point.left {
  left: 0px;
  top: 50%;
  transform: translate(0, -50%);
}

/* SVG 容器样式 */
.connections-svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.connector-path {
  fill: none;
  stroke: #1e88e5;
  stroke-width: 2px;
}


/* 确保决赛垂直居中 */
.round:last-child {
  justify-content: center;
  align-self: center;
}

/* 弹窗样式保持不变 */
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

/* 媒体查询，适配移动设备 */
@media screen and (max-width: 768px) {
  .bracket-container {
    gap: 30px;
  }
  
  .match-wrapper {
    width: 160px;
  }
  
  
  
}
</style>