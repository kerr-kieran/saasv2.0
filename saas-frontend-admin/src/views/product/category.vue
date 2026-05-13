<template>
  <div class="category-page">
    <h2 class="page-title">分类管理</h2>

    <el-row :gutter="20">
      <!-- Tree -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>分类树</span>
              <el-button type="primary" size="small" @click="handleAdd(null)">
                新增根分类
              </el-button>
            </div>
          </template>
          <el-tree
            ref="treeRef"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            default-expand-all
            highlight-current
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ data.name }}</span>
                <span class="tree-node-actions">
                  <el-button type="primary" link size="small" @click.stop="handleAdd(data)">
                    新增子分类
                  </el-button>
                  <el-button type="primary" link size="small" @click.stop="handleEdit(data)">
                    编辑
                  </el-button>
                  <el-button type="danger" link size="small" @click.stop="handleDelete(data)">
                    删除
                  </el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- Info -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>分类信息</span>
          </template>
          <el-empty v-if="!currentNode" description="请在左侧选择分类" />
          <el-descriptions v-else :column="1" border>
            <el-descriptions-item label="分类名称">{{ currentNode.name }}</el-descriptions-item>
            <el-descriptions-item label="排序">{{ currentNode.sort }}</el-descriptions-item>
            <el-descriptions-item label="层级">{{ currentNode.level }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <!-- Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="480px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
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
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '@/api/system'

const treeRef = ref(null)
const treeData = ref([])
const currentNode = ref(null)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const editId = ref(null)
const parentId = ref(null)

const treeProps = {
  children: 'children',
  label: 'name'
}

const form = reactive({
  name: '',
  sort: 0
})

const formRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => (editId.value ? '编辑分类' : '新增分类'))

const loadTree = async () => {
  try {
    const res = await getCategoryTree()
    treeData.value = res.data ?? []
  } catch (e) {
    treeData.value = []
  }
}

const handleAdd = (data) => {
  editId.value = null
  parentId.value = data?.id ?? 0
  form.name = ''
  form.sort = 0
  dialogVisible.value = true
}

const handleEdit = (data) => {
  editId.value = data.id
  form.name = data.name
  form.sort = data.sort ?? 0
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (editId.value) {
      await updateCategory(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createCategory({ ...form, parentId: parentId.value })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    currentNode.value = null
    loadTree()
  } catch (e) {
    // Error handled by interceptor
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (data) => {
  await ElMessageBox.confirm(
    `确定要删除分类"${data.name}"吗？若有子分类将一并删除。`,
    '警告',
    { type: 'warning' }
  )
  try {
    await deleteCategory(data.id)
    ElMessage.success('删除成功')
    currentNode.value = null
    loadTree()
  } catch (e) {
    // Error handled by interceptor
  }
}

const handleDialogClosed = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadTree()
})
</script>

<style scoped>
.page-title {
  margin-bottom: 16px;
  font-size: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 8px;
}

.tree-node-actions {
  opacity: 0;
  transition: opacity 0.2s;
}

.tree-node:hover .tree-node-actions {
  opacity: 1;
}
</style>
