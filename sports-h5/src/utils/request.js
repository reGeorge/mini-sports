import axios from 'axios'
import { showToast } from 'vant'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',  // 使用相对路径，让代理配置生效
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      showToast(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // token 过期或无效，清除 token 并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          showToast('请重新登录')
          break
        case 403:
          showToast('没有权限访问')
          break
        default:
          showToast(error.response?.data?.message || '请求失败')
      }
    } else {
      showToast('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default service 