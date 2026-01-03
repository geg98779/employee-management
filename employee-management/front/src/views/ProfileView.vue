<template>
  <div class="profile-container">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>
    
    <el-card class="profile-card" v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱地址" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="form.role === 1 ? 'danger' : 'success'">
            {{ form.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="修改密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password placeholder="不修改请留空"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="不修改请留空"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/config'

// 表单数据
const form = reactive({
  id: '',
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: 2,
  newPassword: '',
  confirmPassword: ''
})

// 状态变量
const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 表单验证规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (form.newPassword && value !== form.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 生命周期钩子
onMounted(() => {
  loadUserInfo()
})

// 加载用户信息
const loadUserInfo = async () => {
  loading.value = true
  try {
    // 从本地存储获取用户ID
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      ElMessage.error('用户未登录')
      return
    }
    
    const user = JSON.parse(userStr)
    const userId = user.id
    
    // 请求用户详细信息
    const response = await request.get(`/api/user/${userId}`)
    if (response.data.code === 200) {
      const userData = response.data.data
      // 填充表单数据
      form.id = userData.id
      form.username = userData.username
      form.realName = userData.realName || ''
      form.phone = userData.phone || ''
      form.email = userData.email || ''
      form.role = userData.role
    } else {
      ElMessage.error(response.data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 构建提交的数据
        const submitData = {
          id: form.id,
          username: form.username,
          realName: form.realName,
          phone: form.phone,
          email: form.email,
          role: form.role
        }
        
        // 如果有新密码，则添加到提交数据中
        if (form.newPassword) {
          submitData.password = form.newPassword
        }
        
        console.log('提交的数据:', submitData);
        
        // 提交更新请求
        const response = await request.put('/api/user/profile', submitData);
        console.log('服务器响应:', response.data);
        
        if (response.data.code === 200) {
          ElMessage.success('个人信息更新成功')
          
          // 更新本地存储的用户信息
          const userStr = localStorage.getItem('user')
          if (userStr) {
            const user = JSON.parse(userStr)
            user.username = form.username
            user.realName = form.realName
            localStorage.setItem('user', JSON.stringify(user))
          }
          
          // 清空密码字段
          form.newPassword = ''
          form.confirmPassword = ''
        } else {
          ElMessage.error(response.data.message || '更新个人信息失败')
        }
      } catch (error) {
        console.error('更新个人信息失败:', error)
        if (error.response) {
          console.error('错误响应:', error.response.data)
          ElMessage.error(error.response.data.message || '更新个人信息失败')
        } else {
          ElMessage.error('更新个人信息失败: ' + error.message)
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.profile-container {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 20px;
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
}
</style> 