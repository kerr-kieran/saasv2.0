<template>
  <div class="order-page">
    <h2 class="page-title">我的订单</h2>

    <!-- 标签筛选 -->
    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-item', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key; page = 1; loadOrders()"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 订单列表 -->
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="orders.length === 0" class="empty">暂无订单</div>
    <div v-else class="order-list">
      <div
        v-for="order in orders"
        :key="order.id || order.orderNo"
        class="order-card"
        @click="goDetail(order)"
      >
        <div class="order-header">
          <span class="order-no">{{ order.orderNo }}</span>
          <span :class="['order-status', statusClass(order.status)]">
            {{ statusLabel(order.status) }}
          </span>
        </div>
        <div class="order-items">
          <div
            v-for="item in (order.items || order.orderItems || [])"
            :key="item.id || item.skuId"
            class="order-item"
          >
            <div class="item-image">
              <div class="img-placeholder">{{ (item.spuName || item.productName)?.charAt(0) }}</div>
            </div>
            <div class="item-info">
              <p class="item-name">{{ item.spuName || item.productName }}</p>
              <p class="item-meta">
                ¥{{ item.price }} x {{ item.quantity }}
              </p>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">合计: ¥{{ (order.totalAmount || order.totalPrice)?.toFixed(2) }}</span>
          <div class="order-actions" @click.stop>
            <button
              v-if="order.status === 'PENDING'"
              class="btn-cancel"
              @click="handleCancel(order)"
            >
              取消订单
            </button>
            <button class="btn-detail" @click="goDetail(order)">查看详情</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <button :disabled="page === 1" @click="page--; loadOrders()" class="page-btn">上一页</button>
      <span class="page-info">{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
      <button
        :disabled="page >= Math.ceil(total / pageSize)"
        @click="page++; loadOrders()"
        class="page-btn"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderPage, cancelOrder } from '@/api/order'

const router = useRouter()

const tabs = [
  { key: '', label: '全部' },
  { key: 'PENDING', label: '待支付' },
  { key: 'PAID', label: '已支付' },
  { key: 'SHIPPED', label: '已发货' },
  { key: 'COMPLETED', label: '已完成' },
  { key: 'CANCELLED', label: '已取消' }
]

const activeTab = ref('')
const loading = ref(false)
const orders = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (activeTab.value) {
      params.status = activeTab.value
    }
    const res = await getOrderPage(params)
    orders.value = res.data?.records || res.data?.list || res.data || []
    total.value = res.data?.total || 0
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function statusLabel(status) {
  const map = {
    PENDING: '待支付',
    PAID: '已支付',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status || '未知'
}

function statusClass(status) {
  const map = {
    PENDING: 's-pending',
    PAID: 's-paid',
    SHIPPED: 's-shipped',
    COMPLETED: 's-completed',
    CANCELLED: 's-cancelled'
  }
  return map[status] || ''
}

function goDetail(order) {
  router.push(`/order/${order.id || order.orderId}`)
}

async function handleCancel(order) {
  if (!confirm('确认取消该订单？')) return
  try {
    await cancelOrder(order.id || order.orderId)
    loadOrders()
  } catch (e) {
    alert(e.message || '取消失败')
  }
}
</script>

<style scoped>
.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.tabs {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-item {
  padding: 10px 24px;
  border: none;
  background: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.tab-item:hover {
  color: #e4393c;
}

.tab-item.active {
  color: #e4393c;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 2px;
  background: #e4393c;
}

.loading,
.empty {
  text-align: center;
  padding: 80px 0;
  color: #999;
  font-size: 16px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
  font-size: 13px;
}

.order-no {
  color: #999;
}

.order-status {
  font-weight: 600;
}

.s-pending { color: #e6a23c; }
.s-paid { color: #409eff; }
.s-shipped { color: #67c23a; }
.s-completed { color: #67c23a; }
.s-cancelled { color: #999; }

.order-items {
  padding: 12px 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-image {
  width: 56px;
  height: 56px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.img-placeholder {
  font-size: 20px;
  font-weight: 700;
  color: #ddd;
}

.item-name {
  font-size: 13px;
  color: #333;
  margin-bottom: 2px;
}

.item-meta {
  font-size: 12px;
  color: #999;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
}

.order-total {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.btn-detail {
  padding: 6px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
}

.btn-detail:hover {
  border-color: #e4393c;
  color: #e4393c;
}

.btn-cancel {
  padding: 6px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  color: #999;
  cursor: pointer;
  font-size: 13px;
}

.btn-cancel:hover {
  border-color: #e4393c;
  color: #e4393c;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 20px 0;
}

.page-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
}

.page-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}
</style>
