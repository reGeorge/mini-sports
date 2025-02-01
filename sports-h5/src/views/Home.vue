<template>
  <div class="home">
    <van-nav-bar title="首页" />
    
    <div class="content">
      <h2>欢迎使用球赛小程序</h2>
      <div class="auth-buttons">
        <van-button type="primary" @click="goToLogin">登录</van-button>
        <van-button plain type="primary" @click="goToRegister">注册</van-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    goToLogin() {
      this.$router.push('/login')
    },
    goToRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.content {
  padding: 20px;
  text-align: center;
}

h2 {
  margin-bottom: 20px;
}

.auth-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style> 