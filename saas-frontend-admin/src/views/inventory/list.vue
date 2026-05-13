<template>
  <div class="inventory-list">
    <h2 class="page-title">库存管理</h2>

    <!-- Search -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="SKU编号">
          <el-input v-model="searchForm.skuId" placeholder="请输入SKU编号" clearable />
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
        <el-table-column prop="skuId" label="SKU编号" width="180" />
        <el-table-column prop="warehouseName" label="仓库" width="150" />
        <el-table-column prop="availableStock" label="可用库存" width="100" />
        <el-table-column prop="lockedStock" label="锁定库存" width="100" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="totalStock" label="总库存" width="100">
          <template #default="{ row }">
            {{ row.availableStock + row.lockedStock }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="row.availableStock <= row.safetyStock ? 'danger' : 'success'"
              size="small"
            >
              {{ row.availableStock <= row.safetyStock ? '库存不足' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAdjust(row)">调整库存</el-button>
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

    <!-- Adjust Dialog -->
    <el-dialog
      v-model="adjustVisible"
      title="库存调整"
      width="480px"
      :close-on-click-modal="false"
      @closed="handleAdjustClose"
    >
      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-width="100px">
        <el-form-item label="SKU编号">
          <span>{{ currentSku?.skuId }}</span>
        </el-form-item>
        <el-form-item label="仓库">
          <span>{{ currentSku?.warehouseName }}</span>
        </el-form-item>
        <el-form-item label="当前可用库存">
          <span>{{ currentSku?.availableStock }}</span>
        </el-form-item>
        <el-form-item label="调整类型" prop="type">
          <el-radio-group v-model="adjustForm.type">
            <el-radio value="in">入库</el-radio>
            <el-radio value="out">出库</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量" prop="quantity">
          <el-input-number v-model="adjustForm.quantity" :min="1" :max="99999" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="adjustForm.remark" type="textarea" :rows="2" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjustLoading" @click="handleAdjustSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInventoryPage, adjustInventory } from '@/api/inventory'

const loading = ref(false)
const tableData = ref([])
const adjustVisible = ref(false)
const adjustLoading = ref(false)
const currentSku = ref(null)
const adjustFormRef = ref(null)

const searchForm = reactive({
  skuId: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const adjustForm = reactive({
  type: 'in',
  quantity: 1,
  remark: ''
})

const adjustRules = {
  quantity: [{ required: true, message: '请输入调整数量', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      skuId: searchForm.skuId
    }
    const res = await getInventoryPage(params)
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
  searchForm.skuId = ''
  pagination.page = 1
  loadData()
}

const handleAdjust = (row) => {
  currentSku.value = row
  adjustForm.type = 'in'
  adjustForm.quantity = 1
  adjustForm.remark = ''
  adjustVisible.value = true
}

const handleAdjustSubmit = async () => {
  const valid = await adjustFormRef.value.validate().catch(() => false)
  if (!valid) return

  adjustLoading.value = true
  try {
    await adjustInventory({
      skuId: currentSku.value.skuId,
      type: adjustForm.type,
      quantity: adjustForm.quantity,
      remark: adjustForm.remark
    })
    ElMessage.success('库存调整成功')
    adjustVisible.value = false
    loadData()
  } catch (e) {
    // Error handled by interceptor
  } finally {
    adjustLoading.value = false
  }
}

const handleAdjustClose = () => {
  adjustFormRef.value?.resetFields()
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
