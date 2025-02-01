<template>
  <div class="login">
    <van-nav-bar
      title="用户登录"
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
      </van-cell-group>
      
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          登录
        </van-button>
      </div>
    </van-form>

    <div class="register-link">
      <span @click="goToRegister">还没有账号？去注册</span>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import { login } from '@/api/user'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const formData = ref({
      nickname: '',
      password: ''
    })

    const onSubmit = async (values) => {
      try {
        const res = await login(values)
        showSuccessToast('登录成功')
        // 存储用户信息
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        // 跳转到个人信息页面
        router.push('/profile')
      } catch (error) {
        showFailToast(error.response?.data?.message || '登录失败')
      }
    }

    const onClickLeft = () => {
      router.back()
    }

    const goToRegister = () => {
      router.push('/register')
    }

    return {
      formData,
      onSubmit,
      onClickLeft,
      goToRegister
    }
  }
}
</script>

<style scoped>
.login {
  padding-bottom: 20px;
}
.register-link {
  text-align: center;
  margin-top: 16px;
  color: #1989fa;
}
.register-link span {
  cursor: pointer;
}
</style> 