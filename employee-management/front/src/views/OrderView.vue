<template>
  <div class="order-container">
    <div class="page-header">
      <h2>订单管理</h2>
      <el-button type="primary" @click="handleAdd">创建订单</el-button>
    </div>
    
    <div class="search-box">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.orderNo" placeholder="订单编号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="订单状态" clearable>
            <el-option :label="'待付款'" :value="0"></el-option>
            <el-option :label="'已付款'" :value="1"></el-option>
            <el-option :label="'已发货'" :value="2"></el-option>
            <el-option :label="'已完成'" :value="3"></el-option>
            <el-option :label="'已取消'" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60"></el-table-column>
      <el-table-column prop="orderNo" label="订单编号" width="180"></el-table-column>
      <el-table-column prop="username" label="用户"></el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="scope">
          {{ scope.row.totalAmount ? '¥' + scope.row.totalAmount : '' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentMethod" label="支付方式" width="100"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleView(scope.row)">查看</el-button>
          <el-button size="small" type="primary" @click="handleUpdateStatus(scope.row)" v-if="scope.row.status < 3">更新状态</el-button>
          <el-button size="small" type="danger" @click="handleCancel(scope.row)" v-if="scope.row.status === 0">取消</el-button>
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
    
    <!-- 查看订单详情对话框 -->
    <el-dialog title="订单详情" v-model="detailDialogVisible" width="800px">
      <div class="order-detail">
        <div class="order-info">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ currentOrder.username }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="总金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ currentOrder.paymentMethod }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ currentOrder.paymentTime }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ currentOrder.updateTime }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="order-items">
          <h3>订单项</h3>
          <el-table :data="currentOrder.orderItems" border style="width: 100%">
            <el-table-column prop="productName" label="商品名称"></el-table-column>
            <el-table-column prop="productPrice" label="单价" width="100">
              <template #default="scope">
                ¥{{ scope.row.productPrice }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
            <el-table-column prop="totalPrice" label="小计" width="120">
              <template #default="scope">
                ¥{{ scope.row.totalPrice }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
    
    <!-- 更新订单状态对话框 -->
    <el-dialog title="更新订单状态" v-model="statusDialogVisible" width="400px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(statusForm.currentStatus)">
            {{ getStatusText(statusForm.currentStatus) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="statusForm.newStatus" placeholder="请选择新状态">
            <el-option
              v-for="item in availableStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatusUpdate" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 创建订单对话框 -->
    <el-dialog title="创建订单" v-model="createDialogVisible" width="800px">
      <el-form :model="orderForm" :rules="orderRules" ref="orderFormRef" label-width="100px">
        <el-form-item label="用户" prop="userId">
          <el-select v-model="orderForm.userId" placeholder="请选择用户" style="width: 100%">
            <el-option v-for="item in userOptions" :key="item.id" :label="item.username" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="商品列表">
          <div v-for="(item, index) in orderForm.items" :key="index" class="order-item-form">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-select v-model="item.productId" placeholder="选择商品" @change="handleProductChange($event, index)">
                  <el-option v-for="product in productOptions" :key="product.id" :label="product.name" :value="product.id"></el-option>
                </el-select>
              </el-col>
              <el-col :span="6">
                <el-input-number v-model="item.quantity" :min="1" @change="calculateItemTotal(index)" placeholder="数量"></el-input-number>
              </el-col>
              <el-col :span="6">
                <el-input v-model="item.totalPrice" disabled placeholder="小计"></el-input>
              </el-col>
              <el-col :span="2">
                <el-button type="danger" icon="el-icon-delete" circle @click="removeOrderItem(index)" v-if="orderForm.items.length > 1"></el-button>
              </el-col>
            </el-row>
          </div>
          <div style="margin-top: 10px;">
            <el-button type="primary" @click="addOrderItem" plain>添加商品</el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="总金额">
          <el-input v-model="orderForm.totalAmount" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-select v-model="orderForm.paymentMethod" placeholder="请选择支付方式">
            <el-option label="微信支付" value="微信支付"></el-option>
            <el-option label="支付宝" value="支付宝"></el-option>
            <el-option label="银行卡" value="银行卡"></el-option>
            <el-option label="现金" value="现金"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrderForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
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
  orderNo: '',
  status: '',
  dateRange: []
})

// 当前订单
const currentOrder = ref({
  orderItems: []
})

// 对话框相关
const detailDialogVisible = ref(false)
const statusDialogVisible = ref(false)
const createDialogVisible = ref(false)
const submitLoading = ref(false)

// 状态更新表单
const statusForm = reactive({
  id: '',
  orderNo: '',
  currentStatus: 0,
  newStatus: ''
})

// 创建订单表单
const orderFormRef = ref(null)
const orderForm = reactive({
  userId: '',
  items: [{ productId: '', quantity: 1, price: 0, totalPrice: 0 }],
  totalAmount: 0,
  paymentMethod: ''
})

// 表单验证规则
const orderRules = {
  userId: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ],
  paymentMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ]
}

// 用户和商品选项
const userOptions = ref([])
const productOptions = ref([])

// 可用的状态选项
const availableStatusOptions = computed(() => {
  const options = [
    { value: 0, label: '待付款' },
    { value: 1, label: '已付款' },
    { value: 2, label: '已发货' },
    { value: 3, label: '已完成' },
    { value: 4, label: '已取消' }
  ]
  
  // 根据当前状态过滤可选的下一状态
  return options.filter(option => {
    if (statusForm.currentStatus === 0) {
      return option.value === 1 || option.value === 4
    } else if (statusForm.currentStatus === 1) {
      return option.value === 2
    } else if (statusForm.currentStatus === 2) {
      return option.value === 3
    } else {
      return false
    }
  })
})

// 生命周期钩子
onMounted(() => {
  fetchData()
  fetchUsers()
  fetchProducts()
})

// 获取订单列表
const fetchData = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/order/')
    if (response.data.code === 200) {
      tableData.value = response.data.data
      total.value = tableData.value.length
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await request.get('/api/user/')
    if (response.data.code === 200) {
      userOptions.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  }
}

// 获取商品列表
const fetchProducts = async () => {
  try {
    const response = await request.get('/api/product/')
    if (response.data.code === 200) {
      productOptions.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '已付款',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'danger'
  }
  return statusMap[status] || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.orderNo = ''
  searchForm.status = ''
  searchForm.dateRange = []
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

// 查看订单详情
const handleView = async (row) => {
  try {
    const response = await request.get(`/api/order/${row.id}`)
    if (response.data.code === 200) {
      currentOrder.value = response.data.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 更新订单状态
const handleUpdateStatus = (row) => {
  statusForm.id = row.id
  statusForm.orderNo = row.orderNo
  statusForm.currentStatus = row.status
  statusForm.newStatus = ''
  statusDialogVisible.value = true
}

// 提交状态更新
const submitStatusUpdate = async () => {
  if (!statusForm.newStatus) {
    ElMessage.warning('请选择新状态')
    return
  }
  
  submitLoading.value = true
  try {
    const response = await request.put('/api/order/status', null, {
      params: {
        id: statusForm.id,
        status: statusForm.newStatus
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('状态更新成功')
      statusDialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(response.data.message || '状态更新失败')
    }
  } catch (error) {
    console.error('更新订单状态失败:', error)
    ElMessage.error('更新订单状态失败')
  } finally {
    submitLoading.value = false
  }
}

// 取消订单
const handleCancel = (row) => {
  ElMessageBox.confirm(`确定要取消订单"${row.orderNo}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.put('/api/order/status', null, {
        params: {
          id: row.id,
          status: 4 // 已取消状态
        }
      })
      
      if (response.data.code === 200) {
        ElMessage.success('订单取消成功')
        fetchData()
      } else {
        ElMessage.error(response.data.message || '订单取消失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }).catch(() => {})
}

// 创建订单
const handleAdd = () => {
  resetOrderForm()
  createDialogVisible.value = true
}

// 重置订单表单
const resetOrderForm = () => {
  orderForm.userId = ''
  orderForm.items = [{ productId: '', quantity: 1, price: 0, totalPrice: 0 }]
  orderForm.totalAmount = 0
  orderForm.paymentMethod = ''
  if (orderFormRef.value) {
    orderFormRef.value.resetFields()
  }
}

// 添加订单项
const addOrderItem = () => {
  orderForm.items.push({ productId: '', quantity: 1, price: 0, totalPrice: 0 })
}

// 移除订单项
const removeOrderItem = (index) => {
  orderForm.items.splice(index, 1)
  calculateOrderTotal()
}

// 商品选择变化
const handleProductChange = (productId, index) => {
  const product = productOptions.value.find(item => item.id === productId)
  if (product) {
    orderForm.items[index].price = product.price
    calculateItemTotal(index)
  }
}

// 计算订单项总价
const calculateItemTotal = (index) => {
  const item = orderForm.items[index]
  item.totalPrice = item.price * item.quantity
  calculateOrderTotal()
}

// 计算订单总价
const calculateOrderTotal = () => {
  orderForm.totalAmount = orderForm.items.reduce((total, item) => total + (item.totalPrice || 0), 0)
}

// 提交订单表单
const submitOrderForm = () => {
  orderFormRef.value.validate(async (valid) => {
    if (valid) {
      // 验证订单项
      const validItems = orderForm.items.every(item => item.productId && item.quantity > 0)
      if (!validItems) {
        ElMessage.warning('请完善订单商品信息')
        return
      }
      
      submitLoading.value = true
      try {
        const response = await request.post('/api/order/', orderForm)
        if (response.data.code === 200) {
          ElMessage.success('订单创建成功')
          createDialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.data.message || '订单创建失败')
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        ElMessage.error('创建订单失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}
</script>

<style scoped>
.order-container {
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

.order-detail {
  padding: 10px;
}

.order-detail h3 {
  margin: 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-info {
  margin-bottom: 20px;
}

.order-item-form {
  margin-bottom: 10px;
}
</style> 