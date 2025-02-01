import axios from 'axios'
import { showToast } from 'vant'

const service = axios.create({
  baseURL: process.env.NODE_ENV === 'development' ? '/api' : '/api',  // 根据环境设置baseURL
  timeout: 5000
})

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
    console.error('请求错误:', error)  // 添加错误日志
    showToast(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service 