<template>
  <div class="product-card" @click="goDetail">
    <div class="product-image">
      <div class="img-placeholder">{{ product.spuName?.charAt(0) || '商' }}</div>
    </div>
    <div class="product-info">
      <h3 class="product-name">{{ product.spuName }}</h3>
      <div class="product-price">
        <span class="price-symbol">¥</span>
        <span class="price-value">{{ product.price }}</span>
      </div>
      <button class="btn-add-cart" @click.stop="handleAddToCart">
        加入购物车
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()

function goDetail() {
  router.push(`/product/${props.product.id || props.product.spuId}`)
}

function handleAddToCart() {
  cartStore.addToCart({
    skuId: props.product.skuId || props.product.id || props.product.spuId,
    spuId: props.product.spuId || props.product.id,
    spuName: props.product.spuName,
    skuSpec: props.product.skuSpec,
    price: props.product.price,
    quantity: 1,
    image: props.product.image || props.product.coverImage
  })
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}

.product-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-image {
  width: 100%;
  aspect-ratio: 1 / 1;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.img-placeholder {
  font-size: 48px;
  font-weight: 700;
  color: #ccc;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 400;
}

.product-price {
  margin-bottom: 10px;
}

.price-symbol {
  color: #e4393c;
  font-size: 14px;
  font-weight: 600;
}

.price-value {
  color: #e4393c;
  font-size: 20px;
  font-weight: 600;
}

.btn-add-cart {
  width: 100%;
  height: 34px;
  background: #fff;
  color: #e4393c;
  border: 1px solid #e4393c;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.btn-add-cart:hover {
  background: #e4393c;
  color: #fff;
}
</style>
