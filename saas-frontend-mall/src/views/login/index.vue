<template>
  <div class="login-page">
    <div class="login-card">
      <!-- Tab 切换 -->
      <div class="tabs">
        <button
          :class="['tab-item', { active: tab === 'login' }]"
          @click="tab = 'login'"
        >
          登录
        </button>
        <button
          :class="['tab-item', { active: tab === 'register' }]"
          @click="tab = 'register'"
        >
          注册
        </button>
      </div>

      <!-- 登录表单 -->
      <form v-if="tab === 'login'" @submit.prevent="handleLogin" class="form">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="loginForm.username"
            type="text"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            required
          />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '登录中...' : '登录' }}
        </button>
      </form>

      <!-- 注册表单 -->
      <form v-else @submit.prevent="handleRegister" class="form">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="registerForm.username"
            type="text"
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            required
            minlength="6"
          />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input
            v-model="registerForm.email"
            type="email"
            placeholder="请输入邮箱"
          />
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input
            v-model="registerForm.phone"
            type="text"
            placeholder="请输入手机号"
          />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="btn-submit" :disabled="submitting">
          {{ submitting ? '注册中...' : '注册' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const tab = ref('login')
const errorMsg = ref('')
const submitting = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  email: '',
  phone: ''
})

async function handleLogin() {
  errorMsg.value = ''
  if (!loginForm.username.trim() || !loginForm.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  submitting.value = true
  try {
    await userStore.login({
      username: loginForm.username.trim(),
      password: loginForm.password
    })
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    errorMsg.value = e.message || '登录失败，请重试'
  } finally {
    submitting.value = false
  }
}

async function handleRegister() {
  errorMsg.value = ''
  if (!registerForm.username.trim() || !registerForm.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  if (registerForm.password.length < 6) {
    errorMsg.value = '密码至少6位'
    return
  }
  submitting.value = true
  try {
    await userStore.register({
      username: registerForm.username.trim(),
      password: registerForm.password,
      email: registerForm.email.trim(),
      phone: registerForm.phone.trim()
    })
    // 注册成功后自动登录
    await userStore.login({
      username: registerForm.username.trim(),
      password: registerForm.password
    })
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    errorMsg.value = e.message || '注册失败，请重试'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  padding: 60px 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  padding: 40px;
}

.tabs {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-item {
  flex: 1;
  padding: 12px 0;
  border: none;
  background: none;
  font-size: 16px;
  color: #999;
  cursor: pointer;
  position: relative;
}

.tab-item.active {
  color: #e4393c;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 2px;
  background: #e4393c;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
}

.form-group input {
  width: 100%;
  height: 42px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: #e4393c;
}

.error-msg {
  color: #e4393c;
  font-size: 13px;
  margin-bottom: 16px;
}

.btn-submit {
  width: 100%;
  height: 44px;
  background: #e4393c;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-submit:hover {
  background: #d42e2e;
}

.btn-submit:disabled {
  background: #f5a3a3;
  cursor: not-allowed;
}
</style>
