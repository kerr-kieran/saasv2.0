import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo, logout as logoutApi } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    avatar: (state) => state.userInfo?.avatar || '',
    permissions: (state) => state.userInfo?.permissions || [],
    roles: (state) => state.userInfo?.roles || []
  },

  actions: {
    async login(username, password) {
      const res = await loginApi(username, password)
      this.token = res.data.token
      localStorage.setItem('token', this.token)
      await this.fetchUserInfo()
      ElMessage.success('登录成功')
    },

    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
    },

    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // ignore
      }
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})
