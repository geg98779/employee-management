<template>
  <div class="category-container">
    <div class="page-header">
      <h2>部门管理</h2>
      <el-button type="primary" @click="handleAdd">添加部门</el-button>
    </div>
    
    <el-table :data="tableData" border style="width: 100%" row-key="id" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="部门名称"></el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级部门" style="width: 100%">
            <el-option :key="0" label="最高级部门" :value="0"></el-option>
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3"></el-input>
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
import request from '../api/config'

// 表格数据
const tableData = ref([])
const loading = ref(false)
const categoryOptions = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: '',
  name: '',
  description: '',
  parentId: 0,
  status: 1
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 生命周期钩子
onMounted(() => {
  fetchData()
})

// 获取分类列表
const fetchData = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/category/')
    if (response.data.code === 200) {
      tableData.value = response.data.data
      // 过滤出可选的父级分类（排除自己）
      categoryOptions.value = tableData.value.filter(item => item.id !== form.id)
    } else {
      ElMessage.error(response.data.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 添加分类
const handleAdd = () => {
  dialogTitle.value = '添加分类'
  resetForm()
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  dialogVisible.value = true
  
  // 更新可选的父级分类（排除自己及其子分类）
  categoryOptions.value = tableData.value.filter(item => item.id !== form.id && item.parentId !== form.id)
}

// 删除分类
const handleDelete = (row) => {
  // 检查是否有子分类
  const hasChildren = tableData.value.some(item => item.parentId === row.id)
  if (hasChildren) {
    ElMessage.warning('该分类下有子分类，不能删除')
    return
  }
  
  ElMessageBox.confirm(`确定要删除分类"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.delete(`/api/category/${row.id}`)
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除分类失败:', error)
      ElMessage.error('删除分类失败')
    }
  }).catch(() => {})
}

// 重置表单
const resetForm = () => {
  Object.keys(form).forEach(key => {
    form[key] = key === 'status' ? 1 : key === 'parentId' ? 0 : ''
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        let response
        if (form.id) {
          // 更新分类
          response = await request.put('/api/category/', form)
        } else {
          // 添加分类
          response = await request.post('/api/category/', form)
        }
        
        if (response.data.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.data.message || (form.id ? '更新失败' : '添加失败'))
        }
      } catch (error) {
        console.error(form.id ? '更新分类失败:' : '添加分类失败:', error)
        ElMessage.error(form.id ? '更新分类失败' : '添加分类失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.category-container {
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
</style> 