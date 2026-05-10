<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Edit, Lock, Star, UserFilled } from '@element-plus/icons-vue'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import StatePanel from '@/components/StatePanel.vue'
import { useAuthStore } from '@/stores/auth'
import { getUserInfo, getUserPreferences } from '@/api/user'
import { mockUser } from '@/mock/data'

const router = useRouter()
const authStore = useAuthStore()
const user = ref(mockUser)
const preferences = ref<Record<string, unknown>>({})
const loading = ref(true)
const isLoggedIn = computed(() => authStore.isLoggedIn && Boolean(authStore.user?.id))

const userId = computed(() => authStore.user?.id ?? null)

async function load() {
  if (!userId.value) {
    loading.value = false
    return
  }
  loading.value = true
  try {
    const [info, prefs] = await Promise.all([
      getUserInfo(userId.value).catch(() => mockUser),
      getUserPreferences(userId.value).catch(() => ({ scenicTypes: ['scenic', 'campus'] })),
    ])
    user.value = info
    preferences.value = prefs
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      :title="user.nickname || '我的旅程'"
      subtitle="个人中心、偏好设置和密码修改都以 userId 作为显式参数，不再依赖 Authorization 请求头。"
      image="/media/social-card.svg"
      :badges="['用户模块', 'userId 参数', '资料 / 偏好 / 密码']"
    >
      <template #actions>
        <template v-if="isLoggedIn">
          <el-button type="primary" @click="router.push('/user/profile')"><el-icon><Edit /></el-icon>编辑资料</el-button>
          <el-button plain @click="router.push('/user/preferences')"><el-icon><Star /></el-icon>偏好设置</el-button>
          <el-button plain @click="router.push('/user/password')"><el-icon><Lock /></el-icon>修改密码</el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push('/auth/login')">立即登录</el-button>
          <el-button plain @click="router.push('/auth/register')">去注册</el-button>
        </template>
      </template>
    </PageHero>

    <div class="page-grid page-grid--two">
      <section class="panel">
        <SectionHeader title="个人信息" subtitle="展示用户资料、联系方式和当前登录状态。" />
        <StatePanel v-if="loading" title="正在加载用户数据" description="读取 userId 对应的信息与偏好。" kind="loading" />
        <StatePanel
          v-else-if="!isLoggedIn"
          title="当前未登录"
          description="登录后可查看个人资料、偏好设置、密码管理和收藏内容。"
          kind="unauthorized"
        >
          <el-button type="primary" @click="router.push('/auth/login')">前往登录</el-button>
        </StatePanel>
        <div v-else class="profile-card">
          <div class="profile-card__avatar">
            <img :src="user.avatar" alt="avatar" />
          </div>
          <div class="profile-card__body">
            <strong>{{ user.nickname }}</strong>
            <span>{{ user.username }}</span>
            <p>{{ user.email }}</p>
            <div class="profile-card__actions">
              <el-button type="primary" @click="router.push('/user/profile')"><el-icon><UserFilled /></el-icon>资料编辑</el-button>
              <el-button @click="router.push('/favorite')">查看收藏</el-button>
            </div>
          </div>
        </div>
      </section>

      <section class="panel">
        <SectionHeader title="偏好摘要" subtitle="后续推荐、日记和路线可直接消费这份配置。" />
        <div v-if="loading" class="stack">
          <StatePanel title="加载中" kind="loading" />
        </div>
        <StatePanel
          v-else-if="!isLoggedIn"
          title="登录后查看偏好摘要"
          description="推荐、美食、路线和内容模块都会使用这份偏好。"
          kind="unauthorized"
        />
        <div v-else class="summary-list">
          <div class="summary-list__item">
            <span>偏好类型</span>
            <strong>{{ Array.isArray(preferences.scenicTypes) ? (preferences.scenicTypes as string[]).join(' / ') : '未设置' }}</strong>
          </div>
          <div class="summary-list__item">
            <span>兴趣</span>
            <strong>{{ Array.isArray(preferences.interests) ? (preferences.interests as string[]).join(' / ') : '未设置' }}</strong>
          </div>
          <div class="summary-list__item">
            <span>旅行方式</span>
            <strong>{{ String(preferences.travelMode || 'walk') }}</strong>
          </div>
          <div class="summary-list__item">
            <span>预算</span>
            <strong>{{ String(preferences.budgetLevel || 'medium') }}</strong>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>
