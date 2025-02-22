<template>
  <div class="tournament-schedule">
    <van-tabs v-model:active="activeTab" sticky swipeable animated>
      <!-- 小组赛 Tab -->
      <van-tab title="小组赛" name="group" v-if="groupStage">
        <div class="group-stage">
          <div v-for="group in groupMatches" :key="group.name" class="group-section">
            <h3>{{ group.name }}</h3>
            <tournament-group :matches="group.matches" />
          </div>
        </div>
      </van-tab>

      <!-- 淘汰赛 Tab -->
      <van-tab title="淘汰赛" name="knockout" v-if="knockoutStage">
        <div class="knockout-stage">
          <tournament-bracket :matches="knockoutStage.matches" />
        </div>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { showToast } from 'vant'
import { getTournamentStages } from '@/api/tournament'
import TournamentGroup from './TournamentGroup.vue'
import TournamentBracket from './TournamentBracket.vue'

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
  const stage = stagesData.value.find(stage => stage.type === 'KNOCKOUT')
  if (!stage) return null
  return {
    ...stage,
    matches: stage.matches || []
  }
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

// 加载赛事数据
const loadTournamentData = async () => {
  try {
    const response = await getTournamentStages(props.tournamentId)
    stagesData.value = response.data || []
    
    // 自动切换到进行中的阶段
    const ongoingStage = stagesData.value.find(stage => stage.status === 'ONGOING')
    if (ongoingStage) {
      activeTab.value = ongoingStage.type.toLowerCase()
    }
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

.group-section {
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