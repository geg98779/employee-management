<template>
  <div class="user-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="handleAdd">添加用户</el-button>
    </div>

    <div class="search-box">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.username" placeholder="用户名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.role" placeholder="角色" clearable>
            <el-option :label="'管理员'" :value="1"></el-option>
            <el-option :label="'普通用户'" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column prop="username" label="用户名"></el-table-column>
      <el-table-column prop="realName" label="真实姓名"></el-table-column>
      <el-table-column prop="phone" label="手机号码"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="role" label="角色" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.role === 1 ? 'danger' : 'primary'">
            {{ scope.row.role === 1 ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange">
      </el-pagination>
    </div>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="form.id > 0"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item v-else label="密码">
          <el-input v-model="form.password" type="password" placeholder="留空表示不修改密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio :label="1">管理员</el-radio>
            <el-radio :label="2">普通用户</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = reactive({
  username: '',
  role: ''
})

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: 0,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 2
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 生命周期钩子
onMounted(() => {
  fetchData()
})

// 获取用户列表
const fetchData = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = new URLSearchParams()
    if (searchForm.username) {
      params.append('username', searchForm.username)
    }
    if (searchForm.role !== '') {
      params.append('role', searchForm.role)
    }

    // 发送请求
    const url = `/api/user${params.toString() ? '?' + params.toString() : ''}`
    console.log('获取用户列表URL:', url)
    const response = await axios.get(url)

    console.log('获取用户列表响应:', response.data)
    if (response.data.code === 200) {
      tableData.value = response.data.data || []
      total.value = tableData.value.length
    } else {
      ElMessage.error(response.data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.username = ''
  searchForm.role = ''
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

// 页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

// 添加用户
const handleAdd = () => {
  dialogTitle.value = '添加用户'
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  // 复制用户数据到表单
  Object.assign(form, {
    id: row.id || 0,
    username: row.username || '',
    password: '', // 编辑时密码留空
    realName: row.realName || '',
    phone: row.phone || '',
    email: row.email || '',
    role: row.role || 2
  })
  dialogVisible.value = true
}

// 删除用户
const handleDelete = async (row) => {
  // 不允许删除管理员账号
  if (row.username === 'admin') {
    ElMessage.warning('不能删除管理员账号')
    return
  }

  ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      console.log('删除用户ID:', row.id)
      const response = await axios.delete(`/api/user/${row.id}`)
      console.log('删除用户响应:', response.data)
      if (response.data.code === 200) {
        ElMessage.success(response.data.message || '删除成功')
        fetchData()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      if (error.response && error.response.data) {
        ElMessage.error(error.response.data.message || '删除用户失败')
      } else {
        ElMessage.error('删除用户失败')
      }
    }
  }).catch(() => {})
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    id: 0,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    role: 2
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 构建要发送的数据
const buildUserData = () => {
  const data = {
    id: form.id || 0,
    username: form.username,
    realName: form.realName,
    phone: form.phone,
    email: form.email,
    role: form.role
  }

  // 只有添加用户或者编辑时填写了新密码，才发送密码
  if (!form.id || (form.password && form.password.trim() !== '')) {
    data.password = form.password
  }

  return data
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const userData = buildUserData()
        console.log('提交的用户数据:', userData)

        let response
        if (form.id) {
          // 更新用户
          console.log('更新用户，URL: /api/user')
          response = await axios.put('/api/user', userData)
        } else {
          // 添加用户
          console.log('添加用户，URL: /api/user')
          response = await axios.post('/api/user', userData)
        }

        console.log('提交表单响应:', response.data)

        if (response.data.code === 200) {
          ElMessage.success(response.data.message || (form.id ? '更新成功' : '添加成功'))
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.data.message || (form.id ? '更新用户失败' : '添加用户失败'))
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        if (error.response && error.response.data) {
          console.error('错误响应数据:', error.response.data)
          ElMessage.error(error.response.data.message || '操作失败')
        } else {
          ElMessage.error(form.id ? '更新用户失败' : '添加用户失败')
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.user-container {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.search-box {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>