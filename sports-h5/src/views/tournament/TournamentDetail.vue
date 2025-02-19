<template>
  <div class="tournament-detail">
    <van-nav-bar
      title="赛事详情"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />

    <!-- 标签页导航 -->
    <van-tabs  v-model:active="activeTab" sticky>
      <van-tab title="详情" name="details">
        <!-- 基本信息卡片 -->
        <div class="description-card">
          <!-- 赛事标题和状态 -->
          <div class="header-card">
            <div class="header">
              <div class="title">{{ tournament.title }}</div>
              <van-tag :type="getStatusType(tournament.status)" size="medium">
                {{ getStatusText(tournament.status) }}
              </van-tag>
            </div>
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
              <span class="label">积分上限：</span>
              <span>{{ tournament.level === '0' ? '无限制' : tournament.level }}</span>
            </div>
            <div class="info-item">
              <van-icon name="gold-coin-o" />
              <span class="label">报名费用：</span>
              <span>{{ tournament.entryFee }}元</span>
            </div>
          </div>
        </div>

        <!-- 赛事说明卡片 -->
        <div class="description-card">
          <h3>赛事说明</h3>
          <p class="description-text">{{ tournament.description }}</p>
        </div>

        <!-- 操作按钮 -->
        <div class="action-bar">
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
              @click="handleStartTournament"
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

          <!-- 编辑按钮 -->
          <template v-if="hasPermission('tournament:edit') && tournament.status === 'DRAFT'">
            <van-button 
              type="default" 
              block 
              @click="editTournament"
            >
              编辑赛事
            </van-button>
          </template>

          <!-- 删除按钮 -->
          <template v-if="hasPermission('tournament:delete') && tournament.status === 'DRAFT'">
            <van-button 
              type="danger" 
              block 
              @click="deleteTournament"
            >
              删除赛事
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
            v-if="tournament.status === 'REGISTERING' && isRegistered"
            type="danger" 
            block 
            @click="cancelRegistration"
          >
            取消报名
          </van-button>
        </div>
      </van-tab>

      <van-tab title="参赛名单" name="registrations">
        <div class="registrations-card">
          <template v-if="sortedRegistrations.length > 0">
            <div class="registration-list">
              <div v-for="(registration, index) in sortedRegistrations" :key="registration.id" class="registration-item">
                <div class="user-info" @click="showUserDetail(registration.user)">
                  <div class="index-circle">{{ index + 1 }}</div>
                  <span class="name">{{ registration.user?.nickname }}</span>
                </div>
                <van-tag >{{ registration.user?.points || 0 }}分</van-tag>
                <van-tag :type="getRegistrationStatusType(registration.status)">
                  {{ getRegistrationStatusText(registration.status) }}
                </van-tag>
              </div>
            </div>
          </template>
          <van-empty v-else description="暂无报名信息" />
        </div>
      </van-tab>

      <van-tab title="赛程" name="schedule">
        <div class="schedule-card">
          <tournament-schedule :tournament-id="route.params.id" />
        </div>
      </van-tab>
    </van-tabs>

    <!-- 用户详情弹窗 -->
    <van-popup v-model:show="showDetailPopup" round position="bottom" :style="{ height: '80%' }">
      <div class="detail-popup" v-if="selectedUser">
        <div class="popup-header">
          <div class="header-title">详细信息</div>
          <van-icon name="cross" @click="showDetailPopup = false" />
        </div>
        <div class="user-info">
          <div class="info-row">
            <span class="label">ID</span>
            <span class="value">{{ selectedUser.id }}</span>
          </div>
          <div class="info-row">
            <span class="label">昵称</span>
            <span class="value">{{ selectedUser.nickname }}</span>
          </div>
          <div class="info-row">
            <span class="label">握拍方式</span>
            <span class="value">{{ selectedUser.gripStyle || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="label">球拍配置</span>
            <span class="value">{{ selectedUser.racketConfig || '未设置' }}</span>
          </div>
          <div class="info-row">
            <span class="label">当前积分</span>
            <span class="value">{{ selectedUser.points || 0 }}</span>
          </div>
          <div class="info-row">
            <span class="label">水平级别</span>
            <span class="value">{{ selectedUser.level || 'BEGINNER' }}</span>
          </div>
          <div class="info-row">
            <span class="label">参赛场次</span>
            <span class="value">{{ selectedUser.matchCount || 0 }}</span>
          </div>
          <div class="info-row">
            <span class="label">全网排名</span>
            <span class="value">{{ selectedUser.totalRank || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">胜率</span>
            <span class="value">{{ selectedUser.winRate ? selectedUser.winRate + '%' : '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">历史最高积分</span>
            <span class="value">{{ selectedUser.highestPoints || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">年度平均积分</span>
            <span class="value">{{ selectedUser.yearlyAveragePoints || '-' }}</span>
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 报名确认弹窗 -->
    <van-dialog
      v-model:show="showRegisterDialog"
      title="报名确认"
      show-cancel-button
      @confirm="confirmRegister"
    >
      <div class="register-dialog-content">
        <p>{{ tournament.currentPlayers >= tournament.maxPlayers ? '当前报名人数已满，您将进入候补名单，是否继续？' : '确认报名参加该赛事吗？' }}</p>
        <p class="fee-info" v-if="tournament.entryFee > 0">
          需支付报名费：{{ tournament.entryFee }}元
        </p>
      </div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getTournamentById, updateTournamentStatus, deleteTournament as deleteTournamentApi, startTournament } from '@/api/tournament'
import { getRegistrations, register as registerApi, cancelRegistration as cancelRegistrationApi } from '@/api/registration'
import { hasPermission } from '@/utils/permission'
import { formatDate, getDateRange } from '@/utils/date'
import TournamentSchedule from '@/components/tournament/TournamentSchedule.vue'

const router = useRouter()
const route = useRoute()
const tournament = ref({})
const registrations = ref([])
const showRegisterDialog = ref(false)
const isRegistered = ref(false)
const myRegistration = ref(null)
const showDetailPopup = ref(false)
const selectedUser = ref(null)
const activeTab = ref('details')
const matchRecords = ref([])

// 获取当前用户ID
const getCurrentUserId = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return userInfo.id
}

// 获取赛事详情
const loadTournament = async () => {
  try {
    const res = await getTournamentById(route.params.id)
    tournament.value = res.data
    await loadRegistrations() // 加载报名列表
  } catch (error) {
    showToast('获取赛事信息失败')
    router.back()
  }
}

// 获取报名列表 参赛名单
const loadRegistrations = async () => {
  try {
    const res = await getRegistrations(route.params.id)
    registrations.value = res.data
    const userId = getCurrentUserId()
    // 查找当前用户的报名记录
    if (userId) {
      myRegistration.value = registrations.value.find(r => r.userId === userId)
      isRegistered.value = !!myRegistration.value
    }
  } catch (error) {
    console.error('获取参赛名单失败:', error)
    showToast('获取参赛名单失败')
  }
}

// 返回上一页
const onClickLeft = () => {
  router.back()
}

// 更新赛事状态
const updateStatus = async (status) => {
  try {
    const statusTextMap = {
      'REGISTERING': '开始报名',
      'ONGOING': '开始比赛',
      'FINISHED': '结束比赛'
    }
    
    await showDialog({
      title: '确认操作',
      message: `确定要${statusTextMap[status]}吗？`,
      showCancelButton: true,
    })
    
    await updateTournamentStatus(tournament.value.id, status)
    showToast('状态更新成功')
    await loadTournament()
  } catch (error) {
    if (error === 'cancel') return
    showToast('状态更新失败')
  }
}
// 开始比赛
const handleStartTournament = async () => {
  try {
    await showDialog({
      title: '确认开始比赛',
      message: '确定要开始比赛吗？开始后将自动生成对阵表。',
      showCancelButton: true,
    })
    
    await startTournament(tournament.value.id)
    showToast('比赛已开始')
    await loadTournament()
  } catch (error) {
    if (error === 'cancel') return
    showToast('开始比赛失败')
  }
}

// 报名
const register = async () => {
  const message = tournament.value.currentPlayers >= tournament.value.maxPlayers
    ? '当前报名人数已满，您将进入候补名单，是否继续？'
    : '确认报名参加该赛事吗？'
    
  if (tournament.value.entryFee > 0) {
    showRegisterDialog.value = true
  } else {
    try {
      await showDialog({
        title: '报名确认',
        message: message,
        showCancelButton: true,
      })
      await confirmRegister()
    } catch (error) {
      if (error === 'cancel') return
    }
  }
}

// 确认报名
const confirmRegister = async () => {
  try {
    await registerApi(tournament.value.id)
    showToast('报名成功')
    await loadRegistrations()
  } catch (error) {
    showToast('报名失败')
  }
}

// 取消报名
const cancelRegistration = async () => {
  try {
    await showDialog({
      title: '确认取消',
      message: '确定要取消报名吗？取消后如需参加需要重新报名。',
      showCancelButton: true,
    })
    
    await cancelRegistrationApi(tournament.value.id)
    showToast('取消报名成功')
    await loadRegistrations()
  } catch (error) {
    if (error === 'cancel') return
    showToast('取消报名失败')
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
  return level === '0' ? '无限制' : `${level}分`
}

// 获取报名状态文本
const getRegistrationStatusText = (status) => {
  const textMap = {
    'PENDING': '已报名',
    'APPROVED': '已报名',
    'REJECTED': '已拒绝',
    'WAITLIST': '候补中'
  }
  return textMap[status] || status
}

// 获取报名状态样式
const getRegistrationStatusType = (status) => {
  const typeMap = {
    'PENDING': 'success',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'WAITLIST': 'warning'
  }
  return typeMap[status] || 'default'
}

// 编辑赛事
const editTournament = async () => {
  try {
    await showDialog({
      title: '确认编辑',
      message: '确定要编辑该赛事吗？',
      showCancelButton: true,
    })
    
    router.push(`/tournament/edit/${tournament.value.id}`)
  } catch (error) {
    if (error === 'cancel') return
  }
}

// 按积分降序排序的报名列表
const sortedRegistrations = computed(() => {
  return [...registrations.value].sort((a, b) => {
    return (b.user?.points || 0) - (a.user?.points || 0)
  })
})

// 删除赛事
const deleteTournament = async () => {
  try {
    await showDialog({
      title: '确认删除',
      message: `确定要删除赛事"${tournament.value.title}"吗？删除后无法恢复。`,
      showCancelButton: true,
    })
    
    await deleteTournamentApi(tournament.value.id)
    showToast('删除成功')
    router.back()
  } catch (error) {
    if (error === 'cancel') return
    showToast('删除失败')
  }
}

// 显示用户详情
const showUserDetail = (user) => {
  selectedUser.value = user
  showDetailPopup.value = true
}

// 组件挂载时加载数据
onMounted(async () => {
  await loadTournament()
})
</script>

<style lang="scss" scoped>
.tournament-detail {
  min-height: 100vh;
  background-color: #f7f8fa;
  display: flex;
  flex-direction: column;
  padding-top: 46px;
}

.header-card {
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  position: relative;
  z-index: 2;
}

:deep(.van-nav-bar) {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99;
}

.info-card {
  margin-top: 46px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
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
  line-height: 1.6;
  margin: 8px 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  padding: 0 12px;
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
  white-space: pre-wrap;
  word-wrap: break-word;
  padding: 0 12px;
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

.description-card {
  margin: 16px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  
}

.description-card h3 {
  margin-bottom: 10px;
  font-size: 16px;
  color: #333;
}

.description-text {
  margin-bottom: 60%;
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 99;
}

.register-dialog-content {
  padding: 16px;
  text-align: center;
}

.fee-info {
  color: var(--van-danger-color);
  margin-top: 8px;
}

.registration-number {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  background-color: #f2f2f2;
  border-radius: 12px;
  margin-right: 8px;
  font-size: 14px;
  color: #666;
}

.user-name {
  margin-right: 8px;
  font-weight: bold;
}

.points-tag {
  font-size: 12px;
  font-weight: normal;
  vertical-align: middle;
}

.action-bar :deep(.van-button--default) {
  background-color: #f5f5f5;
  border: 1px solid #dcdee0;
}

.detail-popup {
  padding: 20px;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.user-info {
  overflow-y: auto;
  max-height: calc(70vh - 60px);
}

.info-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row .label {
  width: 100px;
  color: #666;
}

.info-row .value {
  flex: 1;
  color: #333;
}

:deep(.van-collapse-item__title) {
  padding-top: 0;
}

:deep(.van-collapse-item__content) {
  padding: 0;
}

:deep(.van-cell) {
  align-items: center;
}
.registration-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background-color: #fff;
  border: 1px solid #ebedf0;
  border-radius: 8px;
}

.registration-item .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.registration-item .name {
  font-size: 14px;
  font-weight: 500;
  color: #323233;
}

.registration-item :deep(.van-tag) {
  margin-left: 8px;
}

.registration-item {
  .user-info {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;

    .index-circle {
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
  }
}
</style>