import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/tournament'
  },
  {
    path: '/tournament',
    name: 'TournamentList',
    component: () => import('@/views/tournament/TournamentList.vue'),
    meta: { 
      requiresAuth: false,
      title: '赛事列表'
    }
  },
  {
    path: '/tournament/detail/:id',
    name: 'TournamentDetail',
    component: () => import('@/views/tournament/TournamentDetail.vue'),
    meta: { 
      requiresAuth: true,
      title: '赛事详情'
    }
  },
  {
    path: '/tournament/create',
    name: 'TournamentCreate',
    component: () => import('@/views/tournament/TournamentForm.vue'),
    meta: { 
      requiresAuth: true,
      title: '创建赛事',
      permission: 'tournament:create'
    }
  },
  {
    path: '/tournament/edit/:id',
    name: 'TournamentEdit',
    component: () => import('@/views/tournament/TournamentForm.vue'),
    meta: { 
      requiresAuth: true,
      title: '编辑赛事',
      permission: 'tournament:edit'
    }
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
  },
  {
    path: '/admin',
    name: 'AdminHome',
    component: () => import('@/views/admin/AdminHome.vue')
  },
  {
    path: '/admin/roles',
    name: 'RoleList',
    component: () => import('@/views/admin/RoleList.vue')
  },
  {
    path: '/admin/user-roles',
    name: 'UserRoleAssign',
    component: () => import('@/views/admin/UserRoleAssign.vue')
  },
  {
    path: '/admin/register',
    name: 'AdminRegister',
    component: () => import('@/views/admin/AdminRegister.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 