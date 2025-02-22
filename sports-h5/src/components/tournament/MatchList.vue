<template>
  <div class="match-list">
    <div v-for="(match, index) in matches" :key="match.id" class="match-item">
      <div class="match-players">
        <div class="player">
          <span class="player-number">#{{ index + 1 }}</span>
          <span class="player-name">{{ match.player1Name }}</span>
        </div>
        <div class="score-section" @click="showScoreDialog(match)">
          <van-tag 
            :type="getScoreTagType(match.player1Score, match.player2Score)" 
            size="medium"
            class="score-tag"
          >
            {{ match.player1Score || 0 }}
          </van-tag>
          <span class="score-separator">:</span>
          <van-tag 
            :type="getScoreTagType(match.player2Score, match.player1Score)" 
            size="medium"
            class="score-tag"
          >
            {{ match.player2Score || 0 }}
          </van-tag>
        </div>
        <div class="player player-right">
          <span class="player-name">{{ match.player2Name }}</span>
          <span class="player-number">#{{ index + 2 }}</span>
        </div>
      </div>
      <div class="match-footer">
        <van-tag 
          :type="getStatusTagType(match.status)"
          size="medium"
        >
          {{ getStatusText(match.status) }}
        </van-tag>
        <van-button 
          v-if="match.status !== 'FINISHED'"
          type="primary" 
          size="small"
          @click="showScoreDialog(match)"
        >
          设置比分
        </van-button>
      </div>
    </div>

    <!-- 比分设置面板 -->
    <div class="score-panel" :class="{ 'score-panel-show': dialogVisible }">
      <div class="score-panel-mask" @click="dialogVisible = false"></div>
      <div class="score-panel-content">
        <div class="score-panel-header">
          <span>设置比分</span>
          <van-icon name="cross" @click="dialogVisible = false" />
        </div>
        <div class="score-panel-body">
          <div class="score-row">
            <span class="player-name">{{ currentMatch?.player1Name }}</span>
            <input
              v-model="player1Score"
              type="tel"
              class="score-input"
              placeholder="比分"
            />
          </div>
          <div class="score-row">
            <span class="player-name">{{ currentMatch?.player2Name }}</span>
            <input
              v-model="player2Score"
              type="tel"
              class="score-input"
              placeholder="比分"
            />
          </div>
        </div>
        <div class="score-panel-footer">
          <van-button plain block @click="dialogVisible = false">取消</van-button>
          <van-button type="primary" block @click="handleScoreConfirm">确认</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast, Dialog, Button, Tag } from 'vant'

const props = defineProps({
  matches: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['score-update'])

const dialogVisible = ref(false)
const currentMatch = ref(null)
const player1Score = ref('')
const player2Score = ref('')

const getScoreTagType = (score1, score2) => {
  if (!score1 || !score2) return 'primary'
  return parseInt(score1) > parseInt(score2) ? 'success' : 'warning'
}

const getStatusTagType = (status) => {
  switch (status) {
    case 'FINISHED':
      return 'success'
    case 'ONGOING':
      return 'primary'
    default:
      return 'default'
  }
}

const getStatusText = (status) => {
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

const showScoreDialog = (match) => {
  currentMatch.value = match
  player1Score.value = match.player1Score || ''
  player2Score.value = match.player2Score || ''
  dialogVisible.value = true
}

const handleScoreConfirm = () => {
  const score1 = parseInt(player1Score.value)
  const score2 = parseInt(player2Score.value)

  if (isNaN(score1) || isNaN(score2)) {
    showToast('请输入有效的比分')
    return
  }

  if (score1 < 0 || score2 < 0) {
    showToast('比分不能为负数')
    return
  }

  if (score1 === score2) {
    showToast('比分不能相等')
    return
  }

  emit('score-update', currentMatch.value.id, score1, score2)
  dialogVisible.value = false
}
</script>

<style scoped>
.match-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.match-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.match-players {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.player {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.player-right {
  justify-content: flex-end;
}

.player-name {
  font-size: 14px;
  color: #323233;
}

.player-number {
  font-size: 12px;
  color: #969799;
  background-color: #f7f8fa;
  padding: 2px 6px;
  border-radius: 10px;
}

.score-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  cursor: pointer;
}

.score-tag {
  min-width: 32px;
  text-align: center;
}

.score-separator {
  font-size: 16px;
  font-weight: bold;
  color: #323233;
}

.match-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.score-panel {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  visibility: hidden;
  transform: translateY(100%);
  transition: transform 0.3s, visibility 0.3s;
}

.score-panel-show {
  visibility: visible;
  transform: translateY(0);
}

.score-panel-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9998;
}

.score-panel-content {
  position: relative;
  background: white;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
  max-height: 90vh;
  z-index: 9999;
}

.score-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #ebedf0;
  font-size: 16px;
  font-weight: 500;
}

.score-panel-body {
  padding: 20px 16px;
}

.score-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.score-row:last-child {
  margin-bottom: 0;
}

.player-name {
  flex: 1;
  font-size: 16px;
  color: #323233;
  margin-right: 16px;
}

.score-input {
  width: 100px;
  height: 44px;
  background: #f7f8fa;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  text-align: center;
  font-size: 16px;
  color: #323233;
}

.score-panel-footer {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #ebedf0;
}

:deep(.van-button) {
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}
</style> 