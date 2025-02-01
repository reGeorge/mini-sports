<template>
  <div class="home">
    <van-nav-bar title="首页" />
    
    <div class="content">
      <h2>欢迎使用球赛小程序</h2>
      <van-button type="primary" @click="goToRegister">注册</van-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
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
</style> 