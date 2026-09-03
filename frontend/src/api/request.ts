/**
 * Axios请求封装
 * <p>
 * 功能说明：
 * 1. 创建Axios实例，配置baseURL和超时时间
 * 2. 请求拦截器：自动携带token
 * 3. 响应拦截器：统一错误处理
 * 4. 封装GET和POST请求方法
 * </p>
 */

import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '@/types/api'
import router from '@/router'

// 创建Axios实例
const service: AxiosInstance = axios.create({
  // 后端API基础路径（通过Vite代理转发）
  baseURL: '/api',
  // 请求超时时间（10秒）
  timeout: 10000,
  // 请求头
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 * <p>
 * 功能：在每个请求发送前，自动添加token到请求头
 * </p>
 */
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    
    // 如果token存在，添加到请求头
    if (token) {
      config.headers['Authorization'] = token
    }
    
    return config
  },
  (error) => {
    // 请求错误处理
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * <p>
 * 功能：
 * 1. 统一处理后端返回的数据格式
 * 2. 处理token过期、未登录等异常情况
 * 3. 统一错误提示
 * </p>
 */
service.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    // 获取响应数据
    const res = response.data
    
    // 判断请求是否成功（状态码200）
    if (res.code === 200) {
      return res as any
    }
    
    // 请求失败，显示错误消息
    ElMessage.error(res.message || '请求失败')
    
    // 处理特定错误码
    if (res.code === 401) {
      // token过期或未登录，清除本地存储并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
    
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // 网络错误或其他异常处理
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      
      switch (status) {
        case 401:
          message = '未登录或登录已过期，请重新登录'
          // 清除本地存储并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          message = '没有权限访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时，请稍后重试'
    }
    
    // 显示错误消息
    ElMessage.error(message)
    
    return Promise.reject(error)
  }
)

/**
 * 封装GET请求
 * <p>
 * 功能：发送GET请求，返回Promise
 * </p>
 *
 * @param url 请求路径
 * @param params 请求参数
 * @returns Promise
 */
export function get<T = any>(url: string, params?: any): Promise<Result<T>> {
  return service.get(url, { params })
}

/**
 * 封装POST请求
 * <p>
 * 功能：发送POST请求，返回Promise
 * </p>
 *
 * @param url 请求路径
 * @param data 请求数据
 * @returns Promise
 */
export function post<T = any>(url: string, data?: any): Promise<Result<T>> {
  return service.post(url, data)
}

/**
 * 封装PUT请求
 */
export function put<T = any>(url: string, data?: any): Promise<Result<T>> {
  return service.put(url, data)
}

/**
 * 封装DELETE请求
 */
export function del<T = any>(url: string): Promise<Result<T>> {
  return service.delete(url)
}

// 导出Axios实例（如果需要直接使用）
export default service