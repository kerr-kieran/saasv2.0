<template>
  <header class="site-header">
    <div class="header-inner">
      <router-link to="/" class="logo">SaaS Mall</router-link>

      <div class="search-bar">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索商品"
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">搜索</button>
      </div>

      <nav class="header-nav">
        <router-link to="/cart" class="nav-item cart-link">
          购物车
          <span v-if="cartStore.totalCount > 0" class="cart-badge">
            {{ cartStore.totalCount }}
          </span>
        </router-link>
        <template v-if="userStore.isLoggedIn">
          <router-link to="/order" class="nav-item">我的订单</router-link>
          <router-link to="/user" class="nav-item">
            {{ userStore.userInfo?.nickname || '个人中心' }}
          </router-link>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-item">登录</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const keyword = ref('')

function handleSearch() {
  const kw = keyword.value.trim()
  if (kw) {
    router.push({ path: '/', query: { keyword: kw } })
  }
}
</script>

<style scoped>
.site-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 64px;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #e4393c;
  text-decoration: none;
  margin-right: 40px;
  flex-shrink: 0;
}

.search-bar {
  flex: 1;
  max-width: 500px;
  display: flex;
}

.search-bar input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border: 2px solid #e4393c;
  border-right: none;
  border-radius: 4px 0 0 4px;
  outline: none;
  font-size: 14px;
}

.search-bar button {
  height: 36px;
  padding: 0 20px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 14px;
}

.header-nav {
  display: flex;
  align-items: center;
  margin-left: 40px;
  gap: 24px;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  white-space: nowrap;
}

.nav-item:hover {
  color: #e4393c;
}

.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -14px;
  background: #e4393c;
  color: #fff;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  border-radius: 9px;
  padding: 0 4px;
}
</style>
