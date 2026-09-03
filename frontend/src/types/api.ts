/**
 * API类型定义文件
 * <p>
 * 功能说明：
 * 定义后端接口返回的数据类型，用于TypeScript类型检查
 * </p>
 */

/**
 * 统一返回结果类型
 * 对应后端Result<T>类
 */
export interface Result<T = any> {
  // 状态码（200：成功，500：失败）
  code: number
  // 消息提示
  message: string
  // 返回数据
  data: T
}

/**
 * 登录请求参数类型
 */
export interface LoginRequest {
  // 用户名
  username: string
  // 密码
  password: string
}

/**
 * 登录响应数据类型
 */
export interface LoginResult {
  // 登录凭证（token）
  token: string
  // 用户基本信息
  userInfo: UserInfo
}

/**
 * 用户信息类型
 */
export interface UserInfo {
  // 用户ID
  id: number
  // 用户名
  username: string
  // 昵称
  nickname: string
  // 头像URL
  avatar: string
  // 邮箱
  email: string
  // 手机号
  phone: string
  // 角色列表
  roles: string[]
}

/**
 * 角色类型
 */
export interface Role {
  // 角色ID
  id: number
  // 角色名称
  roleName: string
  // 角色编码
  roleCode: string
  // 角色描述
  description: string
}