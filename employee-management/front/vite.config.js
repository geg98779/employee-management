import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/employee_management_war/api'),
        configure: (proxy, options) => {
          // 代理配置
          proxy.on('proxyReq', (proxyReq, req, res) => {
            // 确保请求头中包含Origin
            proxyReq.setHeader('Origin', 'http://localhost:5173');
          });
        }
      }
    }
  }
}) 