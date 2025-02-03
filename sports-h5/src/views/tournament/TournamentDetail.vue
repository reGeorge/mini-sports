<template>
  <div class="tournament-detail">
    <van-nav-bar
      title="赛事详情"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <!-- 赛事基本信息 -->
    <div class="info-card">
      <div class="header">
        <div class="title">{{ tournament.title }}</div>
        <van-tag :type="getStatusType(tournament.status)" size="medium">
          {{ getStatusText(tournament.status) }}
        </van-tag>
      </div>
      
      <div class="info-list">
        <div class="info-item">
          <van-icon name="clock-o" />
          <span class="label">比赛时间：</span>
          <span>{{ getDateRange(tournament.startTime, tournament.endTime) }}</span>
        </div>
        <div class="info-item">
          <van-icon name="location-o" />
          <span class="label">比赛地点：</span>
          <span>{{ tournament.location }}</span>
        </div>
        <div class="info-item">
          <van-icon name="friends-o" />
          <span class="label">参与人数：</span>
          <span>{{ tournament.currentPlayers }}/{{ tournament.maxPlayers }}</span>
        </div>
        <div class="info-item">
          <van-icon name="medal-o" />
          <span class="label">比赛类型：</span>
          <span>{{ getTypeText(tournament.type) }}</span>
        </div>
        <div class="info-item">
          <van-icon name="award-o" />
          <span class="label">赛事级别：</span>
          <span>{{ getLevelText(tournament.level) }}</span>
        </div>
        <div class="info-item">
          <van-icon name="gold-coin-o" />
          <span class="label">报名费用：</span>
          <span>{{ tournament.entryFee }}元</span>
        </div>
      </div>

      <div class="description">
        <div class="section-title">赛事说明</div>
        <p>{{ tournament.description }}</p>
      </div>
    </div>

    <!-- 报名列表 -->
    <div class="registration-list" v-if="hasPermission('tournament:audit')">
      <div class="section-title">报名列表</div>
      <van-cell-group>
        <van-cell
          v-for="registration in registrations"
          :key="registration.id"
          :title="registration.user.nickname"
          :label="formatDate(registration.createdAt, 'MM-DD HH:mm')"
        >
          <template #right-icon>
            <van-tag :type="getRegistrationStatusType(registration.status)">
              {{ getRegistrationStatusText(registration.status) }}
            </van-tag>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <!-- 编辑按钮 -->
      <template v-if="hasPermission('tournament:edit') && tournament.status === 'DRAFT'">
        <van-button 
          type="info" 
          block 
          @click="editTournament"
        >
          编辑赛事
        </van-button>
      </template>

      <!-- 状态操作按钮 -->
      <template v-if="hasPermission('tournament:edit')">
        <van-button 
          v-if="tournament.status === 'DRAFT'"
          type="primary" 
          block 
          @click="updateStatus('REGISTERING')"
        >
          开始报名
        </van-button>
        <van-button 
          v-if="tournament.status === 'REGISTERING'"
          type="primary" 
          block 
          @click="updateStatus('ONGOING')"
        >
          开始比赛
        </van-button>
        <van-button 
          v-if="tournament.status === 'ONGOING'"
          type="warning" 
          block 
          @click="updateStatus('FINISHED')"
        >
          结束比赛
        </van-button>
      </template>

      <!-- 报名按钮 -->
      <van-button 
        v-if="tournament.status === 'REGISTERING' && !isRegistered"
        type="primary" 
        block 
        @click="register"
      >
        立即报名
      </van-button>

      <!-- 取消报名按钮 -->
      <van-button 
        v-if="tournament.status === 'REGISTERING' && isRegistered && myRegistration?.status === 'PENDING'"
        type="danger" 
        block 
        @click="cancelRegistration"
      >
        取消报名
      </van-button>
    </div>

    <!-- 报名确认弹窗 -->
    <van-dialog
      v-model:show="showRegisterDialog"
      title="报名确认"
      show-cancel-button
      @confirm="confirmRegister"
    >
      <div class="register-dialog-content">
        <p>确认报名参加该赛事吗？</p>
        <p class="fee-info" v-if="tournament.entryFee > 0">
          需支付报名费：{{ tournament.entryFee }}元
        </p>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getTournamentById, updateTournamentStatus } from '@/api/tournament'
import { getRegistrations, register as registerApi, cancelRegistration as cancelRegistrationApi } from '@/api/registration'
import { hasPermission } from '@/utils/permission'
import { formatDate } from '@/utils/date'

const router = useRouter()
const route = useRoute()
const tournament = ref({})
const registrations = ref([])
const showRegisterDialog = ref(false)
const isRegistered = ref(false)
const myRegistration = ref(null)

// 获取赛事详情
const loadTournament = async () => {
  try {
    const res = await getTournamentById(route.params.id)
    tournament.value = res.data
  } catch (error) {
    showToast('获取赛事信息失败')
    router.back()
  }
}

// 获取报名列表
const loadRegistrations = async () => {
  try {
    const res = await getRegistrations(route.params.id)
    registrations.value = res.data
    // 查找当前用户的报名记录
    myRegistration.value = registrations.value.find(r => r.userId === getCurrentUserId())
    isRegistered.value = myRegistration.value != null
  } catch (error) {
    showToast('获取报名列表失败')
  }
}

// 获取状态样式
const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'default',
    'REGISTERING': 'primary',
    'ONGOING': 'success',
    'FINISHED': 'warning'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'REGISTERING': '报名中',
    'ONGOING': '进行中',
    'FINISHED': '已结束'
  }
  return textMap[status] || status
}

// 获取类型文本
const getTypeText = (type) => {
  const textMap = {
    'SINGLES': '单打',
    'DOUBLES': '双打',
    'TEAM': '团体'
  }
  return textMap[type] || type
}

// 获取级别文本
const getLevelText = (level) => {
  const textMap = {
    'BEGINNER': '新手级',
    'AMATEUR': '业余级',
    'PROFESSIONAL': '专业级'
  }
  return textMap[level] || level
}

// 获取报名状态样式
const getRegistrationStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取报名状态文本
const getRegistrationStatusText = (status) => {
  const textMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return textMap[status] || status
}

// 编辑赛事
const editTournament = () => {
  router.push(`/tournament/edit/${tournament.value.id}`)
}

// 更新状态
const updateStatus = async (status) => {
  try {
    await updateTournamentStatus(tournament.value.id, status)
    showToast('更新成功')
    loadTournament()
  } catch (error) {
    showToast('更新失败')
  }
}

// 报名
const register = () => {
  showRegisterDialog.value = true
}

// 确认报名
const confirmRegister = async () => {
  try {
    await registerApi(tournament.value.id)
    showToast('报名成功')
    loadRegistrations()
  } catch (error) {
    showToast('报名失败')
  }
}

// 取消报名
const cancelRegistration = async () => {
  showDialog({
    title: '确认取消',
    message: '确定要取消报名吗？',
    showCancelButton: true
  }).then(async () => {
    try {
      await cancelRegistrationApi(tournament.value.id)
      showToast('取消成功')
      loadRegistrations()
    } catch (error) {
      showToast('取消失败')
    }
  })
}

// 返回
const onClickLeft = () => {
  router.back()
}

onMounted(() => {
  loadTournament()
  loadRegistrations()
})
</script>

<style scoped>
.tournament-detail {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 100px;
}

.info-card {
  margin: 12px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.info-list {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.info-item .van-icon {
  margin-right: 4px;
  font-size: 16px;
}

.label {
  color: #999;
  margin-right: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  padding-left: 8px;
  border-left: 4px solid var(--van-primary-color);
}

.description {
  margin-top: 16px;
}

.description p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 8px 0;
}

.registration-list {
  margin: 12px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.register-dialog-content {
  padding: 16px;
  text-align: center;
}

.fee-info {
  color: var(--van-danger-color);
  margin-top: 8px;
}
</style> 