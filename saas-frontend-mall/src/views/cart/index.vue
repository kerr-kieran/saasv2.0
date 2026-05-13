<template>
  <div class="cart-page">
    <h2 class="page-title">购物车</h2>

    <!-- 空购物车 -->
    <div v-if="cartStore.items.length === 0" class="empty-cart">
      <div class="empty-icon">&#128722;</div>
      <p>购物车为空</p>
      <router-link to="/" class="btn-go-shop">去逛逛</router-link>
    </div>

    <!-- 购物车列表 -->
    <template v-else>
      <div class="cart-table">
        <div class="cart-header">
          <label class="check-all">
            <input
              type="checkbox"
              :checked="cartStore.isAllChecked"
              @change="cartStore.toggleAllChecked()"
            />
            全选
          </label>
          <span class="col-name">商品信息</span>
          <span class="col-price">单价</span>
          <span class="col-qty">数量</span>
          <span class="col-subtotal">小计</span>
          <span class="col-action">操作</span>
        </div>

        <div
          v-for="item in cartStore.items"
          :key="item.skuId"
          class="cart-item"
        >
          <div class="col-check">
            <input
              type="checkbox"
              :checked="item.checked"
              @change="cartStore.toggleChecked(item.skuId)"
            />
          </div>
          <div class="col-name">
            <div class="item-image">
              <div class="img-placeholder">{{ item.spuName?.charAt(0) }}</div>
            </div>
            <div class="item-info">
              <p class="item-name">{{ item.spuName }}</p>
              <p class="item-spec">规格: {{ item.skuSpec }}</p>
            </div>
          </div>
          <div class="col-price">
            <span class="price">¥{{ item.price }}</span>
          </div>
          <div class="col-qty">
            <div class="qty-control">
              <button @click="cartStore.updateQuantity(item.skuId, item.quantity - 1)">-</button>
              <input
                type="number"
                :value="item.quantity"
                min="1"
                @change="cartStore.updateQuantity(item.skuId, $event.target.value)"
              />
              <button @click="cartStore.updateQuantity(item.skuId, item.quantity + 1)">+</button>
            </div>
          </div>
          <div class="col-subtotal">
            <span class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
          <div class="col-action">
            <button class="btn-delete" @click="cartStore.removeFromCart(item.skuId)">删除</button>
          </div>
        </div>
      </div>

      <!-- 底部结算栏 -->
      <div class="cart-footer">
        <label class="check-all">
          <input
            type="checkbox"
            :checked="cartStore.isAllChecked"
            @change="cartStore.toggleAllChecked()"
          />
          全选
        </label>
        <div class="footer-right">
          <span class="selected-info">
            已选 <strong>{{ cartStore.selectedTotalCount }}</strong> 件
          </span>
          <span class="total-label">合计:</span>
          <span class="total-price">¥{{ cartStore.selectedTotalPrice.toFixed(2) }}</span>
          <button
            class="btn-checkout"
            :disabled="cartStore.selectedTotalCount === 0"
            @click="goCheckout"
          >
            去结算
          </button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()

function goCheckout() {
  router.push('/checkout')
}
</script>

<style scoped>
.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #333;
}

.empty-cart {
  text-align: center;
  padding: 80px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-cart p {
  font-size: 16px;
  color: #999;
  margin-bottom: 20px;
}

.btn-go-shop {
  display: inline-block;
  padding: 10px 32px;
  background: #e4393c;
  color: #fff;
  text-decoration: none;
  border-radius: 6px;
  font-size: 14px;
}

.cart-table {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-size: 13px;
  color: #999;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.cart-item:last-child {
  border-bottom: none;
}

.col-check {
  width: 40px;
  flex-shrink: 0;
}

.check-all {
  width: 60px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #666;
}

.check-all input,
.col-check input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.col-name {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-image {
  width: 72px;
  height: 72px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.img-placeholder {
  font-size: 28px;
  font-weight: 700;
  color: #ddd;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.item-spec {
  font-size: 12px;
  color: #999;
}

.col-price {
  width: 120px;
  text-align: center;
  font-size: 14px;
  color: #333;
}

.col-qty {
  width: 140px;
  display: flex;
  justify-content: center;
}

.qty-control {
  display: flex;
  align-items: center;
}

.qty-control button {
  width: 28px;
  height: 28px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.qty-control button:first-child {
  border-radius: 4px 0 0 4px;
}

.qty-control button:last-child {
  border-radius: 0 4px 4px 0;
}

.qty-control input {
  width: 48px;
  height: 28px;
  border: 1px solid #ddd;
  border-left: none;
  border-right: none;
  text-align: center;
  font-size: 13px;
  outline: none;
  -moz-appearance: textfield;
}

.qty-control input::-webkit-outer-spin-button,
.qty-control input::-webkit-inner-spin-button {
  -webkit-appearance: none;
}

.col-subtotal {
  width: 120px;
  text-align: center;
  font-size: 14px;
  color: #e4393c;
  font-weight: 600;
}

.col-action {
  width: 80px;
  text-align: center;
}

.btn-delete {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete:hover {
  color: #e4393c;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-top: none;
  border-radius: 0 0 8px 8px;
  position: sticky;
  bottom: 0;
  margin-top: -1px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-info {
  font-size: 13px;
  color: #666;
}

.selected-info strong {
  color: #e4393c;
}

.total-label {
  font-size: 14px;
  color: #333;
}

.total-price {
  font-size: 22px;
  color: #e4393c;
  font-weight: 700;
}

.btn-checkout {
  height: 42px;
  padding: 0 32px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
  margin-left: 12px;
}

.btn-checkout:hover {
  background: #d42e2e;
}

.btn-checkout:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
