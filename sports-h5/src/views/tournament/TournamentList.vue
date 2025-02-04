<template>
  <tabbar-layout>
    <div class="tournament-list">
      <van-nav-bar title="赛事" />
      
      <!-- 搜索和筛选区 -->
      <div class="search-box">
        <van-search
          v-model="searchValue"
          placeholder="搜索赛事"
          @search="onSearch"
          shape="round"
        />
        <van-dropdown-menu>
          <van-dropdown-item v-model="statusFilter" :options="statusOptions" />
          <van-dropdown-item v-model="typeFilter" :options="typeOptions" />
        </van-dropdown-menu>
      </div>

      <!-- 赛事列表 -->
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div class="tournament-items">
            <van-card
              v-for="tournament in tournaments"
              :key="tournament.id"
              :title="tournament.title"
              :desc="tournament.description"
              :thumb="'/images/tournament-default.png'"
              :thumb-style="{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '4px' }"
            >
              <template #thumb>
                <img :src="`/images/avatar/tournament2.png`" class="tournament-thumb" />
              </template>
              <template #tags>
                <van-tag :type="getStatusType(tournament.status)" class="status-tag">
                  {{ getStatusText(tournament.status) }}
                </van-tag>
                <van-tag type="primary" class="type-tag">{{ getTypeText(tournament.type) }}</van-tag>
              </template>
              <template #bottom>
                <div class="tournament-info">
                  <div class="info-item">
                    <van-icon name="clock-o" />
                    <span>{{ formatDate(tournament.startTime) }}</span>
                  </div>
                  <div class="info-item">
                    <van-icon name="location-o" />
                    <span>{{ tournament.location }}</span>
                  </div>
                  <div class="info-item">
                    <van-icon name="friends-o" />
                    <span>{{ tournament.currentPlayers }}/{{ tournament.maxPlayers }}</span>
                  </div>
                </div>
              </template>
              <template #footer>
                <div class="card-footer">
                  <van-button 
                    size="small" 
                    type="primary"
                    @click="viewTournament(tournament)"
                  >
                    查看详情
                  </van-button>
                  <template v-if="hasPermission('tournament:edit') && tournament.status === 'DRAFT'">
                    <van-button 
                      size="small" 
                      type="info" 
                      @click="editTournament(tournament)"
                    >
                      编辑
                    </van-button>
                    <van-button 
                      size="small" 
                      type="danger" 
                      @click="deleteTournament(tournament)"
                    >
                      删除
                    </van-button>
                  </template>
                </div>
              </template>
            </van-card>
          </div>
        </van-list>
      </van-pull-refresh>

      <!-- 创建赛事按钮 -->
      <van-floating-bubble 
        v-if="hasPermission('tournament:create')"
        axis="xy" 
        magnetic="x"
        @click="createTournament"
        class="create-button"
      >
        <van-icon name="plus" size="24" />
      </van-floating-bubble>
    </div>
  </tabbar-layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getTournaments, deleteTournament as deleteTournamentApi } from '@/api/tournament'
import { hasPermission } from '@/utils/permission'
import { formatDate } from '@/utils/date'
import TabbarLayout from '@/components/layout/TabbarLayout.vue'

const router = useRouter()
const tournaments = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const searchValue = ref('')
const statusFilter = ref('all')
const typeFilter = ref('all')
const page = ref(1)
const pageSize = 10

// 状态选项
const statusOptions = [
  { text: '全部状态', value: 'all' },
  { text: '草稿', value: 'DRAFT' },
  { text: '报名中', value: 'REGISTERING' },
  { text: '进行中', value: 'ONGOING' },
  { text: '已结束', value: 'FINISHED' }
]

// 比赛类型选项
const typeOptions = [
  { text: '全部类型', value: 'all' },
  { text: '单打', value: 'SINGLES' },
  { text: '双打', value: 'DOUBLES' },
  { text: '团体', value: 'TEAM' }
]

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

// 获取随机数
const getRandomNumber = (min, max) => {
  return Math.floor(Math.random() * (max - min + 1)) + min
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

// 加载赛事列表
const loadTournaments = async () => {
  if (loading.value) return; // 防止重复加载
  
  try {
    loading.value = true;
    const params = {
      page: page.value,
      pageSize,
      keyword: searchValue.value,
      status: statusFilter.value !== 'all' ? statusFilter.value : undefined,
      type: typeFilter.value !== 'all' ? typeFilter.value : undefined
    }
    
    const res = await getTournaments(params)
    const { list, total } = res.data
    
    if (refreshing.value) {
      tournaments.value = []
      refreshing.value = false
    }
    
    tournaments.value.push(...list)
    
    if (tournaments.value.length >= total) {
      finished.value = true
    } else {
      page.value++
    }
  } catch (error) {
    showToast('获取赛事列表失败')
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  finished.value = false
  page.value = 1
  loadTournaments()
}

// 上拉加载
const onLoad = () => {
  if (!loading.value && !finished.value) {
    loadTournaments()
  }
}

// 搜索
const onSearch = () => {
  tournaments.value = []
  finished.value = false
  page.value = 1
  loadTournaments()
}

// 监听筛选条件变化
watch([statusFilter, typeFilter], () => {
  tournaments.value = []
  finished.value = false
  page.value = 1
  loadTournaments()
})

// 查看赛事详情
const viewTournament = (tournament) => {
  router.push(`/tournament/detail/${tournament.id}`)
}

// 创建赛事
const createTournament = () => {
  router.push('/tournament/create')
}

// 编辑赛事
const editTournament = (tournament) => {
  router.push(`/tournament/edit/${tournament.id}`)
}

// 删除赛事
const deleteTournament = (tournament) => {
  showDialog({
    title: '确认删除',
    message: `是否确认删除赛事"${tournament.title}"？`,
    showCancelButton: true,
  }).then(async () => {
    try {
      await deleteTournamentApi(tournament.id)
      showToast('删除成功')
      // 刷新列表
      onRefresh()
    } catch (error) {
      showToast('删除失败')
    }
  })
}

onMounted(() => {
  loadTournaments()
})
</script>

<style scoped>
.tournament-list {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 100px;
}

.search-box {
  position: sticky;
  top: 46px;
  z-index: 98;
  background-color: #fff;
}

.tournament-items {
  padding: 12px;
  margin-bottom: 60px;
}

.van-card {
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 8px;
}

.status-tag {
  margin-right: 4px;
}

.type-tag {
  margin-right: 4px;
}

.tournament-info {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.create-button {
  --van-floating-bubble-size: 48px;
  --van-floating-bubble-background: var(--van-primary-color);
  --van-floating-bubble-color: #fff;
  --van-floating-bubble-z-index: 999;
  right: 16px;
  bottom: 80px;
}

.create-button :deep(.van-icon) {
  font-size: 20px;
}

:deep(.van-nav-bar) {
  position: sticky;
  top: 0;
  z-index: 99;
}

:deep(.van-dropdown-menu) {
  position: sticky;
  top: 100px;
  z-index: 98;
}

.tournament-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}
</style> 