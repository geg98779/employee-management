import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const instance = axios.create({
  baseURL: '', // 使用相对路径，通过Vite代理转发
  timeout: 10000, // 请求超时时间
  withCredentials: true // 允许跨域请求携带Cookie
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    // 如果响应成功，直接返回数据
    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('user')
          localStorage.removeItem('token')
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          // 权限不足
          ElMessage.error('权限不足，无法访问')
          break
        case 404:
          // 请求的资源不存在
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          // 服务器错误
          ElMessage.error('服务器错误，请稍后再试')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      // 请求超时或网络错误
      if (error.message.includes('timeout')) {
        ElMessage.error('请求超时，请检查网络')
      } else {
        ElMessage.error('网络错误，请检查网络连接')
      }
    }
    return Promise.reject(error)
  }
)

export default instance 