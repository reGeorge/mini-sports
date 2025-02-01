<template>
  <tabbar-layout>
    <div class="points-search">
      <van-nav-bar title="积分查询" />
      
      <!-- 搜索框 -->
      <div class="search-box">
        <van-search
          v-model="searchValue"
          placeholder="请输入姓名搜索"
          @search="onSearch"
          shape="round"
          show-action
          @cancel="onCancel"
        />
      </div>

      <!-- 搜索结果列表 -->
      <div class="search-results" v-if="searchResults.length > 0">
        <div class="result-item" v-for="item in searchResults" :key="item.id" @click="showDetail(item)">
          <div class="item-header">
            <span class="rank-number">{{ item.rank }}</span>
            <span class="player-name">{{ item.nickname }}</span>
            <span class="player-gender">{{ item.gender }}</span>
          </div>
          <div class="item-body">
            <div class="points-info">
              <span class="points">{{ item.points }}</span>
              <span class="level">{{ item.level }}</span>
            </div>
            <div class="rank-info">
              <span class="total-rank">总排名: {{ item.totalRank }}</span>
              <span class="match-count">场次: {{ item.matchCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 无搜索结果提示 -->
      <div class="no-result" v-else-if="hasSearched">
        <van-empty description="未找到相关用户" />
      </div>

      <!-- 用户详情弹窗 -->
      <van-popup v-model:show="showDetailPopup" round position="bottom" :style="{ height: '70%' }">
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
              <span class="label">性别</span>
              <span class="value">{{ selectedUser.gender }}</span>
            </div>
            <div class="info-row">
              <span class="label">当前积分</span>
              <span class="value">{{ selectedUser.points }}</span>
            </div>
            <div class="info-row">
              <span class="label">握拍方式</span>
              <span class="value">{{ selectedUser.gripStyle }}</span>
            </div>
            <div class="info-row">
              <span class="label">球拍配置</span>
              <span class="value">{{ selectedUser.racketConfig }}</span>
            </div>
            <div class="info-row">
              <span class="label">水平级别</span>
              <span class="value">{{ selectedUser.level }}</span>
            </div>
            <div class="info-row">
              <span class="label">参赛场次</span>
              <span class="value">{{ selectedUser.matchCount }}</span>
            </div>
            <div class="info-row">
              <span class="label">全网排名</span>
              <span class="value">{{ selectedUser.totalRank }}</span>
            </div>
            <div class="info-row">
              <span class="label">胜率</span>
              <span class="value">{{ selectedUser.winRate }}%</span>
            </div>
            <div class="info-row">
              <span class="label">历史最高积分</span>
              <span class="value">{{ selectedUser.highestPoints }}</span>
            </div>
            <div class="info-row">
              <span class="label">年度平均积分</span>
              <span class="value">{{ selectedUser.yearlyAveragePoints }}</span>
            </div>
          </div>
        </div>
      </van-popup>
    </div>
  </tabbar-layout>
</template>

<script>
import { ref } from 'vue'
import { searchUserPoints } from '@/api/points'
import { showToast } from 'vant'
import TabbarLayout from '@/components/layout/TabbarLayout.vue'

export default {
  name: 'PointsSearch',
  components: {
    TabbarLayout
  },
  setup() {
    const searchValue = ref('')
    const searchResults = ref([])
    const hasSearched = ref(false)
    const showDetailPopup = ref(false)
    const selectedUser = ref(null)

    const onSearch = async () => {
      if (!searchValue.value.trim()) {
        showToast('请输入搜索内容')
        return
      }

      try {
        const res = await searchUserPoints(searchValue.value)
        searchResults.value = res.data
        hasSearched.value = true
      } catch (error) {
        showToast('搜索失败')
      }
    }

    const onCancel = () => {
      searchValue.value = ''
      searchResults.value = []
      hasSearched.value = false
    }

    const showDetail = (user) => {
      selectedUser.value = user
      showDetailPopup.value = true
    }

    return {
      searchValue,
      searchResults,
      hasSearched,
      showDetailPopup,
      selectedUser,
      onSearch,
      onCancel,
      showDetail
    }
  }
}
</script>

<style scoped>
.points-search {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.search-box {
  padding: 8px 0;
  background: #fff;
}

.search-results {
  padding: 12px;
}

.result-item {
  background: white;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.item-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.rank-number {
  font-size: 16px;
  font-weight: bold;
  margin-right: 12px;
}

.player-name {
  font-size: 16px;
  font-weight: bold;
  flex: 1;
}

.player-gender {
  color: #666;
  margin-left: 8px;
}

.item-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points-info {
  display: flex;
  align-items: center;
}

.points {
  font-size: 18px;
  color: #ff6b01;
  font-weight: bold;
  margin-right: 8px;
}

.level {
  color: #666;
}

.rank-info {
  color: #666;
  font-size: 14px;
}

.total-rank {
  margin-right: 12px;
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

.no-result {
  margin-top: 60px;
}
</style> 