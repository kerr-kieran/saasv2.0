<template>
  <div class="product-detail-page" v-if="product">
    <!-- 面包屑 -->
    <div class="breadcrumb">
      <router-link to="/">首页</router-link>
      <span class="sep">&gt;</span>
      <span v-if="product.categoryName">{{ product.categoryName }}</span>
      <span class="sep">&gt;</span>
      <span class="current">{{ product.spuName }}</span>
    </div>

    <!-- 商品主体 -->
    <div class="product-main">
      <div class="product-image-section">
        <div class="main-image">
          <div class="img-placeholder">{{ product.spuName?.charAt(0) || '商' }}</div>
        </div>
      </div>

      <div class="product-info-section">
        <h1 class="product-name">{{ product.spuName }}</h1>
        <p class="product-desc" v-if="product.description">{{ product.description }}</p>

        <div class="price-box">
          <span class="price-label">价格</span>
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ product.price }}</span>
        </div>

        <!-- SKU 选择 -->
        <div class="sku-section" v-if="skus && skus.length > 1">
          <h3 class="section-label">规格</h3>
          <div class="sku-buttons">
            <button
              v-for="sku in skus"
              :key="sku.id"
              :class="['sku-btn', { active: selectedSku?.id === sku.id }]"
              @click="selectedSku = sku"
            >
              {{ sku.spec }}
            </button>
          </div>
        </div>

        <!-- 数量 -->
        <div class="quantity-section">
          <h3 class="section-label">数量</h3>
          <div class="quantity-control">
            <button @click="quantity > 1 && quantity--" :disabled="quantity <= 1">-</button>
            <input type="number" v-model.number="quantity" min="1" />
            <button @click="quantity++">+</button>
          </div>
          <span class="stock-info" v-if="currentStock">库存 {{ currentStock }} 件</span>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="btn-add-cart" @click="handleAddToCart">加入购物车</button>
          <button class="btn-buy-now" @click="handleBuyNow">立即购买</button>
        </div>
      </div>
    </div>

    <!-- 商品描述 -->
    <section class="product-description">
      <h2 class="desc-title">商品详情</h2>
      <div class="desc-content">
        <p v-if="product.detail">{{ product.detail }}</p>
        <p v-else-if="product.description">{{ product.description }}</p>
        <p v-else>暂无详细描述</p>
      </div>
    </section>
  </div>

  <div v-else-if="loading" class="loading">加载中...</div>
  <div v-else class="not-found">商品不存在</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { useCartStore } from '@/store/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const product = ref(null)
const skus = ref([])
const selectedSku = ref(null)
const quantity = ref(1)

const currentStock = computed(() => {
  return selectedSku.value?.stock ?? product.value?.stock ?? null
})

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await getProductDetail(id)
    const data = res.data
    product.value = data.spu || data
    skus.value = data.skus || data.skuList || []
    if (skus.value.length > 0) {
      selectedSku.value = skus.value[0]
    }
  } catch {
    product.value = null
  } finally {
    loading.value = false
  }
})

function getCurrentSkuInfo() {
  const sku = selectedSku.value
  return {
    skuId: sku?.id || product.value.id,
    spuId: product.value.spuId || product.value.id,
    spuName: product.value.spuName,
    skuSpec: sku?.spec || '默认',
    price: sku?.price ?? product.value.price,
    image: product.value.image || product.value.coverImage
  }
}

function handleAddToCart() {
  cartStore.addToCart({
    ...getCurrentSkuInfo(),
    quantity: quantity.value
  })
}

function handleBuyNow() {
  cartStore.addToCart({
    ...getCurrentSkuInfo(),
    quantity: quantity.value
  })
  router.push('/cart')
}
</script>

<style scoped>
.breadcrumb {
  padding: 12px 0;
  font-size: 13px;
  color: #999;
}

.breadcrumb a {
  color: #666;
  text-decoration: none;
}

.breadcrumb a:hover {
  color: #e4393c;
}

.breadcrumb .sep {
  margin: 0 8px;
}

.breadcrumb .current {
  color: #333;
}

.product-main {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

.product-image-section {
  width: 440px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  background: #f5f5f5;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.img-placeholder {
  font-size: 80px;
  font-weight: 700;
  color: #ddd;
}

.product-info-section {
  flex: 1;
}

.product-name {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
  line-height: 1.6;
}

.price-box {
  background: #fff5f5;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.price-label {
  font-size: 14px;
  color: #999;
  margin-right: 12px;
}

.price-symbol {
  color: #e4393c;
  font-size: 18px;
  font-weight: 600;
}

.price-value {
  color: #e4393c;
  font-size: 30px;
  font-weight: 700;
}

.section-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  font-weight: 400;
}

.sku-section {
  margin-bottom: 20px;
}

.sku-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.sku-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.sku-btn:hover {
  border-color: #e4393c;
  color: #e4393c;
}

.sku-btn.active {
  border-color: #e4393c;
  background: #fff5f5;
  color: #e4393c;
}

.quantity-section {
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.quantity-control button {
  width: 34px;
  height: 34px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 16px;
  color: #333;
}

.quantity-control button:first-child {
  border-radius: 4px 0 0 4px;
}

.quantity-control button:last-child {
  border-radius: 0 4px 4px 0;
}

.quantity-control button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.quantity-control input {
  width: 60px;
  height: 34px;
  border: 1px solid #ddd;
  border-left: none;
  border-right: none;
  text-align: center;
  font-size: 14px;
  outline: none;
  -moz-appearance: textfield;
}

.quantity-control input::-webkit-outer-spin-button,
.quantity-control input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.stock-info {
  font-size: 13px;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.btn-add-cart {
  height: 46px;
  padding: 0 32px;
  background: #fff;
  color: #e4393c;
  border: 1px solid #e4393c;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-cart:hover {
  background: #fff5f5;
}

.btn-buy-now {
  height: 46px;
  padding: 0 32px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-buy-now:hover {
  background: #d42e2e;
}

.product-description {
  border-top: 1px solid #e8e8e8;
  padding-top: 30px;
  margin-bottom: 40px;
}

.desc-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.desc-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  min-height: 200px;
}

.loading,
.not-found {
  text-align: center;
  padding: 120px 0;
  color: #999;
  font-size: 16px;
}

@media (max-width: 768px) {
  .product-main {
    flex-direction: column;
  }

  .product-image-section {
    width: 100%;
  }
}
</style>
