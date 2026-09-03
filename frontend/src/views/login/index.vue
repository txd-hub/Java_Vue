<script setup lang="ts">
/**
 * 登录页面组件
 * <p>
 * 功能说明：
 * 1. 显示登录表单（用户名、密码）
 * 2. 表单验证
 * 3. 调用登录接口
 * 4. 登录成功后跳转到首页
 * </p>
 */

import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import type { LoginRequest } from '@/types/api'
import { ElMessage } from 'element-plus'

// 用户状态管理
const userStore = useUserStore()

// 加载状态
const loading = ref(false)

// 登录表单数据
const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref()

/**
 * 处理登录
 * <p>
 * 功能：验证表单，调用登录接口
 * </p>
 */
async function handleLogin() {
  try {
    // 验证表单
    await loginFormRef.value?.validate()
    
    // 设置加载状态
    loading.value = true
    
    // 调用登录方法
    await userStore.login(loginForm)
  } catch (error) {
    // 表单验证失败或登录失败
    if (error !== false) {
      ElMessage.error('登录失败，请检查用户名和密码')
    }
  } finally {
    // 取消加载状态
    loading.value = false
  }
}
</script>

<template>
  <!-- 登录页面容器 -->
  <div class="login-container">
    <!-- 登录卡片 -->
    <el-card class="login-card">
      <!-- 标题 -->
      <template #header>
        <div class="login-header">
          <h2>企业级管理系统</h2>
          <p>欢迎登录，请输入您的账号和密码</p>
        </div>
      </template>
      
      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        size="large"
      >
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <!-- 登录按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 提示信息 -->
      <div class="login-tip">
        <p>默认账号：admin / admin123</p>
        <p>测试账号：testuser / user123</p>
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
// 导入图标组件
import { User, Lock } from '@element-plus/icons-vue'

export default {
  components: {
    User,
    Lock
  }
}
</script>

<style scoped>
/* 登录页面容器 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 登录卡片 */
.login-card {
  width: 400px;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

/* 标题区域 */
.login-header {
  text-align: center;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 24px;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 登录表单 */
.login-form {
  margin-top: 20px;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

/* 提示信息 */
.login-tip {
  margin-top: 20px;
  text-align: center;
  color: #909399;
  font-size: 12px;
}

.login-tip p {
  margin: 5px 0;
}
</style>