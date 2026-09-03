<script setup lang="ts">
/**
 * 布局组件
 * <p>
 * 功能说明：
 * 1. 提供应用整体布局框架
 * 2. 包含顶部导航栏
 * 3. 包含侧边栏菜单
 * 4. 主内容区域
 * </p>
 */

import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

// 用户状态管理
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 页面加载时初始化用户信息
onMounted(() => {
  // 从本地存储恢复用户信息
  userStore.initUserInfo()
  
  // 如果有token但没有用户信息，从后端获取
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.fetchUserInfo()
  }
})

/**
 * 切换侧边栏折叠状态
 */
function toggleCollapse() {
  isCollapse.value = !isCollapse.value
}

/**
 * 处理退出登录
 */
function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
  }).catch(() => {
    // 取消操作
  })
}
</script>

<template>
  <!-- 布局容器 -->
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-left">
        <!-- 折叠按钮 -->
        <el-icon class="collapse-btn" @click="toggleCollapse">
          <component :is="isCollapse ? Expand : Fold" />
        </el-icon>
        <!-- 系统标题 -->
        <h1 class="system-title">企业级管理系统</h1>
      </div>
      
      <div class="header-right">
        <!-- 用户信息 -->
        <el-dropdown @command="handleLogout">
          <span class="user-info">
            <el-avatar :size="32" :src="userStore.avatar || undefined">
              {{ userStore.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <span class="username">{{ userStore.nickname || '用户' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- 主体区域 -->
    <el-container class="layout-main">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :router="true"
          class="aside-menu"
        >
          <!-- 首页菜单项 -->
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <!-- 示例菜单（可扩展） -->
          <el-sub-menu index="example">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/example/user">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/example/role">
              <el-icon><UserFilled /></el-icon>
              <template #title>角色管理</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区域 -->
      <el-main class="layout-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script lang="ts">
// 导入图标组件
import {
  Fold,
  Expand,
  HomeFilled,
  Setting,
  User,
  UserFilled,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'

export default {
  components: {
    Fold,
    Expand,
    HomeFilled,
    Setting,
    User,
    UserFilled,
    ArrowDown,
    SwitchButton
  }
}
</script>

<style scoped>
/* 布局容器 */
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.layout-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 24px;
  cursor: pointer;
  color: #606266;
}

.collapse-btn:hover {
  color: #409eff;
}

.system-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #606266;
}

/* 主体区域 */
.layout-main {
  flex: 1;
  overflow: hidden;
}

/* 侧边栏 */
.layout-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  transition: width 0.3s;
  overflow: hidden;
}

.aside-menu {
  height: 100%;
  border-right: none;
}

/* 主内容区域 */
.layout-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>