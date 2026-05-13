<template>
  <div class="system-permission">
    <h2 class="page-title">权限管理</h2>

    <!-- Table -->
    <el-card shadow="never">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd(null)">新增权限</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        row-key="id"
        default-expand-all
      >
        <el-table-column prop="name" label="权限名称" min-width="200" />
        <el-table-column prop="code" label="权限编码" width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'primary' : row.type === 2 ? 'success' : 'warning'" size="small">
              {{ typeTextMap[row.type] || row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAdd(row)">新增子级</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="上级权限">
          <span>{{ parentName || '根级权限' }}</span>
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
            <el-option label="接口" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径（菜单类型必填）" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionPage, createPermission, updatePermission, deletePermission } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const editId = ref(null)
const parentId = ref(null)
const parentName = ref('')

const form = reactive({
  name: '',
  code: '',
  type: 1,
  path: '',
  sort: 0
})

const formRules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const typeTextMap = {
  1: '菜单',
  2: '按钮',
  3: '接口'
}

const dialogTitle = computed(() => (editId.value ? '编辑权限' : '新增权限'))

// Convert flat list to tree
const buildTree = (list, parentId = 0) => {
  return list
    .filter(item => item.parentId === parentId)
    .sort((a, b) => (a.sort || 0) - (b.sort || 0))
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPermissionPage({ page: 1, pageSize: 1000 })
    const list = res.data?.records ?? res.data?.list ?? []
    tableData.value = buildTree(list)
  } catch (e) {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

const handleAdd = (row) => {
  editId.value = null
  parentId.value = row?.id ?? 0
  parentName.value = row?.name ?? ''
  form.name = ''
  form.code = ''
  form.type = 1
  form.path = ''
  form.sort = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  editId.value = row.id
  parentName.value = row.parentName || '根级权限'
  form.name = row.name
  form.code = row.code
  form.type = row.type
  form.path = row.path || ''
  form.sort = row.sort ?? 0
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      ...form,
      parentId: parentId.value
    }
    if (editId.value) {
      await updatePermission(editId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createPermission(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // Error handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(
    `确定要删除权限"${row.name}"吗？子权限将一并删除。`,
    '警告',
    { type: 'warning' }
  )
  try {
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // Error handled by interceptor
  }
}

const handleDialogClosed = () => {
  formRef.value?.resetFields()
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

.table-header {
  margin-bottom: 16px;
}
</style>
