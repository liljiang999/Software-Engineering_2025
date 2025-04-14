import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
    proxy: {
      // 代理 /search 路径到后端的搜索接口
      '/search': {
        target: 'http://localhost:8080', // 后端的地址
        changeOrigin: true,              // 是否修改请求头中的 Origin 字段
        rewrite: (path) => path.replace(/^\/search/, '') // 将请求路径中的 /search 去掉，映射到后端的搜索接口
      }
    }
  }
})
