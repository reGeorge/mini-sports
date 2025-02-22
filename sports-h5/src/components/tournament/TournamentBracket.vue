<template>
  <div class="tournament-bracket">
    <!-- 淘汰赛对阵图 -->
    <div class="bracket-container">
      <div v-for="(round, roundIndex) in rounds" :key="roundIndex" class="round">
        <div class="round-title">{{ getRoundTitle(roundIndex) }}</div>
        <div class="matches">
          <div v-for="match in round" :key="match.id" class="match">
            <div class="match-wrapper">
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
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  matches: {
    type: Array,
    default: () => []
  }
})

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
</style> 