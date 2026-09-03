<script setup lang="ts">
/**
 * 首页组件
 * <p>
 * 功能说明：
 * 显示欢迎信息和系统概览
 * </p>
 */

import { useUserStore } from '@/stores/user'

// 用户状态管理
const userStore = useUserStore()

/**
 * 打开接口文档
 */
function openDoc() {
  window.open('http://localhost:8080/doc.html', '_blank')
}
</script>

<template>
  <!-- 首页容器 -->
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎使用企业级管理系统</h1>
          <p class="welcome-message">
            您好，{{ userStore.nickname || '用户' }}！
          </p>
          <p class="system-info">
            这是一个基于 Spring Boot 3 + Vue3 + TypeScript 的前后端分离项目示例。
          </p>
          <p class="role-info" v-if="userStore.roles.length > 0">
            您的角色：{{ userStore.roles.join('、') }}
          </p>
        </div>
      </div>
    </el-card>
    
    <!-- 功能卡片 -->
    <el-row :gutter="20" class="feature-row">
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>后端技术栈</span>
            </div>
          </template>
          <ul class="feature-list">
            <li>Spring Boot 3.3.x</li>
            <li>MyBatis-Plus 3.5.5</li>
            <li>Sa-Token 认证</li>
            <li>MySQL 8.0</li>
            <li>Druid 连接池</li>
          </ul>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>前端技术栈</span>
            </div>
          </template>
          <ul class="feature-list">
            <li>Vue 3.x + TypeScript</li>
            <li>Vite 5 构建</li>
            <li>Element Plus UI</li>
            <li>Pinia 状态管理</li>
            <li>Vue Router 4</li>
          </ul>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="feature-card">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>接口文档</span>
            </div>
          </template>
          <p class="feature-desc">
            项目集成了 Knife4j 接口文档，提供在线调试功能。
          </p>
          <el-button type="primary" @click="openDoc">
            打开接口文档
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
// 导入图标组件
import { Monitor, Document } from '@element-plus/icons-vue'

export default {
  components: {
    Monitor,
    Document
  }
}
</script>

<style scoped>
/* 首页容器 */
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  align-items: center;
}

.welcome-text {
  flex: 1;
}

.welcome-text h1 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 28px;
}

.welcome-message {
  font-size: 18px;
  color: #606266;
  margin: 0 0 10px 0;
}

.system-info {
  color: #909399;
  margin: 0 0 10px 0;
}

.role-info {
  color: #409eff;
  font-weight: 500;
  margin: 0;
}

/* 功能卡片行 */
.feature-row {
  margin-top: 20px;
}

.feature-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  color: #606266;
}

.feature-list li:last-child {
  border-bottom: none;
}

.feature-desc {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}
</style>