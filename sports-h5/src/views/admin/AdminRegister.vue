<template>
  <div class="admin-register">
    <van-nav-bar
      title="管理员注册"
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
          v-model="formData.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[
            { required: true, message: '请填写手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误' }
          ]"
        />
        <van-field
          v-model="formData.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[
            { required: true, message: '请填写密码' },
            { min: 6, message: '密码至少6位' }
          ]"
        />
        <van-field
          v-model="formData.confirmPassword"
          type="password"
          name="confirmPassword"
          label="确认密码"
          placeholder="请再次输入密码"
          :rules="[
            { required: true, message: '请确认密码' },
            { validator: validateConfirmPassword, message: '两次密码不一致' }
          ]"
        />
        <van-field
          v-model="formData.adminCode"
          name="adminCode"
          label="管理员码"
          placeholder="请输入管理员注册码"
          :rules="[{ required: true, message: '请填写管理员注册码' }]"
        />
      </van-cell-group>
      
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          注册
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { registerAdmin } from '@/api/user'

const router = useRouter()
const formData = ref({
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: '',
  adminCode: ''
})

const validateConfirmPassword = (val) => {
  return val === formData.value.password
}

const onSubmit = async (values) => {
  try {
    await registerAdmin({
      nickname: values.nickname,
      phone: values.phone,
      password: values.password,
      adminCode: values.adminCode
    })
    showToast('注册成功')
    router.push('/login')
  } catch (error) {
    showToast(error.response?.data?.message || '注册失败')
  }
}

const onClickLeft = () => {
  router.back()
}
</script>

<style scoped>
.admin-register {
  height: 100vh;
  background-color: #f7f8fa;
}
</style> 