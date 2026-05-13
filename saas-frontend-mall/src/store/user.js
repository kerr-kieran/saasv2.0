import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as userApi from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  /**
   * 登录
   */
  async function login(data) {
    const res = await userApi.login(data)
    token.value = res.data.token || res.data.accessToken
    localStorage.setItem('token', token.value)
    await fetchUserInfo()
    return res
  }

  /**
   * 注册
   */
  async function register(data) {
    const res = await userApi.register(data)
    return res
  }

  /**
   * 退出登录
   */
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  /**
   * 获取用户信息
   */
  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await userApi.getUserInfo()
      userInfo.value = res.data
    } catch {
      // token 失效时清除
      logout()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    logout,
    fetchUserInfo
  }
})
