<template>
  <div class="user-detail">
    <van-nav-bar
      title="查看资料"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <div class="form-content">
      <!-- 添加头像显示 -->
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
        >
          <template #label>
            <span class="required-label">姓名</span>
          </template>
        </van-field>
        <van-field
          v-model="formData.phone"
          label="手机"
          placeholder="请输入手机号"
          readonly
        >
          <template #label>
            <span class="required-label">手机</span>
          </template>
        </van-field>
      </van-cell-group>

      <!-- 球拍信息 -->
      <van-cell-group inset class="grip-info">
        <van-field
          v-model="formData.gripStyle"
          label="握拍方式"
          readonly
        >
          <template #label>
            <span class="required-label">握拍方式</span>
          </template>
        </van-field>

        <van-field
          v-model="formData.racketConfig"
          label="球拍配置"
          readonly
        >
          <template #label>
            <span class="required-label">球拍配置</span>
          </template>
        </van-field>
      </van-cell-group>

      <!-- 编辑按钮 -->
      <div class="button-group">
        <van-button type="primary" block @click="handleEdit">编辑资料</van-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

export default {
  name: 'UserDetail',
  setup() {
    const router = useRouter()
    const formData = ref({
      avatarUrl: '',
      nickname: '',
      phone: '',
      gripStyle: '',
      racketConfig: ''
    })

    // 从本地存储获取用户信息
    const loadUserInfo = () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      formData.value = {
        ...formData.value,
        ...userInfo
      }
    }

    onMounted(() => {
      loadUserInfo()
    })

    const onClickLeft = () => {
      router.back()
    }

    const handleEdit = () => {
      router.push('/user/edit')
    }

    // 监听路由变化，每次进入页面时重新加载用户信息
    router.beforeEach((to, from, next) => {
      if (to.path === '/user/detail') {
        loadUserInfo()
      }
      next()
    })

    return {
      formData,
      onClickLeft,
      handleEdit
    }
  }
}
</script>

<style scoped>
.user-detail {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 20px;
}

.form-content {
  padding: 20px;
}

.grip-info {
  margin-top: 12px;
}

.button-group {
  margin-top: 20px;
  padding: 0 16px;
}

.required-label::before {
  content: '*';
  color: #ee0a24;
  margin-right: 4px;
}

:deep(.van-cell-group) {
  margin: 0;
}

:deep(.van-field__label) {
  width: 80px;
}

.avatar-wrapper {
  text-align: center;
  margin-bottom: 20px;
}
</style> 