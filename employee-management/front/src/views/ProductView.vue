<template>
  <div class="product-container">
    <div class="page-header">
      <h2>员工管理</h2>
      <el-button type="primary" @click="handleAdd">添加员工</el-button>
    </div>
    
    <div class="search-box">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.name" placeholder="员工名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.categoryId" placeholder="部门分类" clearable>
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
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
      <el-table-column prop="name" label="员工名称"></el-table-column>
      <el-table-column prop="categoryName" label="部门分类"></el-table-column>
      <el-table-column prop="price" label="售价" width="100">
        <template #default="scope">
          {{ scope.row.price ? '¥' + scope.row.price : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80"></el-table-column>
      <el-table-column prop="unit" label="单位" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>x
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
    
    <!-- 添加/编辑商品对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="员工名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="所属部门" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择部门分类" style="width: 100%">
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="薪资" prop="costPrice">
          <el-input-number v-model="form.costPrice" :precision="2" :step="0.1" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="身高" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1"></el-input-number>
        </el-form-item>
        <el-form-item label="职位" prop="unit">
          <el-input v-model="form.unit"></el-input>
        </el-form-item>
        <el-form-item label="职级" prop="barcode">
          <el-input v-model="form.barcode"></el-input>
        </el-form-item>
        <el-form-item label="" prop="supplierId">
          <el-select v-model="form.supplierId" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="item in supplierOptions" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="员工描述" prop="description">
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
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = reactive({
  name: '',
  categoryId: ''
})

// 分类和供应商选项
const categoryOptions = ref([])
const supplierOptions = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加员工')
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: '',
  name: '',
  description: '',
  categoryId: '',
  price: '',
  costPrice: '',
  stock: 0,
  unit: '',
  barcode: '',
  status: 1,
  supplierId: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入员工名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ]
}

// 生命周期钩子
onMounted(() => {
  fetchData()
  fetchCategories()
  fetchSuppliers()
})

// 获取商品列表
const fetchData = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = new URLSearchParams();
    if (searchForm.name) {
      params.append('name', searchForm.name);
    }
    if (searchForm.categoryId) {
      params.append('categoryId', searchForm.categoryId);
    }
    
    // 发送请求
    const url = `/api/product/${params.toString() ? '?' + params.toString() : ''}`;
    const response = await request.get(url);
    
    if (response.data.code === 200) {
      tableData.value = response.data.data
      total.value = tableData.value.length
    } else {
      ElMessage.error(response.data.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await request.get('/api/category/')
    if (response.data.code === 200) {
      categoryOptions.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 获取供应商列表
const fetchSuppliers = async () => {
  try {
    const response = await request.get('/api/supplier/')
    if (response.data.code === 200) {
      supplierOptions.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取列表失败')
    }
  } catch (error) {
    console.error('获取列表失败:', error)
    ElMessage.error('获取列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.categoryId = ''
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

// 添加商品
const handleAdd = () => {
  dialogTitle.value = '添加员工'
  resetForm()
  dialogVisible.value = true
}

// 编辑商品
const handleEdit = (row) => {
  dialogTitle.value = '编辑员工'
  Object.keys(form).forEach(key => {
    form[key] = row[key]
  })
  dialogVisible.value = true
}

// 删除商品
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.delete(`/api/product/${row.id}`)
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(response.data.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 重置表单
const resetForm = () => {
  Object.keys(form).forEach(key => {
    form[key] = key === 'status' ? 1 : key === 'stock' ? 0 : ''
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
          // 更新商品
          response = await request.put('/api/product/', form)
        } else {
          // 添加商品
          response = await request.post('/api/product/', form)
        }
        
        if (response.data.code === 200) {
          ElMessage.success(form.id ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.data.message || (form.id ? '更新失败' : '添加失败'))
        }
      } catch (error) {
        console.error(form.id ? '更新失败:' : '添加失败:', error)
        ElMessage.error(form.id ? '更新失败' : '添加失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.product-container {
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

.search-box {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 