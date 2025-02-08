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
            v-model="formData.level"
            name="level"
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
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createTournament, updateTournament } from '@/api/tournament'
import { showToast, showLoadingToast, closeToast } from 'vant'

const router = useRouter()
const route = useRoute()
const isEdit = ref(false)

const formData = ref({
  title: '2025年乒乓球赛事',
  description: `赛事信息：

比赛赛制：三局两胜
参赛人数：60
替补人数：24
分组数量：12
每组出线人数：4
淘汰赛对阵：默认对阵

赛事规程：
1.本次比赛执行国家体育总局最新审定的《乒乓球竞赛规则》及本次赛事特别规定。
2.比赛为11分制，三局二胜。
3.第一阶段为小组赛，第二阶段为淘汰赛（当报名人数不满36人时，当天比赛或将会被取消）。
4.前三名获奖选手必须参加颁奖仪式，如不参加则视为放弃名次和奖励。
5.请参赛运动员对自己的身体健康和言行举止负责，赛事方不对因选手自身问题产生的变故负责。
组委会可根据报名人数适当调整赛制。
6.本次比赛使用蝴蝶starcar。
7.本次比赛成绩录入爱聚网积分系统。
8.年龄超过70岁的球友以及患有心脏病、高血压、心脑血管疾病的球友谢绝参赛。
9.参赛选手当天带好身份证件以备查验，不能出示有效身份证件者按弃权处理。
10.比赛无故弃权且不听劝阻的选手将取消本次比赛的所有名次及奖励；禁止赌博；禁止在公共场所抽烟（厕所及走道内）。
11.如发现有违体育道德的行为，可向组织方申诉，不得争吵、乃至损坏赛场设施、组织方有权根据现场情况对争议双方采取必要的合理处置。
12.参加比赛者视为自愿参加，请参赛者斟酌自己的身体状况。

奖品：
第一名 1200元+1瓶价值￥588的赛湖之梦醇柔3号+证书+流动奖杯
第二名 800元+1瓶价值￥388的赛湖之梦醇柔2号+证书
第三名 500元+1瓶价值￥288的赛湖之梦醇柔1号+证书
第四名 300元+1瓶价值￥199的青瓷赛里木第一坛+证书

若报名参赛人数超过48人，则
增加并列第五名150元+证书

增加小组全胜奖：
免下一场参赛报名费（可抵扣￥60）

增加整场全胜奖：
￥300 + 追加一份冠军奖品（醇柔3号）`,
  startTime: '2025-09-01 12:00:00',
  location: '11F',
  maxPlayers: '32',
  type: 'SINGLES',
  level: '1800',
  entryFee: '30'
})

const onClickLeft = () => {
  router.back()
}

const onSubmit = async () => {
  try {
    showLoadingToast({
      message: isEdit.value ? '保存中...' : '创建中...',
      forbidClick: true
    })

    const submitData = {
      ...formData.value,
      maxPlayers: parseInt(formData.value.maxPlayers),
      level: parseInt(formData.value.level),
      entryFee: parseFloat(formData.value.entryFee)
    }

    if (isEdit.value) {
      await updateTournament(route.params.id, submitData)
    } else {
      await createTournament(submitData)
    }

    closeToast()
    showToast({
      type: 'success',
      message: isEdit.value ? '保存成功' : '创建成功'
    })
    
    // 使用replace而不是push，这样可以防止用户点击返回按钮时回到表单页
    router.replace('/tournament')
  } catch (error) {
    console.error('提交表单失败:', error)
    closeToast()
    showToast({
      type: 'fail',
      message: error.message || '操作失败，请重试'
    })
  }
}
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