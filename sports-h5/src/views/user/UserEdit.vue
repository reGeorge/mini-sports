<template>
  <div class="user-edit">
    <van-nav-bar
      title="编辑资料"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
      right-text="保存"
      @click-right="handleSave"
    />
    
    <div class="form-content">
      <!-- 头像 -->
      <div class="avatar-wrapper">
        <van-image
          round
          width="80"
          height="80"
          :src="formData.avatarUrl || '/images/avatar/fzd.png'"
        />
      </div>

      <!-- 基本信息 -->
      <van-cell-group inset>
        <van-field
          v-model="formData.nickname"
          label="姓名"
          placeholder="请输入姓名"
          :rules="[{ required: true, message: '请填写姓名' }]"
          readonly
        />
        <van-field
          v-model="formData.phone"
          label="手机"
          placeholder="请输入手机号"
          readonly
        />
      </van-cell-group>

      <!-- 球拍信息 -->
      <div class="form-section">
        <div class="section-title">握拍方式</div>
        <van-radio-group v-model="formData.gripStyle">
          <div class="radio-grid">
            <van-radio name="右手直拍">右手直拍</van-radio>
            <van-radio name="右手横拍">右手横拍</van-radio>
            <van-radio name="左手直拍">左手直拍</van-radio>
            <van-radio name="左手横拍">左手横拍</van-radio>
          </div>
        </van-radio-group>

        <div class="section-title">球拍配置</div>
        <van-radio-group v-model="formData.racketConfig">
          <div class="radio-list">
            <van-radio name="双面反胶">双面反胶</van-radio>
            <van-radio name="双面颗粒胶">双面颗粒胶</van-radio>
            <van-radio name="一面反胶一面颗粒胶">一面反胶一面颗粒胶</van-radio>
            <van-radio name="直拍单面反胶">直拍单面反胶</van-radio>
            <van-radio name="直拍单面颗粒胶">直拍单面颗粒胶</van-radio>
          </div>
        </van-radio-group>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import { updateUser } from '@/api/user'

export default {
  name: 'UserEdit',
  setup() {
    const router = useRouter()
    const formData = ref({
      avatarUrl: '',
      nickname: '',
      phone: '',
      gripStyle: '',
      racketConfig: ''
    })

    onMounted(() => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      formData.value = {
        ...formData.value,
        ...userInfo
      }
    })

    const onClickLeft = () => {
      router.back()
    }

    const handleSave = async () => {
      try {
        const res = await updateUser(formData.value)
        // 更新本地存储的用户信息
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        showSuccessToast('保存成功')
        router.back()
      } catch (error) {
        showFailToast(error.response?.data?.message || '保存失败')
      }
    }

    return {
      formData,
      onClickLeft,
      handleSave
    }
  }
}
</script>

<style scoped>
.user-edit {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 20px;
}

.form-content {
  padding: 20px;
}

.avatar-wrapper {
  text-align: center;
  margin-bottom: 20px;
}

.form-section {
  margin-top: 12px;
  padding: 16px;
  background: white;
  border-radius: 12px;
}

.section-title {
  font-size: 14px;
  color: #969799;
  margin: 16px 0 12px;
  padding-left: 4px;
}

.section-title:first-child {
  margin-top: 0;
}

.radio-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.radio-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

:deep(.van-radio) {
  margin: 0;
  padding: 14px;
  background-color: #f7f8fa;
  border-radius: 8px;
  width: 100%;
  display: flex;
  align-items: center;
  min-height: 24px;
}

:deep(.van-radio__label) {
  color: #323233;
  margin-left: 8px;
  flex: 1;
  font-size: 14px;
  line-height: 20px;
}

:deep(.van-radio__icon) {
  flex: none;
  height: 20px;
  font-size: 20px;
  line-height: 1;
}

:deep(.van-radio__icon .van-icon) {
  border: 1px solid #c8c9cc;
  width: 20px;
  height: 20px;
  border-radius: 100%;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.van-radio__icon--checked .van-icon) {
  background-color: #1989fa;
  border-color: #1989fa;
  color: #fff;
}
</style> 