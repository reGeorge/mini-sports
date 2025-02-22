const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    host: '0.0.0.0',
    proxy: {
      '/api': {  // 匹配 /api 开头的请求
        target: 'http://localhost:8088',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''  // 移除 /api 前缀（后端定义的接口不带/api前缀）
        }
      }
    }
  }
}) 