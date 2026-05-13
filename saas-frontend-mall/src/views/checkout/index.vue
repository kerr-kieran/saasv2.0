<template>
  <div class="checkout-page">
    <h2 class="page-title">确认订单</h2>

    <!-- 收货地址 -->
    <section class="section address-section">
      <h3 class="section-title">收货地址</h3>
      <div v-if="loadingAddress" class="loading-text">加载中...</div>
      <div v-else-if="selectedAddress" class="selected-address">
        <div class="address-info">
          <span class="contact">{{ selectedAddress.recipientName }}</span>
          <span class="phone">{{ selectedAddress.phone }}</span>
          <span v-if="selectedAddress.isDefault" class="default-tag">默认</span>
          <p class="full-address">
            {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}
            {{ selectedAddress.detail }}
          </p>
        </div>
        <button class="btn-change" @click="showAddressPicker = true">更换地址</button>
      </div>
      <div v-else class="no-address">
        <p>暂无收货地址，请先添加</p>
        <button class="btn-add-address" @click="showAddressForm = true">新增地址</button>
      </div>

      <!-- 地址选择弹层 -->
      <div v-if="showAddressPicker" class="modal-overlay" @click.self="showAddressPicker = false">
        <div class="address-picker">
          <h4>选择收货地址</h4>
          <div
            v-for="addr in addresses"
            :key="addr.id"
            :class="['addr-item', { active: selectedAddress?.id === addr.id }]"
            @click="selectAddress(addr)"
          >
            <div>
              <span class="contact">{{ addr.recipientName }}</span>
              <span class="phone">{{ addr.phone }}</span>
              <span v-if="addr.isDefault" class="default-tag">默认</span>
              <p>{{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}</p>
            </div>
          </div>
          <button class="btn-new-addr" @click="showAddressForm = true; showAddressPicker = false">
            新增地址
          </button>
          <button class="btn-close" @click="showAddressPicker = false">关闭</button>
        </div>
      </div>

      <!-- 新增地址表单 -->
      <div v-if="showAddressForm" class="modal-overlay" @click.self="showAddressForm = false">
        <div class="address-form-modal">
          <h4>新增收货地址</h4>
          <div class="form-group">
            <label>收件人</label>
            <input v-model="newAddress.recipientName" placeholder="请输入收件人姓名" />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="newAddress.phone" placeholder="请输入手机号" />
          </div>
          <div class="form-group">
            <label>省/市/区</label>
            <input v-model="newAddress.province" placeholder="省" style="width:30%;margin-right:2%" />
            <input v-model="newAddress.city" placeholder="市" style="width:30%;margin-right:2%" />
            <input v-model="newAddress.district" placeholder="区" style="width:34%" />
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <input v-model="newAddress.detail" placeholder="街道、门牌号等" />
          </div>
          <div class="form-actions">
            <button class="btn-save" @click="saveAddress">保存</button>
            <button class="btn-cancel" @click="showAddressForm = false">取消</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 商品清单 -->
    <section class="section order-items-section">
      <h3 class="section-title">商品清单</h3>
      <div class="order-items">
        <div
          v-for="item in cartStore.checkedItems"
          :key="item.skuId"
          class="order-item"
        >
          <div class="item-image">
            <div class="img-placeholder">{{ item.spuName?.charAt(0) }}</div>
          </div>
          <div class="item-info">
            <p class="item-name">{{ item.spuName }}</p>
            <p class="item-spec">规格: {{ item.skuSpec }}</p>
          </div>
          <div class="item-price">¥{{ item.price }}</div>
          <div class="item-qty">x{{ item.quantity }}</div>
          <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>
    </section>

    <!-- 订单合计 -->
    <section class="section summary-section">
      <div class="summary-row">
        <span>商品件数:</span>
        <span>{{ cartStore.selectedTotalCount }} 件</span>
      </div>
      <div class="summary-row total">
        <span>应付总额:</span>
        <span class="total-price">¥{{ cartStore.selectedTotalPrice.toFixed(2) }}</span>
      </div>
    </section>

    <!-- 提交 -->
    <div class="submit-bar">
      <button
        class="btn-place-order"
        :disabled="!selectedAddress || submitting"
        @click="handlePlaceOrder"
      >
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { getAddresses, addAddress } from '@/api/member'
import { createOrder } from '@/api/order'

const router = useRouter()
const cartStore = useCartStore()

const loadingAddress = ref(false)
const addresses = ref([])
const selectedAddress = ref(null)
const showAddressPicker = ref(false)
const showAddressForm = ref(false)
const submitting = ref(false)

const newAddress = reactive({
  recipientName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: ''
})

onMounted(async () => {
  if (cartStore.checkedItems.length === 0) {
    router.push('/cart')
    return
  }
  await loadAddresses()
})

async function loadAddresses() {
  loadingAddress.value = true
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
    // 优先选默认地址
    const defaultAddr = addresses.value.find((a) => a.isDefault)
    selectedAddress.value = defaultAddr || (addresses.value.length > 0 ? addresses.value[0] : null)
  } catch {
    addresses.value = []
  } finally {
    loadingAddress.value = false
  }
}

function selectAddress(addr) {
  selectedAddress.value = addr
  showAddressPicker.value = false
}

async function saveAddress() {
  if (!newAddress.recipientName || !newAddress.phone || !newAddress.detail) {
    alert('请完善地址信息')
    return
  }
  try {
    const res = await addAddress({ ...newAddress })
    showAddressForm.value = false
    // 清空表单
    Object.assign(newAddress, {
      recipientName: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: ''
    })
    await loadAddresses()
    // 选中新增的地址
    if (res.data?.id) {
      const added = addresses.value.find((a) => a.id === res.data.id)
      if (added) selectedAddress.value = added
    }
  } catch (e) {
    alert(e.message || '保存失败')
  }
}

async function handlePlaceOrder() {
  if (!selectedAddress.value) {
    alert('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    await createOrder({
      addressId: selectedAddress.value.id,
      items: cartStore.checkedItems.map((item) => ({
        skuId: item.skuId,
        spuId: item.spuId,
        quantity: item.quantity,
        price: item.price
      }))
    })
    // 清除已购买的商品
    cartStore.clearCheckedItems()
    router.push('/order')
  } catch (e) {
    alert(e.message || '下单失败')
  } finally {
    submitting.value = false
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

.section {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.selected-address {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.contact {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-right: 16px;
}

.phone {
  font-size: 14px;
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

.full-address {
  margin-top: 4px;
  font-size: 13px;
  color: #666;
}

.btn-change,
.btn-add-address,
.btn-new-addr {
  padding: 6px 16px;
  border: 1px solid #e4393c;
  background: #fff;
  color: #e4393c;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  flex-shrink: 0;
}

.no-address {
  text-align: center;
  padding: 20px 0;
  color: #999;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.item-image {
  width: 60px;
  height: 60px;
  background: #f0f0f0;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.img-placeholder {
  font-size: 22px;
  font-weight: 700;
  color: #ddd;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #333;
}

.item-spec {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.item-price {
  font-size: 14px;
  color: #333;
  width: 80px;
  text-align: center;
}

.item-qty {
  font-size: 14px;
  color: #666;
  width: 60px;
  text-align: center;
}

.item-subtotal {
  font-size: 14px;
  color: #e4393c;
  font-weight: 600;
  width: 100px;
  text-align: right;
}

.summary-section {
  text-align: right;
}

.summary-row {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  padding: 6px 0;
  font-size: 14px;
  color: #666;
}

.summary-row.total {
  font-size: 16px;
  color: #333;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.total-price {
  font-size: 24px;
  color: #e4393c;
  font-weight: 700;
}

.submit-bar {
  text-align: right;
  margin-top: 8px;
}

.btn-place-order {
  height: 48px;
  padding: 0 48px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-place-order:hover {
  background: #d42e2e;
}

.btn-place-order:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading-text {
  color: #999;
  font-size: 14px;
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

.address-picker,
.address-form-modal {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  width: 520px;
  max-height: 70vh;
  overflow-y: auto;
}

.address-picker h4,
.address-form-modal h4 {
  margin-bottom: 16px;
  font-size: 16px;
}

.addr-item {
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  margin-bottom: 10px;
  cursor: pointer;
  font-size: 14px;
  transition: border-color 0.2s;
}

.addr-item:hover,
.addr-item.active {
  border-color: #e4393c;
}

.btn-close,
.btn-cancel {
  width: 100%;
  margin-top: 12px;
  padding: 10px 0;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
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

.form-actions {
  display: flex;
  gap: 12px;
}

.btn-save {
  flex: 1;
  height: 40px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel {
  flex: 1;
  height: 40px;
  margin-top: 0;
}
</style>
