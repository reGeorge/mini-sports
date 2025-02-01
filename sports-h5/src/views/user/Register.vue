<template>
  <div class="register">
    <van-nav-bar
      title="用户注册"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="formData.nickname"
          name="nickname"
          label="昵称"
          placeholder="请输入昵称"
          :rules="[{ required: true, message: '请填写昵称' }]"
        />
        <van-field
          v-model="formData.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
        <van-field
          v-model="formData.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[
            { required: true, message: '请填写手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
          ]"
        />
      </van-cell-group>
      
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          注册
        </van-button>
      </div>
    </van-form>

    <div class="login-link">
      <span @click="goToLogin">已有账号？去登录</span>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import { register } from '@/api/user'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const formData = ref({
      nickname: '',
      password: '',
      phone: ''
    })

    const onSubmit = async (values) => {
      try {
        const res = await register({
          ...values,
          avatarUrl: '/images/avatar/fzd.png'  // 设置默认头像
        })
        showSuccessToast('注册成功')
        router.push('/login')
      } catch (error) {
        showFailToast(error.response?.data?.message || '注册失败')
      }
    }

    const onClickLeft = () => {
      router.back()
    }

    const goToLogin = () => {
      router.push('/login')
    }

    return {
      formData,
      onSubmit,
      onClickLeft,
      goToLogin
    }
  }
}
</script>

<style scoped>
.register {
  padding-bottom: 20px;
}
.login-link {
  text-align: center;
  margin-top: 16px;
  color: #1989fa;
}
.login-link span {
  cursor: pointer;
}
</style> 