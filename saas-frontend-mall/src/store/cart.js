import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const CART_KEY = 'saas_mall_cart'

/**
 * 从 localStorage 加载购物车数据
 */
function loadCart() {
  try {
    const raw = localStorage.getItem(CART_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

/**
 * 保存购物车数据到 localStorage
 */
function saveCart(items) {
  localStorage.setItem(CART_KEY, JSON.stringify(items))
}

export const useCartStore = defineStore('cart', () => {
  const items = ref(loadCart())

  /**
   * 购物车总数量
   */
  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  /**
   * 购物车总价（所有商品）
   */
  const totalPrice = computed(() => {
    return items.value.reduce(
      (sum, item) => sum + item.price * item.quantity,
      0
    )
  })

  /**
   * 选中商品总价
   */
  const selectedTotalPrice = computed(() => {
    return items.value
      .filter((item) => item.checked)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  /**
   * 选中商品总数量
   */
  const selectedTotalCount = computed(() => {
    return items.value
      .filter((item) => item.checked)
      .reduce((sum, item) => sum + item.quantity, 0)
  })

  /**
   * 选中的商品列表
   */
  const checkedItems = computed(() => {
    return items.value.filter((item) => item.checked)
  })

  /**
   * 是否全选
   */
  const isAllChecked = computed(() => {
    return items.value.length > 0 && items.value.every((item) => item.checked)
  })

  /**
   * 添加商品到购物车
   */
  function addToCart(product) {
    const existing = items.value.find(
      (item) => item.skuId === product.skuId
    )
    if (existing) {
      existing.quantity += product.quantity || 1
    } else {
      items.value.push({
        skuId: product.skuId,
        spuId: product.spuId,
        spuName: product.spuName,
        skuSpec: product.skuSpec || '默认',
        price: product.price,
        quantity: product.quantity || 1,
        image: product.image || '',
        checked: true
      })
    }
    persistCart()
  }

  /**
   * 从购物车移除商品
   */
  function removeFromCart(skuId) {
    const index = items.value.findIndex((item) => item.skuId === skuId)
    if (index > -1) {
      items.value.splice(index, 1)
      persistCart()
    }
  }

  /**
   * 更新商品数量
   */
  function updateQuantity(skuId, quantity) {
    const item = items.value.find((item) => item.skuId === skuId)
    if (item) {
      const qty = parseInt(quantity)
      if (qty < 1) {
        removeFromCart(skuId)
        return
      }
      item.quantity = qty
      persistCart()
    }
  }

  /**
   * 切换选中状态
   */
  function toggleChecked(skuId) {
    const item = items.value.find((item) => item.skuId === skuId)
    if (item) {
      item.checked = !item.checked
      persistCart()
    }
  }

  /**
   * 全选/取消全选
   */
  function toggleAllChecked() {
    const checked = !isAllChecked.value
    items.value.forEach((item) => {
      item.checked = checked
    })
    persistCart()
  }

  /**
   * 清空购物车
   */
  function clearCart() {
    items.value = []
    persistCart()
  }

  /**
   * 清空已选中的商品（下单后调用）
   */
  function clearCheckedItems() {
    items.value = items.value.filter((item) => !item.checked)
    persistCart()
  }

  /**
   * 持久化到 localStorage
   */
  function persistCart() {
    saveCart(items.value)
  }

  return {
    items,
    totalCount,
    totalPrice,
    selectedTotalPrice,
    selectedTotalCount,
    checkedItems,
    isAllChecked,
    addToCart,
    removeFromCart,
    updateQuantity,
    toggleChecked,
    toggleAllChecked,
    clearCart,
    clearCheckedItems
  }
})
