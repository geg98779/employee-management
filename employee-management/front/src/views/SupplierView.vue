<template>
  <div class="performance-container">
    <div class="page-header">
      <h2>绩效管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="generatePerformance">生成绩效</el-button>
        <el-button type="success" @click="exportPerformance">导出绩效</el-button>
      </div>
    </div>

    <div class="search-box">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.employeeName" placeholder="员工姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.department" placeholder="部门" clearable>
            <el-option label="技术部" value="技术部"></el-option>
            <el-option label="销售部" value="销售部"></el-option>
            <el-option label="市场部" value="市场部"></el-option>
            <el-option label="运营部" value="运营部"></el-option>
            <el-option label="人事部" value="人事部"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.month" placeholder="考核月份" clearable>
            <el-option
                v-for="month in monthOptions"
                :key="month.value"
                :label="month.label"
                :value="month.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.performanceLevel" placeholder="绩效等级" clearable>
            <el-option label="A(优秀)" value="A"></el-option>
            <el-option label="B(良好)" value="B"></el-option>
            <el-option label="C(合格)" value="C"></el-option>
            <el-option label="D(待改进)" value="D"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="employeeId" label="工号" width="90"></el-table-column>
      <el-table-column prop="employeeName" label="姓名"></el-table-column>
      <el-table-column prop="department" label="部门"></el-table-column>
      <el-table-column prop="position" label="职位"></el-table-column>
      <el-table-column prop="month" label="考核月份" width="110"></el-table-column>

      <el-table-column label="任务完成情况" width="180">
        <template #default="scope">
          <div class="task-stats">
            <span>总数: {{ scope.row.totalTasks || 0 }}</span>
            <span style="margin-left: 10px;">完成: {{ scope.row.completedTasks || 0 }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="qualityScore" label="质量得分" width="100">
        <template #default="scope">
          <el-rate
              v-model="scope.row.qualityScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}分">
          </el-rate>
        </template>
      </el-table-column>

      <el-table-column prop="efficiencyScore" label="效率得分" width="100">
        <template #default="scope">
          <el-rate
              v-model="scope.row.efficiencyScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}分">
          </el-rate>
        </template>
      </el-table-column>

      <el-table-column prop="attendanceScore" label="考勤得分" width="100">
        <template #default="scope">
          <el-rate
              v-model="scope.row.attendanceScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}分">
          </el-rate>
        </template>
      </el-table-column>

      <el-table-column prop="totalScore" label="总分" width="100">
        <template #default="scope">
          <span class="total-score">{{ scope.row.totalScore || 0 }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="performanceLevel" label="绩效等级" width="100">
        <template #default="scope">
          <el-tag :type="getLevelType(scope.row.performanceLevel)" effect="dark">
            {{ scope.row.performanceLevel }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="reviewStatus" label="审核状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.reviewStatus)">
            {{ getStatusText(scope.row.reviewStatus) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="handleReview(scope.row)"
              :disabled="scope.row.reviewStatus === 2">
            {{ scope.row.reviewStatus === 0 ? '审核' : '查看' }}
          </el-button>
          <el-button
              size="small"
              type="success"
              @click="generateSinglePerformance(scope.row)"
              :disabled="scope.row.reviewStatus === 2">
            重新生成
          </el-button>
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

    <!-- 绩效审核对话框 -->
    <el-dialog title="绩效审核" v-model="reviewDialogVisible" width="700px">
      <el-form :model="reviewForm" label-width="100px">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="员工姓名">{{ reviewForm.employeeName }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ reviewForm.department }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{ reviewForm.position }}</el-descriptions-item>
          <el-descriptions-item label="考核月份">{{ reviewForm.month }}</el-descriptions-item>
        </el-descriptions>

        <div class="score-section">
          <h4>评分详情</h4>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="score-item">
                <div class="score-label">质量得分</div>
                <div class="score-value">{{ reviewForm.qualityScore }}分</div>
                <el-rate v-model="reviewForm.qualityScore" disabled></el-rate>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="score-item">
                <div class="score-label">效率得分</div>
                <div class="score-value">{{ reviewForm.efficiencyScore }}分</div>
                <el-rate v-model="reviewForm.efficiencyScore" disabled></el-rate>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="score-item">
                <div class="score-label">考勤得分</div>
                <div class="score-value">{{ reviewForm.attendanceScore }}分</div>
                <el-rate v-model="reviewForm.attendanceScore" disabled></el-rate>
              </div>
            </el-col>
          </el-row>

          <div class="total-score-display">
            <span class="total-label">总分：</span>
            <span class="total-value">{{ reviewForm.totalScore }}分</span>
            <el-tag :type="getLevelType(reviewForm.performanceLevel)" size="large" style="margin-left: 20px;">
              绩效等级：{{ reviewForm.performanceLevel }}
            </el-tag>
          </div>
        </div>

        <div class="task-details" v-if="reviewForm.taskDetails">
          <h4>任务完成情况</h4>
          <el-table :data="reviewForm.taskDetails" border size="small">
            <el-table-column prop="taskName" label="任务名称" width="180"></el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag size="small" :type="scope.row.status === '已完成' ? 'success' : 'warning'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quality" label="质量评分" width="80">
              <template #default="scope">
                <el-rate v-model="scope.row.quality" disabled size="small"></el-rate>
              </template>
            </el-table-column>
            <el-table-column prop="completionTime" label="完成时间" width="120"></el-table-column>
            <el-table-column prop="remarks" label="备注"></el-table-column>
          </el-table>
        </div>

        <el-form-item label="经理评语" v-if="reviewForm.reviewStatus === 0">
          <el-input
              v-model="reviewForm.reviewComment"
              type="textarea"
              :rows="3"
              placeholder="请输入评语...">
          </el-input>
        </el-form-item>

        <el-form-item label="审核结果" v-if="reviewForm.reviewStatus === 0">
          <el-radio-group v-model="reviewForm.reviewResult">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">不通过</el-radio>
          </el-radio-group>
        </el-form-item>

        <div v-if="reviewForm.reviewStatus !== 0" class="review-info">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="审核状态">
              <el-tag :type="getStatusType(reviewForm.reviewStatus)">
                {{ getStatusText(reviewForm.reviewStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ reviewForm.reviewTime }}</el-descriptions-item>
            <el-descriptions-item label="经理评语">{{ reviewForm.reviewComment }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button
              type="primary"
              @click="submitReview"
              v-if="reviewForm.reviewStatus === 0"
              :loading="submitLoading">
            确认审核
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as XLSX from 'xlsx'

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])

// 搜索表单
const searchForm = reactive({
  employeeName: '',
  department: '',
  month: '',
  performanceLevel: ''
})

// 月份选项
const monthOptions = ref([])

// 审核对话框相关
const reviewDialogVisible = ref(false)
const submitLoading = ref(false)

// 审核表单
const reviewForm = reactive({
  id: '',
  employeeName: '',
  department: '',
  position: '',
  month: '',
  qualityScore: 0,
  efficiencyScore: 0,
  attendanceScore: 0,
  totalScore: 0,
  performanceLevel: '',
  reviewStatus: 0,
  reviewResult: 1,
  reviewComment: '',
  reviewTime: '',
  taskDetails: []
})

// 初始化月份选项
const initMonthOptions = () => {
  const currentYear = new Date().getFullYear()
  const currentMonth = new Date().getMonth() + 1

  const options = []
  for (let i = 0; i < 6; i++) {
    const month = currentMonth - i
    const year = currentYear
    let displayMonth = month
    let displayYear = year

    if (month <= 0) {
      displayMonth = month + 12
      displayYear = year - 1
    }

    options.unshift({
      label: `${displayYear}年${displayMonth.toString().padStart(2, '0')}月`,
      value: `${displayYear}-${displayMonth.toString().padStart(2, '0')}`
    })
  }

  monthOptions.value = options
  searchForm.month = options[options.length - 1].value
}

// 生命周期钩子
onMounted(() => {
  initMonthOptions()
  fetchData()
})

// 获取绩效列表
const fetchData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))

    // 生成模拟数据
    const mockData = generateMockPerformanceData()

    // 应用搜索过滤
    let filteredData = mockData

    if (searchForm.employeeName) {
      filteredData = filteredData.filter(item =>
          item.employeeName.includes(searchForm.employeeName)
      )
    }

    if (searchForm.department) {
      filteredData = filteredData.filter(item =>
          item.department === searchForm.department
      )
    }

    if (searchForm.month) {
      filteredData = filteredData.filter(item =>
          item.month === searchForm.month
      )
    }

    if (searchForm.performanceLevel) {
      filteredData = filteredData.filter(item =>
          item.performanceLevel === searchForm.performanceLevel
      )
    }

    tableData.value = filteredData
    total.value = filteredData.length
  } catch (error) {
    console.error('获取绩效列表失败:', error)
    ElMessage.error('获取绩效列表失败')
  } finally {
    loading.value = false
  }
}

// 生成模拟绩效数据
const generateMockPerformanceData = () => {
  const departments = ['技术部', '销售部', '市场部', '运营部', '人事部']
  const positions = ['工程师', '销售专员', '市场经理', '运营专员', '人事专员']
  const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']

  const data = []
  const selectedMonth = searchForm.month || monthOptions.value[monthOptions.value.length - 1].value

  for (let i = 1; i <= 30; i++) {
    const qualityScore = Math.floor(Math.random() * 3) + 3 // 3-5分
    const efficiencyScore = Math.floor(Math.random() * 3) + 3
    const attendanceScore = Math.floor(Math.random() * 3) + 3
    const totalScore = qualityScore + efficiencyScore + attendanceScore

    let performanceLevel = 'C'
    if (totalScore >= 13) performanceLevel = 'A'
    else if (totalScore >= 11) performanceLevel = 'B'
    else if (totalScore >= 8) performanceLevel = 'C'
    else performanceLevel = 'D'

    const reviewStatus = Math.random() > 0.3 ? Math.floor(Math.random() * 3) : 0

    data.push({
      id: i,
      employeeId: 'EMP' + (1000 + i),
      employeeName: names[Math.floor(Math.random() * names.length)],
      department: departments[Math.floor(Math.random() * departments.length)],
      position: positions[Math.floor(Math.random() * positions.length)],
      month: selectedMonth,
      totalTasks: Math.floor(Math.random() * 10) + 5,
      completedTasks: Math.floor(Math.random() * 10) + 3,
      qualityScore,
      efficiencyScore,
      attendanceScore,
      totalScore,
      performanceLevel,
      reviewStatus,
      reviewTime: reviewStatus > 0 ? new Date().toLocaleDateString() : ''
    })
  }

  return data
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '审核通过',
    2: '审核不通过'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取等级类型
const getLevelType = (level) => {
  const levelMap = {
    'A': 'success',
    'B': 'primary',
    'C': 'info',
    'D': 'warning'
  }
  return levelMap[level] || 'info'
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.employeeName = ''
  searchForm.department = ''
  searchForm.month = monthOptions.value[monthOptions.value.length - 1].value
  searchForm.performanceLevel = ''
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

// 选择行变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 打开审核对话框
const handleReview = (row) => {
  Object.keys(reviewForm).forEach(key => {
    reviewForm[key] = row[key] || ''
  })

  // 生成模拟任务详情
  reviewForm.taskDetails = generateMockTaskDetails(row.id)

  reviewDialogVisible.value = true
}

// 生成模拟任务详情
const generateMockTaskDetails = (employeeId) => {
  const taskNames = [
    '项目需求分析',
    '系统设计文档编写',
    '代码开发',
    '单元测试',
    '功能测试',
    '性能优化',
    'bug修复',
    '项目部署',
    '用户培训',
    '项目总结'
  ]

  const details = []
  for (let i = 0; i < 5; i++) {
    details.push({
      taskName: taskNames[Math.floor(Math.random() * taskNames.length)],
      status: Math.random() > 0.2 ? '已完成' : '进行中',
      quality: Math.floor(Math.random() * 3) + 3,
      completionTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toLocaleDateString(),
      remarks: Math.random() > 0.5 ? '按时完成，质量良好' : '遇到技术难点，延期完成'
    })
  }

  return details
}

// 提交审核
const submitReview = async () => {
  if (!reviewForm.reviewComment) {
    ElMessage.warning('请填写经理评语')
    return
  }

  submitLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 800))

    // 更新表格数据
    const index = tableData.value.findIndex(item => item.id === reviewForm.id)
    if (index !== -1) {
      tableData.value[index].reviewStatus = reviewForm.reviewResult
      tableData.value[index].reviewTime = new Date().toLocaleString()
    }

    ElMessage.success('审核提交成功')
    reviewDialogVisible.value = false
  } catch (error) {
    console.error('审核提交失败:', error)
    ElMessage.error('审核提交失败')
  } finally {
    submitLoading.value = false
  }
}

// 批量生成绩效
const generatePerformance = () => {
  ElMessageBox.confirm('确定要为所有员工生成本月绩效吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    ElMessage.info('正在生成绩效数据...')

    // 模拟生成过程
    setTimeout(() => {
      fetchData()
      ElMessage.success('绩效生成完成')
    }, 1000)
  }).catch(() => {})
}

// 为单个员工重新生成绩效
const generateSinglePerformance = (row) => {
  ElMessageBox.confirm(`确定要为 ${row.employeeName} 重新生成绩效吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 模拟生成过程
    const index = tableData.value.findIndex(item => item.id === row.id)
    if (index !== -1) {
      // 生成新的模拟数据
      const qualityScore = Math.floor(Math.random() * 3) + 3
      const efficiencyScore = Math.floor(Math.random() * 3) + 3
      const attendanceScore = Math.floor(Math.random() * 3) + 3
      const totalScore = qualityScore + efficiencyScore + attendanceScore

      let performanceLevel = 'C'
      if (totalScore >= 13) performanceLevel = 'A'
      else if (totalScore >= 11) performanceLevel = 'B'
      else if (totalScore >= 8) performanceLevel = 'C'
      else performanceLevel = 'D'

      tableData.value[index].qualityScore = qualityScore
      tableData.value[index].efficiencyScore = efficiencyScore
      tableData.value[index].attendanceScore = attendanceScore
      tableData.value[index].totalScore = totalScore
      tableData.value[index].performanceLevel = performanceLevel
      tableData.value[index].reviewStatus = 0
      tableData.value[index].reviewTime = ''

      ElMessage.success('绩效重新生成完成')
    }
  }).catch(() => {})
}

// 导出绩效数据
const exportPerformance = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可以导出')
    return
  }

  // 准备导出数据
  const exportData = tableData.value.map(item => ({
    '工号': item.employeeId,
    '姓名': item.employeeName,
    '部门': item.department,
    '职位': item.position,
    '考核月份': item.month,
    '总任务数': item.totalTasks,
    '完成任务数': item.completedTasks,
    '质量得分': item.qualityScore,
    '效率得分': item.efficiencyScore,
    '考勤得分': item.attendanceScore,
    '总分': item.totalScore,
    '绩效等级': item.performanceLevel,
    '审核状态': getStatusText(item.reviewStatus),
    '审核时间': item.reviewTime || ''
  }))

  // 创建工作簿
  const wb = XLSX.utils.book_new()
  const ws = XLSX.utils.json_to_sheet(exportData)

  // 设置列宽
  const wscols = [
    { wch: 10 }, { wch: 8 }, { wch: 10 }, { wch: 10 },
    { wch: 12 }, { wch: 10 }, { wch: 10 }, { wch: 10 },
    { wch: 10 }, { wch: 10 }, { wch: 8 }, { wch: 8 },
    { wch: 10 }, { wch: 12 }
  ]
  ws['!cols'] = wscols

  // 添加工作表到工作簿
  XLSX.utils.book_append_sheet(wb, ws, '员工绩效')

  // 生成文件名
  const fileName = `员工绩效_${new Date().toISOString().split('T')[0]}.xlsx`

  // 导出文件
  XLSX.writeFile(wb, fileName)

  ElMessage.success('导出成功')
}
</script>

<style scoped>
.performance-container {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.search-box {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.task-stats {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.total-score {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.score-section {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.score-section h4 {
  margin-bottom: 15px;
  color: #606266;
}

.score-item {
  text-align: center;
  padding: 10px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.score-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.score-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.total-score-display {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  padding: 15px;
  background: white;
  border-radius: 4px;
}

.total-label {
  font-size: 16px;
  color: #606266;
}

.total-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-left: 10px;
}

.task-details {
  margin: 20px 0;
}

.task-details h4 {
  margin-bottom: 10px;
  color: #606266;
}

.review-info {
  margin-top: 20px;
}
</style>