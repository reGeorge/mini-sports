<template>
  <div class="tournament-schedule">
    <!-- 小组赛阶段 -->
    <div v-if="groupStage" class="stage-section">
      <div class="stage-header">
        <h3 class="stage-title">小组赛</h3>
        <van-tag class="stage-status" :type="getStageStatusType(groupStage.status)">{{ getStageStatusText(groupStage.status) }}</van-tag>
      </div>
      
      <!-- 小组列表 -->
      <div class="groups-container">
        <van-collapse v-model="activeGroups" accordion>
          <van-collapse-item 
            v-for="group in groups" 
            :key="group.id" 
            :name="group.id"
            :title="group.name"
            class="group-card"
          >
            <div class="group-matches">
              <div v-for="(match, index) in group.matches" :key="match.id" class="match-item" @click="onMatchClick(match)">
                <div class="match-index">{{ index + 1 }}</div>
                <div class="match-players">
                  <span class="player" @click.stop="onPlayerClick(match.player1Id, match.player1Name, match.player1Points, match.player1Level, match.player1GripType, match.player1RacketConfig)">{{ match.player1Name }}</span>
                  <div class="match-info">
                        <div class="match-score">
                            <van-tag 
                              :type="getScoreType(match.player1Score, match.player2Score)" 
                              class="score-tag"
                              @click.stop="onScoreClick(match)"
                            >
                                <span>{{ match.player1Score || 0 }}</span>
                                <span>:</span>
                                <span>{{ match.player2Score || 0 }}</span>
                            </van-tag>
                        </div>    
                    </div>
                    <span class="player" @click.stop="onPlayerClick(match.player2Id, match.player2Name, match.player2Points, match.player2Level, match.player2GripType, match.player2RacketConfig)">{{ match.player2Name }}</span>
                </div>
                <van-tag class="status-tag" :type="getMatchStatusType(match.status)">{{ getMatchStatusText(match.status) }}</van-tag>
              </div>
            </div>
          </van-collapse-item>
        </van-collapse>
      </div>
    </div>

    <!-- 淘汰赛阶段 -->
    <div v-if="knockoutStage" class="stage-section">
      <div class="stage-header">
        <h3 class="stage-title">淘汰赛</h3>
        <van-tag class="stage-status" :type="getStageStatusType(knockoutStage.status)">{{ getStageStatusText(knockoutStage.status) }}</van-tag>
      </div>
      <div class="knockout-matches">
        <div v-for="(match, index) in knockoutMatches" :key="match.id" class="match-item" @click="onMatchClick(match)">
          <div class="match-index">{{ index + 1 }}</div>
          <div class="match-players">
            <span class="player" @click.stop="onPlayerClick(match.player1Id, match.player1Name, match.player1Points, match.player1Level, match.player1GripType, match.player1RacketConfig)">{{ match.player1Name }}</span>
            <div class="match-info">
              <div class="match-score">
                <van-tag 
                  :type="getScoreType(match.player1Score, match.player2Score)" 
                  class="score-tag"
                  @click.stop="onScoreClick(match)"
                >
                  <span>{{ match.player1Score || 0 }}</span>
                  <span>:</span>
                  <span>{{ match.player2Score || 0 }}</span>
                </van-tag>
              </div>    
            </div>
            <span class="player" @click.stop="onPlayerClick(match.player2Id, match.player2Name, match.player2Points, match.player2Level, match.player2GripType, match.player2RacketConfig)">{{ match.player2Name }}</span>
          </div>
          <van-tag class="status-tag" :type="getMatchStatusType('ONGOING')">{{ getMatchStatusText('ONGOING') }}</van-tag>
        </div>
      </div>
    </div>
    <!-- 比分设置对话框 -->
    <van-dialog
      v-model:show="showScoreDialog"
      title="设置比分"
      show-cancel-button
      @confirm="confirmSetScore"
    >
      <div class="score-dialog-content">
        <van-field
          v-model="player1Score"
          type="number"
          :label="selectedMatch?.player1Name"
          placeholder="请输入比分"
        />
        <van-field
          v-model="player2Score"
          type="number"
          :label="selectedMatch?.player2Name"
          placeholder="请输入比分"
        />
      </div>
    </van-dialog>
    <van-dialog
      v-model:show="showUserDetailDialog"
      title="选手详情"
      width="90%"
    >
      <div class="user-detail-content" v-if="selectedUser">
        <div class="detail-item">
          <span class="label">ID：</span>
          <span class="value">{{ selectedUser.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">昵称：</span>
          <span class="value">{{ selectedUser.nickname }}</span>
        </div>
        <div class="detail-item">
          <span class="label">当前积分：</span>
          <span class="value">{{ selectedUser.points || 0 }}</span>
        </div>
        <div class="detail-item">
          <span class="label">水平级别：</span>
          <span class="value">{{ selectedUser.level || 'BEGINNER' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">握拍方式：</span>
          <span class="value">{{ selectedUser.gripType || '未设置' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">球拍配置：</span>
          <span class="value">{{ selectedUser.racketConfig || '未设置' }}</span>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, Collapse, CollapseItem, Dialog, Field } from 'vant'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { usePermission } from '@/utils/permission'

// 用于控制分组折叠面板的展开状态，初始值为空字符串表示全部折叠
const activeGroups = ref('')
const showScoreDialog = ref(false)
const showUserDetailDialog = ref(false)
const selectedMatch = ref(null)
const selectedUser = ref(null)
const player1Score = ref('')
const player2Score = ref('')
const { hasPermission } = usePermission()
const router = useRouter()

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

// 获取赛事阶段状态样式
const getStageStatusType = (status) => {
  switch (status) {
    case 'FINISHED':
      return 'success'
    case 'ONGOING':
      return 'warning'
    default:
      return 'primary'
  }
}

// 获取赛事阶段状态文本
const getStageStatusText = (status) => {
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

// 点击选手名称时的处理函数
const onPlayerClick = (id, nickname, points, level, gripType, racketConfig) => {
  selectedUser.value = {
    id,
    nickname,
    points: points || 0,
    level: level || 'BEGINNER',
    gripType: gripType || '未设置',
    racketConfig: racketConfig || '未设置'
  }
  showUserDetailDialog.value = true
}

// 点击比赛项目时的处理函数
const onMatchClick = (match) => {
  // 点击整个比赛项目时不再处理比分设置
  // 可以在这里添加其他比赛详情相关的逻辑
}

// 点击比分标签时的处理函数
const onScoreClick = (match) => {
  if (hasPermission('match:score')) {
    selectedMatch.value = match
    player1Score.value = match.player1Score?.toString() || ''
    player2Score.value = match.player2Score?.toString() || ''
    showScoreDialog.value = true
  } else {
    showToast('您没有权限设置比分');
  }
}

// 确认设置比分
const confirmSetScore = async () => {
  if (!player1Score.value || !player2Score.value) {
    showToast('请输入双方比分')
    return
  }

  try {
    await request({
      url: `/tournaments/${props.tournamentId}/matches/${selectedMatch.value.id}/score`,
      method: 'put',
      data: {
        player1Score: parseInt(player1Score.value),
        player2Score: parseInt(player2Score.value)
      }
    })
    showToast('比分设置成功')
    showScoreDialog.value = false
    loadStages() // 刷新赛程数据
  } catch (error) {
    showToast('比分设置失败')
  }
}

// 获取比分标签样式
const getScoreType = (score1, score2) => {
  if (!score1 && !score2) return 'primary'
  return parseInt(score1) > parseInt(score2) ? 'success' : 'warning'
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
  margin-bottom: 12px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  padding: 4px;
}

:deep(.van-collapse-item__content) {
  padding: 12px;
  background: #f7f8fa;
}

:deep(.van-collapse-item__title) {
  font-weight: bold;
  color: #323233;
  background: #f7f8fa;
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
  background: #ffffff;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.match-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
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
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.player {
  font-size: 14px;
  color: #323233;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px 8px;
}

.player:hover {
  color: #1989fa;
  transform: translateY(-1px);
}

.match-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.vs {
  color: #969799;
  font-size: 14px;
  margin: 0 8px;
}

.match-score {
  font-size: 16px;
  font-weight: 500;
  color: #1989fa;
  display: flex;
  align-items: center;
  gap: 4px;
}

.match-score span {
  margin: 0 2px;
}

.knockout-matches {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.score-dialog-content {
  padding: 20px 16px;
}
.user-detail-content {
  padding: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  line-height: 20px;
}

.detail-item .label {
  color: #646566;
  width: 80px;
  flex-shrink: 0;
}

.detail-item .value {
  color: #323233;
  flex: 1;
}
.score-tag {
  padding: 2px 4px;
  font-weight: bold;
}

.score-tag :deep(.van-tag__content) {
  font-size: 10px;
}

.status-tag {
  padding: 2px 4px;
  font-weight: bold;
}
.stage-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.stage-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
</style>