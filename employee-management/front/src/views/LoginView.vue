<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 顶部装饰 -->
      <div class="login-header">
        <div class="logo-circle">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" fill="white"/>
            <path d="M16 7C16 8.66 17.34 10 19 10C20.66 10 22 8.66 22 7C22 5.34 20.66 4 19 4C17.34 4 16 5.34 16 7Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 7C2 8.66 3.34 10 5 10C6.66 10 8 8.66 8 7C8 5.34 6.66 4 5 4C3.34 4 2 5.34 2 7Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 17C16 18.66 17.34 20 19 20C20.66 20 22 18.66 22 17C22 15.34 20.66 14 19 14C17.34 14 16 15.34 16 17Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2 17C2 18.66 3.34 20 5 20C6.66 20 8 18.66 8 17C8 15.34 6.66 14 5 14C3.34 14 2 15.34 2 17Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="login-title">员工管理系统</h1>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>

      <!-- 登录表单 -->
      <div class="login-form-container">
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0" class="login-form">
          <!-- 用户名 -->
          <div class="input-group">
            <el-form-item prop="username">
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg class="icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <el-input
                    v-model="loginForm.username"
                    placeholder="请输入用户名"
                    class="custom-input"
                ></el-input>
              </div>
            </el-form-item>
          </div>

          <!-- 密码 -->
          <div class="input-group">
            <el-form-item prop="password">
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg class="icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 15V17M6 21H18C19.1046 21 20 20.1046 20 19V13C20 11.8954 19.1046 11 18 11H6C4.89543 11 4 11.8954 4 13V19C4 20.1046 4.89543 21 6 21ZM16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11H16Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </div>
                <el-input
                    v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                    show-password
                    class="custom-input"
                ></el-input>
              </div>
            </el-form-item>
          </div>

          <!-- 登录按钮 -->
          <div class="button-group">
            <el-form-item>
              <el-button
                  type="primary"
                  @click="handleLogin"
                  :loading="loading"
                  class="login-btn"
              >
                <template #loading>
                  <span class="btn-loading">
                    <span class="loading-dot"></span>
                    <span class="loading-dot"></span>
                    <span class="loading-dot"></span>
                  </span>
                  登录中
                </template>
                <span v-if="!loading">
                  <svg class="btn-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M11 16L7 12M7 12L11 8M7 12H21M16 16V17C16 18.6569 14.6569 20 13 20H6C4.34315 20 3 18.6569 3 17V7C3 5.34315 4.34315 4 6 4H13C14.6569 4 16 5.34315 16 7V8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  登录
                </span>
              </el-button>
            </el-form-item>
          </div>
        </el-form>

        <!-- 注册链接 -->
        <div class="register-container">
          <router-link to="/register" class="register-link">
            <span>没有账号？</span>
            <span class="register-text">立即注册</span>
            <svg class="arrow-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </router-link>
        </div>
      </div>

    </div>

    <!-- 背景装饰 -->
    <div class="bg-circle bg-circle-1"></div>
    <div class="bg-circle bg-circle-2"></div>
    <div class="bg-circle bg-circle-3"></div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const params = new URLSearchParams()
        params.append('username', loginForm.username)
        params.append('password', loginForm.password)

        const response = await axios.post('/api/user/login', params)
        if (response.data.code === 200) {
          ElMessage.success('登录成功')
          localStorage.setItem('user', JSON.stringify(response.data.data))

          // 跳转到首页或重定向页面
          const redirectPath = route.query.redirect || '/'
          router.push(redirectPath)
        } else {
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后重试')
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 基础样式 */
.login-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
}

/* 背景装饰圆圈 */
.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(100, 115, 255, 0.1) 0%, rgba(167, 139, 250, 0.1) 100%);
  z-index: 0;
}

.bg-circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -100px;
}

.bg-circle-2 {
  width: 200px;
  height: 200px;
  bottom: -80px;
  left: -80px;
}

.bg-circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
}

/* 登录卡片 */
.login-card {
  width: 100%;
  max-width: 440px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(255, 255, 255, 0.3);
  overflow: hidden;
  position: relative;
  z-index: 1;
  animation: cardFadeIn 0.8s ease-out;
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 卡片头部 */
.login-header {
  background: linear-gradient(135deg, #6673ff 0%, #8b5cf6 100%);
  padding: 40px 32px;
  text-align: center;
  color: white;
  position: relative;
}

.logo-circle {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.logo-icon {
  width: 40px;
  height: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 400;
}

/* 登录表单区域 */
.login-form-container {
  padding: 40px 32px;
}

/* 输入框组 */
.input-group {
  margin-bottom: 24px;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  color: #8b8b8b;
  transition: color 0.3s ease;
}

.input-icon .icon {
  width: 20px;
  height: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  padding-left: 48px;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #eef0f3;
  background: #fafbfc;
  box-shadow: none;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #6673ff;
  background: white;
  box-shadow: 0 4px 12px rgba(102, 115, 255, 0.15);
}

.custom-input :deep(.el-input__inner) {
  color: #333;
  font-size: 15px;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: #999;
}

/* 按钮组 */
.button-group {
  margin-top: 32px;
}

.login-btn {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6673ff 0%, #8b5cf6 100%);
  border: none;
  font-size: 16px;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(102, 115, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 115, 255, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.login-btn:hover::after {
  opacity: 1;
}

.btn-icon {
  width: 18px;
  height: 18px;
  margin-right: 8px;
  vertical-align: middle;
}

/* 加载动画 */
.btn-loading {
  display: inline-flex;
  align-items: center;
  margin-right: 8px;
}

.loading-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: white;
  margin: 0 2px;
  animation: dotPulse 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes dotPulse {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.6;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 注册链接 */
.register-container {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

.register-link {
  display: inline-flex;
  align-items: center;
  color: #666;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #6673ff;
}

.register-text {
  color: #6673ff;
  font-weight: 600;
  margin: 0 6px;
  transition: color 0.3s ease;
}

.register-link:hover .register-text {
  color: #8b5cf6;
}

.arrow-icon {
  width: 16px;
  height: 16px;
  transition: transform 0.3s ease;
}

.register-link:hover .arrow-icon {
  transform: translateX(4px);
}

/* 底部信息 */
.login-footer {
  padding: 20px 32px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
  background: #fafbfc;
  color: #888;
  font-size: 13px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    border-radius: 20px;
  }

  .login-header {
    padding: 32px 24px;
  }

  .login-form-container {
    padding: 32px 24px;
  }

  .login-title {
    font-size: 24px;
  }

  .logo-circle {
    width: 70px;
    height: 70px;
  }

  .logo-icon {
    width: 36px;
    height: 36px;
  }
}

@media (max-width: 360px) {
  .login-container {
    padding: 16px;
  }

  .login-header {
    padding: 28px 20px;
  }

  .login-form-container {
    padding: 28px 20px;
  }
}
</style>