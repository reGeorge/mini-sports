<template>
  <div class="tournament-schedule">
    <van-tabs v-model:active="activeTab" sticky swipeable animated>
      <!-- 小组赛 Tab -->
      <van-tab title="小组赛" name="group" v-if="groupStage">
        <div class="group-stage">
          <div v-for="group in groupMatches" :key="group.name" class="group-section">
            <h3>{{ group.name }}</h3>
            <match-list 
              :matches="group.matches" 
              @score-update="handleScoreUpdate"
            />
          </div>
        </div>
      </van-tab>

      <!-- 淘汰赛 Tab -->
      <van-tab title="淘汰赛" name="knockout" v-if="knockoutStage">
        <div class="knockout-stage">
          <div v-for="round in knockoutRounds" :key="round" class="round-section">
            <h3>第{{ round }}轮</h3>
            <match-list 
              :matches="getMatchesByRound(round)" 
              @score-update="handleScoreUpdate"
            />
          </div>
        </div>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { showToast, Tab, Tabs } from 'vant'
import request from '@/utils/request'
import MatchList from './MatchList.vue'
import { getTournamentStages } from '@/api/tournament'

const activeTab = ref('group')
const stagesData = ref([])

const props = defineProps({
  tournamentId: {
    type: [String, Number],
    required: true
  }
})

// 获取小组赛阶段
const groupStage = computed(() => {
  return stagesData.value.find(stage => stage.type === 'GROUP')
})

// 获取淘汰赛阶段
const knockoutStage = computed(() => {
  return stagesData.value.find(stage => stage.type === 'KNOCKOUT')
})

// 获取淘汰赛轮次
const knockoutRounds = computed(() => {
  if (!knockoutStage.value?.matches) return []
  return [...new Set(knockoutStage.value.matches.map(match => match.round))]
    .sort((a, b) => a - b)
})

// 按小组分组比赛
const groupMatches = computed(() => {
  if (!groupStage.value?.matches) return []
  
  const groups = {}
  groupStage.value.matches.forEach(match => {
    const groupName = match.groupName || '默认小组'
    if (!groups[groupName]) {
      groups[groupName] = {
        name: groupName,
        matches: []
      }
    }
    groups[groupName].matches.push(match)
  })
  
  return Object.values(groups)
})

// 按轮次获取比赛
const getMatchesByRound = (round) => {
  if (!knockoutStage.value?.matches) return []
  return knockoutStage.value.matches.filter(match => match.round === round)
}

// 处理比分更新
const handleScoreUpdate = async (matchId, player1Score, player2Score) => {
  try {
    await request({
      url: `/tournaments/${props.tournamentId}/matches/${matchId}/score`,
      method: 'put',
      data: {
        player1Score,
        player2Score
      }
    })
    await loadTournamentData()
    showToast('比分更新成功')
  } catch (error) {
    console.error('更新比分失败:', error)
    showToast('比分更新失败')
  }
}

// 加载赛事数据
const loadTournamentData = async () => {
  try {
    const response = await getTournamentStages(props.tournamentId)
    stagesData.value = response.data || []
  } catch (error) {
    console.error('加载赛事数据失败:', error)
    showToast('加载数据失败')
  }
}

onMounted(() => {
  loadTournamentData()
})
</script>

<style scoped>
.tournament-schedule {
  min-height: 100vh;
  background-color: #f7f8fa;
}

:deep(.van-tabs) {
  background-color: #fff;
}

:deep(.van-tabs__wrap) {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

:deep(.van-tab) {
  font-size: 16px;
  padding: 12px 0;
}

:deep(.van-tab__pane) {
  padding: 16px;
}

.group-stage,
.knockout-stage {
  margin-top: 16px;
}

.group-section,
.round-section {
  margin-bottom: 24px;
}

h3 {
  margin: 0 0 16px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 16px;
  color: #333;
}
</style>