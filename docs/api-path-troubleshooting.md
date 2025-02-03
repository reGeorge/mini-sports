# API路径问题定位与解决方案

## 问题现象
1. 部分API请求返回404错误（如赛事列表接口）
2. 部分API请求正常（如用户相关接口）
3. 前端请求路径与后端Controller路径不一致

## 定位思路

### 1. 检查前端配置
1. 检查 `request.js` 中的基础配置：
```javascript
const service = axios.create({
  baseURL: '/api',  // 统一添加/api前缀
  timeout: 5000
})
```

2. 检查 `vue.config.js` 中的代理配置：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    pathRewrite: {
      '^/api': ''  // 移除/api前缀
    }
  }
}
```

### 2. 检查后端配置
1. 检查各Controller的路径映射：
```java
// 用户相关接口（正常工作）
@RequestMapping("/user")

// 赛事相关接口（返回404）
@RequestMapping("/api/tournaments")  // 问题所在：多了/api前缀
```

2. 检查前端API调用：
```javascript
// user.js（正常工作）
url: '/user/login'  // 最终请求：/api/user/login

// tournament.js（返回404）
url: '/tournaments'  // 最终请求：/api/tournaments
```

## 问题原因
1. 前端统一通过 `request.js` 添加 `/api` 前缀
2. 后端Controller路径不统一，有的带 `/api` 前缀，有的不带
3. 代理配置中移除了 `/api` 前缀
4. 导致最终请求路径与后端路径不匹配

## 解决方案

### 1. 统一后端Controller路径
所有Controller统一不使用 `/api` 前缀：
```java
@RequestMapping("/user")
@RequestMapping("/role")
@RequestMapping("/permission")
@RequestMapping("/points")
@RequestMapping("/tournaments")  // 移除/api前缀
```

### 2. 统一前端API调用路径
所有API调用统一不使用 `/api` 前缀：
```javascript
// 正确示例
url: '/user/login'
url: '/tournaments'
url: '/tournaments/${id}/registrations'

// 错误示例（需要修改）
url: '/api/tournaments/${id}/registrations'
```

### 3. 保持代理配置
在 `vue.config.js` 中保持以下配置：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
    pathRewrite: {
      '^/api': ''  // 移除/api前缀，与后端路径对应
    }
  }
}
```

## 最佳实践
1. 后端Controller路径统一不使用 `/api` 前缀
2. 前端API调用路径与后端Controller路径保持一致
3. 通过 `request.js` 统一添加 `/api` 前缀
4. 通过代理配置统一处理 `/api` 前缀

## 优势
1. 路径结构清晰，便于维护
2. 前后端路径一致，减少混淆
3. 通过 `/api` 前缀区分API请求
4. 便于配置代理和跨域处理 