<template>
  <div class="tournament-schedule">
    <!-- 小组赛阶段 -->
    <div v-if="groupStage" class="stage-section">
      <h3 class="stage-title">小组赛</h3>
      
      <!-- 小组列表 -->
      <div class="groups-container">
        <van-collapse v-model:active-names="activeGroups" accordion>
          <van-collapse-item 
            v-for="group in groups" 
            :key="group.id" 
            :name="group.id"
            :title="group.name"
            class="group-card"
          >
            <div class="group-matches">
              <div v-for="(match, index) in group.matches" :key="match.id" class="match-item">
                <div class="match-index">{{ index + 1 }}</div>
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
          </van-collapse-item>
        </van-collapse>
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
import { showToast, Collapse, CollapseItem } from 'vant'
import request from '@/utils/request'

// 用于控制分组折叠面板的展开状态，初始值为空数组表示全部折叠
const activeGroups = ref([])

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
    
    // 处理小组赛数据
    if (groupStage.value && groupStage.value.matches) {
      // 根据groupId对matches进行分组
      const groupedMatches = groupStage.value.matches.reduce((acc, match) => {
        if (!acc[match.groupId]) {
          acc[match.groupId] = {
            id: match.groupId,
            name: `Group ${String.fromCharCode(65 + Object.keys(acc).length)}`,
            matches: []
          }
        }
        acc[match.groupId].matches.push(match)
        return acc
      }, {})
      
      groups.value = Object.values(groupedMatches)
    }
    
    // 处理淘汰赛数据
    if (knockoutStage.value && knockoutStage.value.matches) {
      knockoutMatches.value = knockoutStage.value.matches
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
  display: block;
  width: 100%;
}

.group-card {
  margin-bottom: 8px;
  background: #fff;
  border-radius: 8px;
}

:deep(.van-collapse-item__content) {
  padding: 12px;
}

:deep(.van-collapse-item__title) {
  font-weight: bold;
  color: #323233;
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
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.match-index {
  width: 24px;
  height: 24px;
  background-color: #f2f3f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.match-players {
  flex: 1;
  display: flex;
  flex-direction: row;
  gap: 8px;
}

.player {
  color: #323233;
  font-size: 14px;
}

.vs {
  color: #969799;
  font-size: 12px;
  margin: 4px 0;
}

.match-score {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 4px 0;
  font-size: 14px;
  font-weight: bold;
  color: #1989fa;
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