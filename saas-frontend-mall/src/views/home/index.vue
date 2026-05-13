<template>
  <div class="home-page">
    <!-- Hero Banner -->
    <section class="hero-banner">
      <div class="hero-content">
        <h1>欢迎来到 SaaS Mall</h1>
        <p>海量好物，尽在其中</p>
      </div>
    </section>

    <!-- 分类导航 -->
    <section class="category-bar" v-if="categories.length > 0">
      <button
        v-for="cat in categories"
        :key="cat.id"
        :class="['cat-item', { active: activeCategory === cat.id }]"
        @click="activeCategory = cat.id"
      >
        {{ cat.name }}
      </button>
    </section>

    <!-- 商品网格 -->
    <section class="product-section">
      <h2 class="section-title">全部商品</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="products.length === 0" class="empty">暂无商品</div>
      <div v-else class="product-grid">
        <ProductCard
          v-for="product in products"
          :key="product.id || product.spuId"
          :product="product"
        />
      </div>
    </section>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <button
        :disabled="page === 1"
        @click="page--; loadProducts()"
        class="page-btn"
      >
        上一页
      </button>
      <span class="page-info">{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
      <button
        :disabled="page >= Math.ceil(total / pageSize)"
        @click="page++; loadProducts()"
        class="page-btn"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getProductPage, getCategoryTree } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()

const loading = ref(false)
const products = ref([])
const categories = ref([])
const activeCategory = ref(null)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

onMounted(() => {
  loadCategories()
  loadProducts()
})

// 监听路由关键字变化
watch(
  () => route.query.keyword,
  () => {
    page.value = 1
    loadProducts()
  }
)

async function loadCategories() {
  try {
    const res = await getCategoryTree()
    categories.value = res.data || []
  } catch {
    // ignore
  }
}

async function loadProducts() {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (route.query.keyword) {
      params.keyword = route.query.keyword
    }
    if (activeCategory.value) {
      params.categoryId = activeCategory.value
    }
    const res = await getProductPage(params)
    products.value = res.data?.records || res.data?.list || res.data || []
    total.value = res.data?.total || 0
  } catch {
    products.value = []
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.hero-banner {
  background: linear-gradient(135deg, #e4393c 0%, #ff6b6b 100%);
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
  color: #fff;
  margin-bottom: 30px;
}

.hero-content h1 {
  font-size: 36px;
  margin-bottom: 10px;
}

.hero-content p {
  font-size: 18px;
  opacity: 0.9;
}

.category-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.cat-item {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}

.cat-item:hover {
  border-color: #e4393c;
  color: #e4393c;
}

.cat-item.active {
  background: #e4393c;
  color: #fff;
  border-color: #e4393c;
}

.product-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

@media (max-width: 960px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 640px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.loading,
.empty {
  text-align: center;
  padding: 80px 0;
  color: #999;
  font-size: 16px;
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

.page-btn:not(:disabled):hover {
  border-color: #e4393c;
  color: #e4393c;
}

.page-info {
  font-size: 14px;
  color: #666;
}
</style>
