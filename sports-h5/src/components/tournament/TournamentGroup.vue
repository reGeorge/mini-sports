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
      </div>
      <div v-for="(player, index) in groupStandings" :key="player.id" class="group-row">
        <div class="cell rank">{{ index + 1 }}</div>
        <div class="cell player">{{ player.name }}</div>
        <div class="cell">{{ player.wins }}</div>
        <div class="cell">{{ player.losses }}</div>
        <div class="cell">{{ player.points }}</div>
      </div>
    </div>

    <!-- 小组赛对阵表 -->
    <div class="matches-container">
      <div class="matches-header">对阵表</div>
      <div class="matches-grid">
        <div v-for="match in matches" :key="match.id" class="match-item">
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  matches: {
    type: Array,
    default: () => []
  }
})

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
  padding: 12px;
  flex: 1;
  text-align: center;
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
  padding: 12px;
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
</style> 