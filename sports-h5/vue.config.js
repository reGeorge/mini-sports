const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        },
        logLevel: 'debug',
        onProxyReq(proxyReq, req) {
          console.log('原始请求:', `http://localhost:${req.socket.localPort}${req.url}`);
          console.log('代理到目标:', `${proxyReq.protocol}//${proxyReq.host}:8088${proxyReq.path}`);
        }
      }
    }
  }
}) 