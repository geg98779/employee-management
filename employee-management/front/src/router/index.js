import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true },
      children: [
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/ProductView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'categories',
          name: 'categories',
          component: () => import('../views/CategoryView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'suppliers',
          name: 'suppliers',
          component: () => import('../views/SupplierView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/OrderView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('../views/UserView.vue'),
          meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/ProfileView.vue'),
          meta: { requiresAuth: true }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    }
  ]
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要登录的路由
    if (!user) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 检查是否需要管理员权限
      if (to.matched.some(record => record.meta.requiresAdmin)) {
        if (user.role === 1) {
          next()
        } else {
          next({ path: '/' })
        }
      } else {
        next()
      }
    }
  } else {
    next()
  }
})

export default router 