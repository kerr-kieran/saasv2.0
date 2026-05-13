<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in statCards" :key="item.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-icon" :style="{ background: item.bg }">
              <el-icon :size="28" color="#fff">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>欢迎使用 SaaS Admin</span>
          </template>
          <p style="line-height: 2; color: #606266">
            SaaS 电商管理后台，提供商品管理、订单管理、库存管理、会员管理等功能。
            使用 Vue 3 + Element Plus 构建。
          </p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Odometer, Goods, UserFilled, Coin } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/dashboard'

const statCards = ref([
  { key: 'orders', label: '今日订单', value: 0, icon: Odometer, bg: '#409EFF' },
  { key: 'products', label: '商品总数', value: 0, icon: Goods, bg: '#67C23A' },
  { key: 'members', label: '会员总数', value: 0, icon: UserFilled, bg: '#E6A23C' },
  { key: 'revenue', label: '今日营收', value: '¥0', icon: Coin, bg: '#F56C6C' }
])

const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      statCards.value[0].value = res.data.todayOrders ?? 0
      statCards.value[1].value = res.data.totalProducts ?? 0
      statCards.value[2].value = res.data.totalMembers ?? 0
      statCards.value[3].value = '¥' + (res.data.todayRevenue ?? 0).toLocaleString()
    }
  } catch (e) {
    // Use mock data when API is not available
    statCards.value[0].value = 128
    statCards.value[1].value = 1560
    statCards.value[2].value = 3850
    statCards.value[3].value = '¥12,800'
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
  font-size: 20px;
  color: #303133;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-card-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style>
