/**
 * Vue Router路由配置
 * <p>
 * 功能说明：
 * 1. 定义应用路由规则
 * 2. 配置路由守卫（未登录自动跳转登录页）
 * 3. 实现路由懒加载
 * </p>
 */

import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    // 登录页路由
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false  // 不需要登录
    }
  },
  {
    // 首页路由（包含布局）
    path: '/',
    name: 'Layout',
    component: () => import('@/components/layout/index.vue'),
    redirect: '/home',  // 默认重定向到首页
    meta: {
      requiresAuth: true  // 需要登录
    },
    children: [
      {
        // 首页子路由
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: {
          title: '首页',
          requiresAuth: true
        }
      }
    ]
  },
  {
    // 404页面
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/login'
  }
]

// 创建路由实例
const router = createRouter({
  // 使用HTML5 history模式
  history: createWebHistory(),
  // 路由配置
  routes
})

/**
 * 全局前置守卫
 * <p>
 * 功能：
 * 1. 检查用户是否已登录
 * 2. 未登录则跳转到登录页
 * 3. 已登录则允许访问
 * </p>
 */
router.beforeEach((to, _from, next) => {
  // 从localStorage获取token
  const token = localStorage.getItem('token')
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 企业级管理系统`
  }
  
  // 检查是否需要登录认证
  if (to.meta.requiresAuth !== false) {
    // 需要登录，检查token是否存在
    if (!token) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }  // 记录原目标路径，登录后跳回
      })
      return
    }
  }
  
  // 已登录但访问登录页，跳转到首页
  if (to.path === '/login' && token) {
    next({ path: '/' })
    return
  }
  
  // 允许访问
  next()
})

// 导出路由实例
export default router