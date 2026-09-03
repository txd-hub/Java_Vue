import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  // Vue插件
  plugins: [vue()],
  
  // 路径别名配置
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  
  // 开发服务器配置
  server: {
    // 端口号
    port: 5173,
    // 是否自动打开浏览器
    open: false,
    // 代理配置（解决跨域问题）
    proxy: {
      // 代理/api开头的请求到后端服务器
      '/api': {
        // 后端服务器地址
        target: 'http://localhost:8080',
        // 是否允许跨域
        changeOrigin: true
      }
    }
  },
  
  // TypeScript配置
  esbuild: {
    // 移除console.log（生产环境）
    pure: ['console.log'],
    // 移除debugger（生产环境）
    drop: ['debugger']
  }
})