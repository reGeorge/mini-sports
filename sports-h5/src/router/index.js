import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/games'
  },
  {
    path: '/games',
    name: 'Games',
    component: () => import('@/views/games/GameList.vue')
  },
  {
    path: '/games/:id',
    name: 'GameDetail',
    component: () => import('@/views/games/GameDetail.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue')
  },
  {
    path: '/user/detail',
    name: 'UserDetail',
    component: () => import('@/views/user/UserDetail.vue')
  },
  {
    path: '/user/edit',
    name: 'UserEdit',
    component: () => import('@/views/user/UserEdit.vue')
  },
  {
    path: '/points',
    name: 'Points',
    component: () => import('@/views/points/PointsSearch.vue')
  }
  // ... 其他路由配置
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 