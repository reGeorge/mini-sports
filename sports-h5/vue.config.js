const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080,
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
          console.log('原始请求:', `${req.method} ${req.protocol}://${req.headers.host}${req.url}`);
          console.log('代理到目标:', `${proxyReq.method} ${proxyReq.protocol}//${proxyReq.host}${proxyReq.path}`);
          console.log('请求头:', proxyReq.getHeaders());
        },
        onProxyRes(proxyRes, req) {
          console.log('响应状态:', proxyRes.statusCode);
          console.log('响应头:', proxyRes.headers);
        }
      }
    }
  }
}) 