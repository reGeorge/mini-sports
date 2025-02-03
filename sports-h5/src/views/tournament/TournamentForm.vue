<template>
  <div class="tournament-form">
    <van-nav-bar
      :title="isEdit ? '编辑赛事' : '创建赛事'"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <van-form @submit="onSubmit">
      <!-- 基本信息 -->
      <div class="form-section">
        <div class="section-title">基本信息</div>
        <van-cell-group inset>
          <van-field
            v-model="formData.title"
            name="title"
            label="赛事名称"
            placeholder="请输入赛事名称"
            :rules="[{ required: true, message: '请输入赛事名称' }]"
          />
          
          <van-field
            v-model="formData.description"
            name="description"
            label="赛事说明"
            type="textarea"
            rows="3"
            placeholder="请输入赛事说明"
            :rules="[{ required: true, message: '请输入赛事说明' }]"
          />
          
          <van-field
            v-model="formData.startTime"
            name="startTime"
            label="开始时间"
            type="datetime-local"
            placeholder="请选择开始时间"
            :rules="[{ required: true, message: '请选择开始时间' }]"
          />
          
          <van-field
            v-model="formData.location"
            name="location"
            label="比赛地点"
            placeholder="请输入比赛地点"
            :rules="[{ required: true, message: '请输入比赛地点' }]"
          />
        </van-cell-group>
      </div>
      
      <!-- 比赛设置 -->
      <div class="form-section">
        <div class="section-title">比赛设置</div>
        <van-cell-group inset>
          <van-field
            v-model="formData.maxPlayers"
            name="maxPlayers"
            label="最大人数"
            type="digit"
            placeholder="请输入最大参与人数"
            :rules="[
              { required: true, message: '请输入最大参与人数' },
              { pattern: /^[1-9]\d*$/, message: '请输入正整数' }
            ]"
          />
          
          <van-field
            name="type"
            label="比赛类型"
            :rules="[{ required: true, message: '请选择比赛类型' }]"
          >
            <template #input>
              <van-radio-group v-model="formData.type" direction="horizontal">
                <van-radio name="SINGLES">单打</van-radio>
                <van-radio name="DOUBLES">双打</van-radio>
                <van-radio name="TEAM">团体</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          
          <van-field
            v-model="formData.maxPoints"
            name="maxPoints"
            label="积分上限"
            type="digit"
            placeholder="请输入参赛积分上限（0表示无限制）"
            :rules="[
              { required: true, message: '请输入积分上限' },
              { pattern: /^[0-9]\d*$/, message: '请输入非负整数' }
            ]"
          />
          
          <van-field
            v-model="formData.entryFee"
            name="entryFee"
            label="报名费用"
            type="digit"
            placeholder="请输入报名费用（元）"
            :rules="[
              { required: true, message: '请输入报名费用' },
              { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的金额' }
            ]"
          />
        </van-cell-group>
      </div>
      
      <!-- 提交按钮 -->
      <div class="submit-bar">
        <van-button round block type="primary" native-type="submit">
          {{ isEdit ? '保存修改' : '创建赛事' }}
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { createTournament, getTournamentById, updateTournament } from '@/api/tournament'
import { formatDate } from '@/utils/date'

const router = useRouter()
const route = useRoute()
const isEdit = computed(() => !!route.params.id)

// 获取下一个整点时间
const getNextHourTime = () => {
  const now = new Date()
  const nextHour = new Date(now)
  nextHour.setHours(now.getHours() + 1, 0, 0, 0)
  return nextHour.toISOString().slice(0, 16) // 格式：YYYY-MM-DDTHH:mm
}

// 表单数据
const formData = ref({
  title: '',
  description: '',
  startTime: getNextHourTime(),
  location: '',
  maxPlayers: '16',
  type: 'SINGLES',
  maxPoints: '0',
  entryFee: '30'
})

// 返回
const onClickLeft = () => {
  router.back()
}

// 提交表单
const onSubmit = async (values) => {
  try {
    // 转换时间格式为时间戳
    const startTime = new Date(values.startTime).getTime()
    
    // 确保数字字段为数字类型
    const submitData = {
      ...values,
      startTime,
      maxPlayers: parseInt(values.maxPlayers),
      maxPoints: parseInt(values.maxPoints),
      entryFee: parseFloat(values.entryFee)
    }
    
    // 添加调试日志
    console.log('提交的表单数据:', values)
    console.log('处理后的提交数据:', submitData)
    
    if (isEdit.value) {
      console.log('正在更新赛事，ID:', route.params.id)
      const response = await updateTournament(route.params.id, submitData)
      console.log('更新赛事响应:', response)
      showToast('修改成功')
    } else {
      console.log('正在创建新赛事')
      const response = await createTournament(submitData)
      console.log('创建赛事响应:', response)
      showToast('创建成功')
    }
    router.back()
  } catch (error) {
    console.error('提交失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    showToast(error.message || (isEdit.value ? '修改失败' : '创建失败'))
  }
}

// 加载赛事详情
const loadTournament = async () => {
  if (!isEdit.value) return
  
  try {
    const res = await getTournamentById(route.params.id)
    const tournament = res.data
    
    // 转换时间戳为datetime-local格式
    const formatDateTime = (timestamp) => {
      const date = new Date(timestamp)
      return date.toISOString().slice(0, 16) // 格式：YYYY-MM-DDTHH:mm
    }
    
    formData.value = {
      ...tournament,
      startTime: formatDateTime(tournament.startTime)
    }
  } catch (error) {
    showToast('获取赛事信息失败')
    router.back()
  }
}

onMounted(() => {
  loadTournament()
})
</script>

<style scoped>
.tournament-form {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 80px;
}

.form-section {
  margin: 12px 0;
}

.section-title {
  font-size: 14px;
  color: #969799;
  padding: 0 16px;
  margin: 8px 0;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.van-picker-column) {
  font-size: 16px;
}

:deep(.van-picker__title) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.van-picker__confirm, .van-picker__cancel) {
  font-size: 14px;
}

:deep(.van-field__label) {
  width: 6em;
}
</style> 