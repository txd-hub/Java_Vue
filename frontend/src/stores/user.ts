/**
 * 用户状态管理
 * <p>
 * 功能说明：
 * 1. 管理用户登录状态
 * 2. 存储用户信息和token
 * 3. 提供登录、退出、获取用户信息等方法
 * </p>
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth'
import type { LoginRequest, UserInfo } from '@/types/api'
import { ElMessage } from 'element-plus'
import router from '@/router'

/**
 * 用户状态Store
 * <p>
 * 使用Composition API风格定义
 * </p>
 */
export const useUserStore = defineStore('user', () => {
  // ==================== 状态定义 ====================
  
  // 登录凭证（token）
  const token = ref<string>(localStorage.getItem('token') || '')
  
  // 用户信息
  const userInfo = ref<UserInfo | null>(null)
  
  // ==================== 计算属性 ====================
  
  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)
  
  // 用户角色列表
  const roles = computed(() => userInfo.value?.roles || [])
  
  // 用户昵称
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')
  
  // 用户头像
  const avatar = computed(() => userInfo.value?.avatar || '')
  
  // ==================== 方法定义 ====================
  
  /**
   * 用户登录
   * <p>
   * 功能：调用登录接口，保存token和用户信息
   * </p>
   *
   * @param loginData 登录参数（用户名、密码）
   * @returns 登录是否成功
   */
  async function login(loginData: LoginRequest): Promise<boolean> {
    try {
      // 调用登录API
      const res = await loginApi(loginData)
      
      // 保存token
      token.value = res.data.token
      localStorage.setItem('token', res.data.token)
      
      // 保存用户信息
      userInfo.value = res.data.userInfo
      localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      
      // 显示成功消息
      ElMessage.success('登录成功')
      
      // 跳转到首页
      router.push('/')
      
      return true
    } catch (error) {
      // 登录失败，错误已在request.ts中处理
      return false
    }
  }
  
  /**
   * 获取用户信息
   * <p>
   * 功能：根据token从后端获取用户详细信息
   * </p>
   */
  async function fetchUserInfo() {
    try {
      // 调用获取用户信息API
      const res = await getUserInfoApi()
      
      // 保存用户信息
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    } catch (error) {
      // 获取失败，可能是token过期
      console.error('获取用户信息失败：', error)
      // 清除登录状态
      logout()
    }
  }
  
  /**
   * 用户退出登录
   * <p>
   * 功能：清除本地存储的token和用户信息，跳转到登录页
   * </p>
   */
  async function logout() {
    try {
      // 调用退出API（如果token还有效）
      if (token.value) {
        await logoutApi()
      }
    } catch (error) {
      // 即使退出接口失败，也要清除本地状态
      console.error('退出接口调用失败：', error)
    } finally {
      // 清除本地状态
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      // 跳转到登录页
      router.push('/login')
    }
  }
  
  /**
   * 初始化用户信息
   * <p>
   * 功能：从本地存储恢复用户信息（页面刷新时使用）
   * </p>
   */
  function initUserInfo() {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo && token.value) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (error) {
        console.error('解析用户信息失败：', error)
        // 清除无效数据
        localStorage.removeItem('userInfo')
      }
    }
  }
  
  // ==================== 返回状态和方法 ====================
  
  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLoggedIn,
    roles,
    nickname,
    avatar,
    // 方法
    login,
    fetchUserInfo,
    logout,
    initUserInfo
  }
})