<template>
  <div class="member-list">
    <h2 class="page-title">会员管理</h2>

    <!-- Search -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="会员编号/用户名">
          <el-input v-model="searchForm.keyword" placeholder="请输入会员编号或用户名" clearable />
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="searchForm.level" placeholder="全部等级" clearable>
            <el-option label="普通会员" :value="1" />
            <el-option label="银卡会员" :value="2" />
            <el-option label="金卡会员" :value="3" />
            <el-option label="钻石会员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card shadow="never" style="margin-top: 16px">
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="memberNo" label="会员编号" width="140" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="level" label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="levelTypeMap[row.level]" size="small">
              {{ levelTextMap[row.level] || row.level }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="joinTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="会员详情" width="600px">
      <template v-if="currentMember">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="会员ID">{{ currentMember.id }}</el-descriptions-item>
          <el-descriptions-item label="会员编号">{{ currentMember.memberNo }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentMember.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentMember.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="等级">
            <el-tag :type="levelTypeMap[currentMember.level]" size="small">
              {{ levelTextMap[currentMember.level] || currentMember.level }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="积分">{{ currentMember.points }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentMember.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentMember.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ currentMember.joinTime }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ currentMember.lastLoginTime || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMemberPage, getMemberDetail } from '@/api/member'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentMember = ref(null)

const searchForm = reactive({
  keyword: '',
  level: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const levelTypeMap = {
  1: 'info',
  2: 'warning',
  3: 'primary',
  4: 'danger'
}

const levelTextMap = {
  1: '普通会员',
  2: '银卡会员',
  3: '金卡会员',
  4: '钻石会员'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      level: searchForm.level
    }
    const res = await getMemberPage(params)
    tableData.value = res.data?.records ?? res.data?.list ?? []
    pagination.total = res.data?.total ?? 0
  } catch (e) {
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.level = null
  pagination.page = 1
  loadData()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getMemberDetail(row.id)
    currentMember.value = res.data
    detailVisible.value = true
  } catch (e) {
    // Error handled by interceptor
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 16px;
  font-size: 20px;
  color: #303133;
}

.search-card {
  background: #fff;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
