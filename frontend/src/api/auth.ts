/**
 * 认证相关API接口
 * <p>
 * 功能说明：
 * 封装登录、退出、获取用户信息等接口
 * </p>
 */

import { get, post } from './request'
import type { LoginRequest, LoginResult, UserInfo } from '@/types/api'

/**
 * 用户登录
 * <p>
 * 接口：POST /api/auth/login
 * 功能：验证用户名密码，返回token
 * </p>
 *
 * @param data 登录参数（用户名、密码）
 * @returns 包含token和用户信息的登录结果
 */
export function login(data: LoginRequest) {
  return post<LoginResult>('/auth/login', data)
}

/**
 * 获取当前登录用户信息
 * <p>
 * 接口：GET /api/auth/info
 * 功能：根据token获取当前登录用户的详细信息
 * </p>
 *
 * @returns 用户信息（包含角色）
 */
export function getUserInfo() {
  return get<UserInfo>('/auth/info')
}

/**
 * 用户退出登录
 * <p>
 * 接口：POST /api/auth/logout
 * 功能：清除登录状态，使token失效
 * </p>
 */
export function logout() {
  return post('/auth/logout')
}