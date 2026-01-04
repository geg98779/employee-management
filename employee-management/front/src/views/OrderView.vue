<template>
  <div class="order-container">
    <div class="page-header">
      <h2>任务管理</h2>
      <el-button type="primary" @click="handleAdd">创建任务</el-button>
    </div>

    <div class="search-box">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.orderNo" placeholder="任务编号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="任务状态" clearable>
            <el-option :label="'待交付'" :value="0"></el-option>
            <el-option :label="'已交付'" :value="1"></el-option>
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
      <el-table-column prop="orderNo" label="任务编号" width="180"></el-table-column>
      <el-table-column prop="username" label="员工"></el-table-column>
<!--      <el-table-column prop="totalAmount" label="总任务" width="120">-->
<!--        <template #default="scope">-->
<!--          {{ scope.row.totalAmount ? '¥' + scope.row.totalAmount : '' }}-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
<!--      <el-table-column prop="paymentMethod" label="任务类型" width="100"></el-table-column>-->
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
    <el-dialog title="任务详情" v-model="detailDialogVisible" width="800px">
      <div class="order-detail">
        <div class="order-info">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="任务编号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="员工">{{ currentOrder.username }}</el-descriptions-item>
            <el-descriptions-item label="任务状态">
              <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
            </el-descriptions-item>
<!--            <el-descriptions-item label="">¥{{ currentOrder.totalAmount }}</el-descriptions-item>-->
<!--            <el-descriptions-item label="支付方式">{{ currentOrder.paymentMethod }}</el-descriptions-item>-->
<!--            <el-descriptions-item label="支付时间">{{ currentOrder.paymentTime }}</el-descriptions-item>-->
<!--            <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>-->
<!--            <el-descriptions-item label="更新时间">{{ currentOrder.updateTime }}</el-descriptions-item>-->
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

<!--        <el-form-item label="商品列表">-->
<!--          <div v-for="(item, index) in orderForm.items" :key="index" class="order-item-form">-->
<!--            <el-row :gutter="10">-->
<!--              <el-col :span="10">-->
<!--                <el-select v-model="item.productId" placeholder="选择商品" @change="handleProductChange($event, index)">-->
<!--                  <el-option v-for="product in productOptions" :key="product.id" :label="product.name" :value="product.id"></el-option>-->
<!--                </el-select>-->
<!--              </el-col>-->
<!--              <el-col :span="6">-->
<!--                <el-input-number v-model="item.quantity" :min="1" @change="calculateItemTotal(index)" placeholder="数量"></el-input-number>-->
<!--              </el-col>-->
<!--              <el-col :span="6">-->
<!--                <el-input v-model="item.totalPrice" disabled placeholder="小计"></el-input>-->
<!--              </el-col>-->
<!--              <el-col :span="2">-->
<!--                <el-button type="danger" icon="el-icon-delete" circle @click="removeOrderItem(index)" v-if="orderForm.items.length > 1"></el-button>-->
<!--              </el-col>-->
<!--            </el-row>-->
<!--          </div>-->
<!--          <div style="margin-top: 10px;">-->
<!--            <el-button type="primary" @click="addOrderItem" plain>添加商品</el-button>-->
<!--          </div>-->
<!--        </el-form-item>-->

<!--        <el-form-item label="总金额">-->
<!--          <el-input v-model="orderForm.totalAmount" disabled></el-input>-->
<!--        </el-form-item>-->

<!--        <el-form-item label="支付方式" prop="paymentMethod">-->
<!--          <el-select v-model="orderForm.paymentMethod" placeholder="请选择支付方式">-->
<!--            <el-option label="微信支付" value="微信支付"></el-option>-->
<!--            <el-option label="支付宝" value="支付宝"></el-option>-->
<!--            <el-option label="银行卡" value="银行卡"></el-option>-->
<!--            <el-option label="现金" value="现金"></el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOrderForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>

  <!-- 新增的上传文件区域 -->
  <div class="upload-section" style="margin: 20px 0; padding: 20px; border: 1px solid #ebeef5; border-radius: 4px;">
    <h3 style="margin-bottom: 15px;">上传附件</h3>

    <!-- 上传操作区域 -->
    <div class="upload-operation">
      <el-upload
          ref="uploadRef"
          class="upload-demo"
          :action="uploadUrl"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :before-upload="beforeFileUpload"
          :file-list="fileList"
          :auto-upload="false"
          :limit="5"
          :on-exceed="handleExceed"
          multiple>
        <template #trigger>
          <el-button type="primary">选择文件</el-button>
        </template>
        <el-button style="margin-left: 10px;" type="success" @click="submitUpload">
          上传到服务器
        </el-button>
        <el-button style="margin-left: 10px;" type="warning" @click="pauseAllUploads" v-if="hasUploading">
          暂停所有
        </el-button>
        <el-button style="margin-left: 10px;" type="danger" @click="clearAllFiles">
          清空列表
        </el-button>
        <template #tip>
          <div class="el-upload__tip">
            支持上传图片、文档、压缩包等文件，单个文件不超过50MB，最多上传5个文件
          </div>
        </template>
      </el-upload>
    </div>

    <!-- 上传文件列表 -->
    <div class="file-list-container" v-if="fileList.length > 0">
      <h4 style="margin: 20px 0 10px 0;">上传文件列表</h4>
      <el-table :data="fileList" border style="width: 100%;" size="small">
        <el-table-column prop="name" label="文件名" min-width="200">
          <template #default="{ row }">
            <div class="file-name-cell">
              <i :class="getFileIcon(row.name)" style="margin-right: 8px;"></i>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getUploadStatusType(row.status)" size="small">
              {{ getUploadStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="180">
          <template #default="{ row }">
            <el-progress
                :percentage="row.percentage || 0"
                :status="getProgressStatus(row.status)"
                :show-text="row.status !== 'uploading'"
                :stroke-width="6"
                style="width: 150px;">
              <template #default="{ percentage }" v-if="row.status === 'uploading'">
                <span style="font-size: 12px;">{{ percentage }}%</span>
              </template>
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row, $index }">
            <div style="display: flex; gap: 5px;">
              <el-button
                  v-if="row.status === 'waiting' || row.status === 'paused'"
                  size="small"
                  @click="startUpload(row, $index)"
                  type="primary"
                  plain>
                上传
              </el-button>
              <el-button
                  v-if="row.status === 'uploading'"
                  size="small"
                  @click="pauseUpload(row, $index)"
                  type="warning"
                  plain>
                暂停
              </el-button>
              <el-button
                  v-if="row.status === 'success'"
                  size="small"
                  @click="downloadFile(row)"
                  type="success"
                  plain>
                下载
              </el-button>
              <el-button
                  size="small"
                  @click="removeFile($index)"
                  type="danger"
                  plain>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180"></el-table-column>
      </el-table>

      <!-- 上传统计信息 -->
      <div class="upload-stats" style="margin-top: 15px; padding: 10px; background-color: #f5f7fa; border-radius: 4px;">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-label">总文件数：</span>
              <span class="stat-value">{{ fileList.length }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-label">成功：</span>
              <span class="stat-value success">{{ successCount }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-label">失败：</span>
              <span class="stat-value danger">{{ errorCount }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <span class="stat-label">总大小：</span>
              <span class="stat-value">{{ formatFileSize(totalSize) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 模拟上传配置 -->
    <div class="upload-config" style="margin-top: 20px; padding: 15px; background-color: #fafafa; border-radius: 4px;">
      <h4 style="margin-bottom: 10px;">上传配置</h4>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="config-item">
            <span class="config-label">上传速度模拟：</span>
            <el-slider
                v-model="uploadSpeed"
                :min="1"
                :max="10"
                :step="1"
                show-stops
                style="width: 200px;">
            </el-slider>
            <span style="margin-left: 10px;">{{ uploadSpeed }}x</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="config-item">
            <span class="config-label">失败率模拟：</span>
            <el-slider
                v-model="failureRate"
                :min="0"
                :max="30"
                :step="5"
                show-stops
                style="width: 200px;">
            </el-slider>
            <span style="margin-left: 10px;">{{ failureRate }}%</span>
          </div>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="autoStartUpload" label="选择后自动开始上传" />
        </el-col>
      </el-row>
    </div>
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
    { value: 0, label: '待完成' },
    { value: 1, label: '已完成 ' },
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
    0: '待完成',
    1: '已完成',
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
      // if (!validItems) {
      //   ElMessage.warning('请完善订单商品信息')
      //   return
      // }

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

// ============ 新增的上传文件功能相关代码 ============

// 上传相关引用
const uploadRef = ref(null)

// 上传配置
const uploadUrl = ref('https://jsonplaceholder.typicode.com/posts/') // 模拟上传地址
const uploadSpeed = ref(5) // 上传速度倍数
const failureRate = ref(10) // 失败率百分比
const autoStartUpload = ref(false)

// 文件列表
const fileList = ref([])

// 上传计时器映射
const uploadTimers = ref({})

// 计算属性
const hasUploading = computed(() => {
  return fileList.value.some(file => file.status === 'uploading')
})

const successCount = computed(() => {
  return fileList.value.filter(file => file.status === 'success').length
})

const errorCount = computed(() => {
  return fileList.value.filter(file => file.status === 'error').length
})

const totalSize = computed(() => {
  return fileList.value.reduce((total, file) => total + (file.size || 0), 0)
})

// 文件上传前的验证
const beforeFileUpload = (file) => {
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB!')
    return false
  }

  // 检查文件类型（可选）
  const allowedTypes = [
    'image/jpeg', 'image/png', 'image/gif',
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'text/plain',
    'application/zip', 'application/x-rar-compressed'
  ]

  if (!allowedTypes.includes(file.type) && file.type !== '') {
    ElMessage.warning('文件类型可能不被支持')
  }

  return true
}

// 文件选择变化
const handleFileChange = (file, fileListArray) => {
  // 更新文件列表
  fileList.value = fileListArray.map(f => {
    // 如果是新文件，添加自定义属性
    if (f.raw) {
      return {
        ...f,
        id: generateFileId(),
        name: f.name,
        size: f.size,
        status: 'waiting',
        percentage: 0,
        uploadTime: '',
        raw: f.raw
      }
    }
    return f
  })

  // 如果开启了自动上传，开始上传
  if (autoStartUpload.value && file.status === 'ready') {
    startUpload(file, fileList.value.length - 1)
  }
}

// 文件移除
const handleFileRemove = (file, fileListArray) => {
  // 如果文件正在上传，先停止上传
  if (file.status === 'uploading' && uploadTimers.value[file.id]) {
    clearInterval(uploadTimers.value[file.id])
    delete uploadTimers.value[file.id]
  }

  fileList.value = fileListArray
}

// 超出文件限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传5个文件')
}

// 提交上传
const submitUpload = () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }

  // 开始上传所有等待中的文件
  fileList.value.forEach((file, index) => {
    if (file.status === 'waiting') {
      startUpload(file, index)
    }
  })
}

// 开始上传单个文件
const startUpload = (file, index) => {
  if (!file) return

  // 更新文件状态
  fileList.value[index].status = 'uploading'
  fileList.value[index].percentage = 0

  // 模拟上传进度
  const speedFactor = uploadSpeed.value
  const intervalTime = 100 / speedFactor // 根据速度调整间隔

  uploadTimers.value[file.id] = setInterval(() => {
    // 随机增加进度，模拟网络波动
    const increment = Math.random() * (5 * speedFactor) + (2 * speedFactor)
    fileList.value[index].percentage = Math.min(
        fileList.value[index].percentage + increment,
        100
    )

    // 模拟上传完成
    if (fileList.value[index].percentage >= 100) {
      clearInterval(uploadTimers.value[file.id])
      delete uploadTimers.value[file.id]

      // 模拟随机失败
      const shouldFail = Math.random() * 100 < failureRate.value

      if (shouldFail) {
        fileList.value[index].status = 'error'
        ElMessage.error(`文件 "${file.name}" 上传失败`)
      } else {
        fileList.value[index].status = 'success'
        fileList.value[index].uploadTime = new Date().toLocaleString()
        ElMessage.success(`文件 "${file.name}" 上传成功`)
      }
    }
  }, intervalTime)
}

// 暂停上传单个文件
const pauseUpload = (file, index) => {
  if (uploadTimers.value[file.id]) {
    clearInterval(uploadTimers.value[file.id])
    delete uploadTimers.value[file.id]
    fileList.value[index].status = 'paused'
  }
}

// 暂停所有上传
const pauseAllUploads = () => {
  fileList.value.forEach((file, index) => {
    if (file.status === 'uploading') {
      pauseUpload(file, index)
    }
  })
  ElMessage.info('已暂停所有上传任务')
}

// 清空所有文件
const clearAllFiles = () => {
  if (fileList.value.length === 0) return

  ElMessageBox.confirm('确定要清空所有文件吗？正在上传的文件也会被取消', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除所有计时器
    Object.values(uploadTimers.value).forEach(timer => {
      clearInterval(timer)
    })
    uploadTimers.value = {}

    // 清空文件列表
    fileList.value = []
    if (uploadRef.value) {
      uploadRef.value.clearFiles()
    }

    ElMessage.success('已清空所有文件')
  }).catch(() => {})
}

// 删除单个文件
const removeFile = (index) => {
  const file = fileList.value[index]

  ElMessageBox.confirm(`确定要删除文件 "${file.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 如果文件正在上传，先停止上传
    if (file.status === 'uploading' && uploadTimers.value[file.id]) {
      clearInterval(uploadTimers.value[file.id])
      delete uploadTimers.value[file.id]
    }

    // 从列表中移除
    fileList.value.splice(index, 1)

    // 更新上传组件的文件列表
    if (uploadRef.value) {
      uploadRef.value.handleRemove(file)
    }

    ElMessage.success('文件已删除')
  }).catch(() => {})
}

// 下载文件（模拟）
const downloadFile = (file) => {
  ElMessage.info(`模拟下载文件: ${file.name}`)

  // 模拟下载过程
  const link = document.createElement('a')
  link.style.display = 'none'
  link.href = URL.createObjectURL(file.raw || new Blob(['模拟文件内容'], { type: 'text/plain' }))
  link.download = file.name
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  // 显示下载成功消息
  setTimeout(() => {
    ElMessage.success(`文件 "${file.name}" 下载完成`)
  }, 500)
}

// 辅助函数
const generateFileId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const getFileIcon = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase()
  const iconMap = {
    // 图片
    jpg: 'el-icon-picture',
    jpeg: 'el-icon-picture',
    png: 'el-icon-picture',
    gif: 'el-icon-picture',
    bmp: 'el-icon-picture',
    // 文档
    pdf: 'el-icon-document',
    doc: 'el-icon-document',
    docx: 'el-icon-document',
    txt: 'el-icon-document',
    // 表格
    xls: 'el-icon-s-data',
    xlsx: 'el-icon-s-data',
    // 压缩包
    zip: 'el-icon-files',
    rar: 'el-icon-files',
    '7z': 'el-icon-files',
    // 默认
    default: 'el-icon-document'
  }

  return iconMap[ext] || iconMap.default
}

const getUploadStatusText = (status) => {
  const statusMap = {
    waiting: '等待上传',
    uploading: '上传中',
    success: '上传成功',
    error: '上传失败',
    paused: '已暂停'
  }
  return statusMap[status] || '未知状态'
}

const getUploadStatusType = (status) => {
  const statusMap = {
    waiting: 'info',
    uploading: 'primary',
    success: 'success',
    error: 'danger',
    paused: 'warning'
  }
  return statusMap[status] || 'info'
}

const getProgressStatus = (status) => {
  if (status === 'success') return 'success'
  if (status === 'error') return 'exception'
  return undefined
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

/* 新增的上传文件相关样式 */
.upload-section {
  background-color: white;
}

.file-name-cell {
  display: flex;
  align-items: center;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.stat-value {
  font-weight: bold;
  font-size: 16px;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.danger {
  color: #f56c6c;
}

.config-item {
  display: flex;
  align-items: center;
}

.config-label {
  color: #606266;
  font-size: 14px;
  margin-right: 10px;
  white-space: nowrap;
}
</style>