<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  DataLine,
  Document,
  Files,
  HomeFilled,
  MapLocation,
  Medal,
  Menu,
  Notebook,
  PieChart,
  Star,
  UserFilled,
  Van,
  Search,
  Tickets,
  Clock,
} from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'

const appStore = useAppStore()
const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const modeLabel = computed(() => (appStore.apiMode === 'mock' ? 'Mock' : 'Real'))

const navGroups = [
  {
    title: '门户',
    items: [{ label: '首页', path: '/', icon: HomeFilled }],
  },
  {
    title: '景点',
    items: [
      { label: '景点列表', path: '/scenic/list', icon: MapLocation },
      { label: '景点搜索', path: '/scenic/search', icon: Search },
      { label: '校园列表', path: '/scenic/campus', icon: Medal },
    ],
  },
  {
    title: '导航',
    items: [
      { label: '路线规划', path: '/navigation', icon: Van },
      { label: '拥挤度', path: '/navigation/congestion', icon: PieChart },
      { label: '附近设施', path: '/navigation/nearby', icon: Tickets },
    ],
  },
  {
    title: '内容',
    items: [
      { label: '推荐', path: '/recommend', icon: Star },
      { label: '日记', path: '/diary', icon: Notebook },
      { label: '收藏', path: '/favorite', icon: Document },
      { label: '历史', path: '/history', icon: Clock },
    ],
  },
  {
    title: '协同',
    items: [
      { label: '小组', path: '/group', icon: DataLine },
      { label: '账单', path: '/expense', icon: PieChart },
      { label: '文件', path: '/file', icon: Files },
      { label: '我的', path: '/user', icon: UserFilled },
    ],
  },
]

function go(path: string) {
  router.push(path)
  appStore.mobileMenuOpen = false
}

function isActive(path: string) {
  return path === '/' ? route.path === '/' : route.path.startsWith(path)
}

function search() {
  const keyword = appStore.globalKeyword.trim()
  router.push({ path: '/scenic/search', query: keyword ? { keyword } : {} })
}

function logout() {
  authStore.logout()
  router.push('/auth/login')
}

function goUserCenter() {
  if (authStore.isLoggedIn) {
    go('/user')
    return
  }
  router.push('/auth/login')
  appStore.mobileMenuOpen = false
}
</script>

<template>
  <div class="app-shell">
    <aside class="app-shell__sidebar">
      <div class="brand">
        <img src="/media/logo-mark.svg" alt="JourneyCraft" />
        <div>
          <strong>JourneyCraft</strong>
          <span>Travel Experience Hub</span>
        </div>
      </div>

      <div class="search-bar">
        <el-input
          v-model="appStore.globalKeyword"
          placeholder="搜索景点 / 美食 / 路线"
          clearable
          @keyup.enter="search"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="mode-switch">
        <span class="mode-switch__label">API</span>
        <el-segmented
          v-model="appStore.apiMode"
          :options="['mock', 'real']"
          size="small"
        />
      </div>

      <nav class="nav-groups">
        <section v-for="group in navGroups" :key="group.title" class="nav-group">
          <div class="nav-group__title">{{ group.title }}</div>
          <button
            v-for="item in group.items"
            :key="item.path"
            type="button"
            class="nav-item"
            :class="{ 'is-active': isActive(item.path) }"
            @click="go(item.path)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </button>
        </section>
      </nav>
    </aside>

    <div class="app-shell__main">
      <header class="topbar">
        <div class="topbar__left">
          <button class="topbar__menu" type="button" @click="appStore.mobileMenuOpen = true">
            <el-icon><Menu /></el-icon>
          </button>
          <div class="topbar__title">
            <strong>旅程产品面板</strong>
            <span>{{ modeLabel }} 模式 · {{ appStore.apiMode === 'mock' ? '本地 mock' : '真实联调' }}</span>
          </div>
        </div>
        <div class="topbar__actions">
          <el-button text @click="go('/navigation/route')">
            <el-icon><Van /></el-icon>
            路线规划
          </el-button>
          <el-button text @click="go('/scenic/list')">
            <el-icon><MapLocation /></el-icon>
            景点
          </el-button>
          <el-button text @click="go('/favorite')">
            <el-icon><Star /></el-icon>
            收藏
          </el-button>
          <el-divider direction="vertical" />
          <el-dropdown trigger="click">
            <button class="avatar-button" type="button">
              <img :src="authStore.user?.avatar || 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80'" alt="avatar" />
              <span>{{ authStore.user?.nickname || '未登录' }}</span>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <template v-if="authStore.isLoggedIn">
                  <el-dropdown-item @click="go('/user')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="go('/user/preferences')">偏好设置</el-dropdown-item>
                  <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                </template>
                <template v-else>
                  <el-dropdown-item @click="go('/auth/login')">登录</el-dropdown-item>
                  <el-dropdown-item @click="go('/auth/register')">注册</el-dropdown-item>
                </template>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="app-shell__content">
        <router-view />
      </main>

      <nav class="mobile-bar">
        <button type="button" :class="{ active: isActive('/') }" @click="go('/')"><el-icon><HomeFilled /></el-icon><span>首页</span></button>
        <button type="button" :class="{ active: isActive('/scenic') }" @click="go('/scenic/list')"><el-icon><MapLocation /></el-icon><span>景点</span></button>
        <button type="button" :class="{ active: isActive('/navigation') }" @click="go('/navigation')"><el-icon><Van /></el-icon><span>路线</span></button>
        <button type="button" :class="{ active: isActive('/recommend') }" @click="go('/recommend')"><el-icon><Star /></el-icon><span>推荐</span></button>
        <button type="button" :class="{ active: isActive('/user') }" @click="goUserCenter"><el-icon><UserFilled /></el-icon><span>我的</span></button>
      </nav>
    </div>

    <transition name="slide-fade">
      <div v-if="appStore.mobileMenuOpen" class="drawer-mask" @click="appStore.mobileMenuOpen = false">
        <aside class="drawer" @click.stop>
          <div class="drawer__head">
            <strong>模块导航</strong>
            <el-button text @click="appStore.mobileMenuOpen = false">关闭</el-button>
          </div>
          <div class="drawer__grid">
            <button v-for="group in navGroups" :key="group.title" class="drawer__block" type="button" @click="go(group.items[0].path)">
              <span class="drawer__title">{{ group.title }}</span>
              <span class="drawer__desc">{{ group.items.map((item) => item.label).join(' / ') }}</span>
            </button>
          </div>
        </aside>
      </div>
    </transition>
  </div>
</template>
