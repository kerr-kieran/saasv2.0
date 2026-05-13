<template>
  <div class="user-center-page">
    <div class="uc-layout">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="user-avatar">
          <div class="avatar-icon">&#128100;</div>
          <p class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</p>
        </div>
        <nav class="sidebar-nav">
          <a
            :class="['nav-item', { active: activeTab === 'profile' }]"
            @click="activeTab = 'profile'"
          >
            个人资料
          </a>
          <a
            :class="['nav-item', { active: activeTab === 'address' }]"
            @click="activeTab = 'address'"
          >
            收货地址
          </a>
          <router-link to="/order" class="nav-item">我的订单</router-link>
          <a class="nav-item logout" @click="handleLogout">退出登录</a>
        </nav>
      </aside>

      <!-- 主内容区 -->
      <main class="content">
        <!-- 个人资料 -->
        <section v-if="activeTab === 'profile'" class="section">
          <h3 class="section-title">个人资料</h3>
          <div v-if="loadingProfile" class="loading-text">加载中...</div>
          <form v-else @submit.prevent="saveProfile" class="profile-form">
            <div class="form-group">
              <label>昵称</label>
              <input v-model="profile.nickname" placeholder="请输入昵称" />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input v-model="profile.email" placeholder="请输入邮箱" />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input v-model="profile.phone" placeholder="请输入手机号" />
            </div>
            <button type="submit" class="btn-save">保存</button>
          </form>
        </section>

        <!-- 收货地址 -->
        <section v-if="activeTab === 'address'" class="section">
          <div class="section-header">
            <h3 class="section-title">收货地址</h3>
            <button class="btn-add" @click="openAddressForm()">新增地址</button>
          </div>
          <div v-if="loadingAddress" class="loading-text">加载中...</div>
          <div v-else-if="addresses.length === 0" class="empty-text">暂无收货地址</div>
          <div v-else class="address-list">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="addr-card"
            >
              <div class="addr-info">
                <span class="contact">{{ addr.recipientName }}</span>
                <span class="phone">{{ addr.phone }}</span>
                <span v-if="addr.isDefault" class="default-tag">默认</span>
                <p>{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</p>
              </div>
              <div class="addr-actions">
                <button class="btn-edit" @click="openAddressForm(addr)">编辑</button>
                <button class="btn-del" @click="handleDeleteAddress(addr.id)">删除</button>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- 地址编辑弹层 -->
    <div v-if="showAddressForm" class="modal-overlay" @click.self="showAddressForm = false">
      <div class="address-modal">
        <h4>{{ editingAddress ? '编辑地址' : '新增地址' }}</h4>
        <div class="form-group">
          <label>收件人</label>
          <input v-model="addrForm.recipientName" placeholder="请输入收件人姓名" />
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input v-model="addrForm.phone" placeholder="请输入手机号" />
        </div>
        <div class="form-group">
          <label>省/市/区</label>
          <div class="region-inputs">
            <input v-model="addrForm.province" placeholder="省" />
            <input v-model="addrForm.city" placeholder="市" />
            <input v-model="addrForm.district" placeholder="区" />
          </div>
        </div>
        <div class="form-group">
          <label>详细地址</label>
          <input v-model="addrForm.detail" placeholder="街道、门牌号等" />
        </div>
        <div class="form-actions">
          <button class="btn-save" @click="saveAddress">保存</button>
          <button class="btn-cancel" @click="showAddressForm = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getProfile, getAddresses, addAddress, updateAddress, deleteAddress } from '@/api/member'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('profile')

// 个人资料
const loadingProfile = ref(false)
const profile = reactive({
  nickname: '',
  email: '',
  phone: ''
})

// 地址管理
const loadingAddress = ref(false)
const addresses = ref([])
const showAddressForm = ref(false)
const editingAddress = ref(null)
const addrForm = reactive({
  recipientName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: ''
})

onMounted(async () => {
  userStore.fetchUserInfo()
  loadProfile()
})

// 切换到地址标签时加载地址
watch(activeTab, (val) => {
  if (val === 'address') {
    loadAddresses()
  }
})

async function loadProfile() {
  loadingProfile.value = true
  try {
    const res = await getProfile()
    const data = res.data
    Object.assign(profile, {
      nickname: data.nickname || userStore.userInfo?.nickname || '',
      email: data.email || userStore.userInfo?.email || '',
      phone: data.phone || userStore.userInfo?.phone || ''
    })
  } catch {
    // 使用 store 中的数据
  } finally {
    loadingProfile.value = false
  }
}

function saveProfile() {
  // 预留：可调用 updateProfile API
  alert('资料保存功能待实现')
}

async function loadAddresses() {
  loadingAddress.value = true
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
  } catch {
    addresses.value = []
  } finally {
    loadingAddress.value = false
  }
}

function openAddressForm(addr = null) {
  editingAddress.value = addr
  if (addr) {
    Object.assign(addrForm, {
      recipientName: addr.recipientName || '',
      phone: addr.phone || '',
      province: addr.province || '',
      city: addr.city || '',
      district: addr.district || '',
      detail: addr.detail || ''
    })
  } else {
    Object.assign(addrForm, {
      recipientName: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: ''
    })
  }
  showAddressForm.value = true
}

async function saveAddress() {
  if (!addrForm.recipientName || !addrForm.phone || !addrForm.detail) {
    alert('请完善地址信息')
    return
  }
  try {
    if (editingAddress.value) {
      await updateAddress(editingAddress.value.id, { ...addrForm })
    } else {
      await addAddress({ ...addrForm })
    }
    showAddressForm.value = false
    loadAddresses()
  } catch (e) {
    alert(e.message || '保存失败')
  }
}

async function handleDeleteAddress(id) {
  if (!confirm('确定删除该地址？')) return
  try {
    await deleteAddress(id)
    loadAddresses()
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.uc-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.user-avatar {
  text-align: center;
  padding: 28px 16px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.user-name {
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.sidebar-nav {
  padding: 8px 0;
}

.nav-item {
  display: block;
  padding: 12px 20px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-item:hover {
  background: #fafafa;
  color: #e4393c;
}

.nav-item.active {
  color: #e4393c;
  background: #fff5f5;
  border-right: 3px solid #e4393c;
}

.nav-item.logout {
  margin-top: 12px;
  border-top: 1px solid #f0f0f0;
  color: #999;
}

.content {
  flex: 1;
  min-width: 0;
}

.section {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.btn-add {
  padding: 6px 16px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.profile-form {
  max-width: 400px;
}

.profile-form .form-group {
  margin-bottom: 16px;
}

.profile-form label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #666;
}

.profile-form input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.profile-form input:focus {
  border-color: #e4393c;
}

.btn-save {
  padding: 8px 28px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.loading-text,
.empty-text {
  color: #999;
  font-size: 14px;
  padding: 20px 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.addr-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
}

.contact {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-right: 12px;
}

.phone {
  font-size: 13px;
  color: #666;
}

.default-tag {
  display: inline-block;
  padding: 1px 8px;
  font-size: 11px;
  background: #e4393c;
  color: #fff;
  border-radius: 3px;
  margin-left: 8px;
}

.addr-info p {
  margin-top: 4px;
  font-size: 13px;
  color: #666;
}

.addr-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.btn-edit {
  padding: 4px 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 12px;
}

.btn-edit:hover {
  border-color: #e4393c;
  color: #e4393c;
}

.btn-del {
  padding: 4px 14px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  color: #999;
  cursor: pointer;
  font-size: 12px;
}

.btn-del:hover {
  border-color: #e4393c;
  color: #e4393c;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}

.address-modal {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  width: 520px;
  max-height: 80vh;
  overflow-y: auto;
}

.address-modal h4 {
  margin-bottom: 16px;
  font-size: 16px;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  font-size: 13px;
  color: #666;
}

.form-group input {
  width: 100%;
  height: 38px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.form-group input:focus {
  border-color: #e4393c;
}

.region-inputs {
  display: flex;
  gap: 2%;
}

.region-inputs input {
  width: 32%;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.form-actions .btn-save {
  flex: 1;
  height: 40px;
}

.btn-cancel {
  flex: 1;
  height: 40px;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

@media (max-width: 768px) {
  .uc-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }
}
</style>
