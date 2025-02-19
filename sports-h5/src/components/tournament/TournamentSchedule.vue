<template>
  <div class="tournament-schedule">
    <!-- 小组赛阶段 -->
    <div v-if="groupStage" class="stage-section">
      <h3 class="stage-title">小组赛</h3>
      
      <!-- 小组列表 -->
      <div class="groups-container">
        <div v-for="group in groups" :key="group.id" class="group-card">
          <div class="group-header">
            <h4>{{ group.name }}</h4>
          </div>
          <div class="group-matches">
            <div v-for="match in group.matches" :key="match.id" class="match-item">
              <div class="match-players">
                <span class="player">{{ match.player1Name }}</span>
                <span class="vs">VS</span>
                <span class="player">{{ match.player2Name }}</span>
              </div>
              <div class="match-score" v-if="match.status !== 'PENDING'">
                <span>{{ match.player1Score || 0 }}</span>
                <span>:</span>
                <span>{{ match.player2Score || 0 }}</span>
              </div>
              <van-tag :type="getMatchStatusType(match.status)">{{ getMatchStatusText(match.status) }}</van-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 淘汰赛阶段 -->
    <div v-if="knockoutStage" class="stage-section">
      <h3 class="stage-title">淘汰赛</h3>
      <div class="knockout-matches">
        <div v-for="match in knockoutMatches" :key="match.id" class="match-item">
          <div class="match-players">
            <span class="player">{{ match.player1Name }}</span>
            <span class="vs">VS</span>
            <span class="player">{{ match.player2Name }}</span>
          </div>
          <div class="match-score" v-if="match.status !== 'PENDING'">
            <span>{{ match.player1Score || 0 }}</span>
            <span>:</span>
            <span>{{ match.player2Score || 0 }}</span>
          </div>
          <van-tag :type="getMatchStatusType(match.status)">{{ getMatchStatusText(match.status) }}</van-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'

const props = defineProps({
  tournamentId: {
    type: [String, Number],
    required: true
  }
})

const groupStage = ref(null)
const knockoutStage = ref(null)
const groups = ref([])
const knockoutMatches = ref([])

// 获取赛事阶段信息
const loadStages = async () => {
  try {
    const res = await request({
      url: `/tournaments/${props.tournamentId}/stages`,
      method: 'get'
    })
    
    // 分别找到小组赛和淘汰赛阶段
    groupStage.value = res.data.find(stage => stage.type === 'GROUP')
    knockoutStage.value = res.data.find(stage => stage.type === 'KNOCKOUT')
    
    if (groupStage.value) {
      await loadGroups()
    }
    if (knockoutStage.value) {
      await loadKnockoutMatches()
    }
  } catch (error) {
    showToast('获取赛程信息失败')
  }
}

// 获取小组赛分组信息
const loadGroups = async () => {
  try {
    const res = await request({
      url: `/tournaments/${props.tournamentId}/stages/${groupStage.value.id}/groups`,
      method: 'get'
    })
    groups.value = res.data
  } catch (error) {
    showToast('获取分组信息失败')
  }
}

// 获取淘汰赛对阵信息
const loadKnockoutMatches = async () => {
  try {
    const res = await request({
      url: `/tournaments/${props.tournamentId}/stages/${knockoutStage.value.id}/matches`,
      method: 'get'
    })
    knockoutMatches.value = res.data
  } catch (error) {
    showToast('获取淘汰赛对阵信息失败')
  }
}

// 获取比赛状态样式
const getMatchStatusType = (status) => {
  switch (status) {
    case 'FINISHED':
      return 'success'
    case 'ONGOING':
      return 'warning'
    default:
      return 'primary'
  }
}

// 获取比赛状态文本
const getMatchStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '未开始'
    case 'ONGOING':
      return '进行中'
    case 'FINISHED':
      return '已结束'
    default:
      return '未知'
  }
}

onMounted(() => {
  loadStages()
})
</script>

<style scoped>
.tournament-schedule {
  padding: 16px;
}

.stage-section {
  margin-bottom: 24px;
}

.stage-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #323233;
}

.groups-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.group-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.group-header {
  margin-bottom: 12px;
  border-bottom: 1px solid #ebedf0;
  padding-bottom: 8px;
}

.group-header h4 {
  margin: 0;
  color: #323233;
  font-size: 16px;
}

.match-item {
  background: #f7f8fa;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 8px;
}

.match-players {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.player {
  flex: 1;
  text-align: center;
  color: #323233;
}

.vs {
  margin: 0 12px;
  color: #969799;
  font-size: 14px;
}

.match-score {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: bold;
}

.match-score span {
  margin: 0 4px;
}

.knockout-matches {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>