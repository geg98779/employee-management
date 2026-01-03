<template>
  <div class="home-container">
    <el-container class="main-container">
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h3>员工管理系统</h3>
        </div>
        <el-menu
          router
          :default-active="activeMenu"
          class="menu"
          background-color="#304156"
          text-color="#fff"
          active-text-color="#409EFF">
          <el-menu-item index="/products">
            <el-icon><el-icon-goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="/categories">
            <el-icon><el-icon-folder /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/suppliers">
            <el-icon><el-icon-user /></el-icon>
            <span>供应商管理</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><el-icon-document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/users" v-if="isAdmin">
            <el-icon><el-icon-setting /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><el-icon-user /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container class="content-container">
        <el-header class="header">
          <div class="header-left">
            <el-icon class="toggle-sidebar" @click="toggleSidebar">
              <el-icon-menu />
            </el-icon>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-info">
                {{ currentUser.username }}
                <el-icon><el-icon-arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const sidebarCollapsed = ref(false)
const currentUser = ref({})

const activeMenu = computed(() => {
  return route.path
})

const isAdmin = computed(() => {
  return currentUser.value.role === 1
})

onMounted(() => {
  // 从本地存储获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  
  // 如果没有登录，跳转到登录页
  if (!currentUser.value.id) {
    router.push('/login')
  }
  
  // 默认跳转到商品页面
  if (route.path === '/') {
    router.push('/products')
  }
})

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 清除本地存储的用户信息
      localStorage.removeItem('user')
      // 跳转到登录页
      router.push('/login')
      ElMessage.success('退出登录成功')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.home-container {
  height: 100%;
}

.main-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  border-bottom: 1px solid #1f2d3d;
}

.menu {
  border-right: none;
}

.header {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style> 