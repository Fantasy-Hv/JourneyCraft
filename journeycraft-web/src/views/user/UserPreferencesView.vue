<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getUserPreferences, updateUserPreferences } from '@/api/user'
import StatePanel from '@/components/StatePanel.vue'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const userId = computed(() => authStore.user?.id ?? null)
const isLoggedIn = computed(() => authStore.isLoggedIn && Boolean(userId.value))
const form = reactive({
  scenicTypes: ['scenic', 'campus'],
  interests: ['夜游', '拍照'],
  travelMode: 'walk',
  budgetLevel: 'medium',
  dietaryPreference: 'none',
})

async function load() {
  if (!userId.value) {
    return
  }
  const prefs = await getUserPreferences(userId.value).catch(() => form)
  Object.assign(form, prefs)
}

async function submit() {
  if (!userId.value) {
    router.push('/auth/login')
    return
  }
  loading.value = true
  try {
    await updateUserPreferences(userId.value, form)
    router.push('/user')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-card">
    <div class="page-card__head">
      <div>
        <h2>偏好设置</h2>
        <p>推荐、内容和路线会直接消费这份偏好配置。</p>
      </div>
      <el-button text @click="router.push('/user')">返回中心</el-button>
    </div>
    <StatePanel
      v-if="!isLoggedIn"
      title="当前未登录"
      description="请先登录，再查看或修改个人偏好。"
      kind="unauthorized"
    >
      <el-button type="primary" @click="router.push('/auth/login')">前往登录</el-button>
    </StatePanel>
    <template v-else>
    <el-form label-position="top" class="form-grid">
      <el-form-item label="兴趣标签">
        <el-select v-model="form.interests" multiple filterable allow-create default-first-option placeholder="选择兴趣标签" />
      </el-form-item>
      <el-form-item label="目的地类型">
        <el-select v-model="form.scenicTypes" multiple placeholder="景点 / 校园" />
      </el-form-item>
      <el-form-item label="出行方式">
        <el-segmented v-model="form.travelMode" :options="['walk', 'bike', 'shuttle']" />
      </el-form-item>
      <el-form-item label="预算">
        <el-segmented v-model="form.budgetLevel" :options="['low', 'medium', 'high']" />
      </el-form-item>
      <el-form-item label="饮食偏好">
        <el-input v-model="form.dietaryPreference" placeholder="none / vegetarian / spicy / ..." />
      </el-form-item>
    </el-form>
    <div class="page-actions">
      <el-button @click="router.push('/user')">取消</el-button>
      <el-button type="primary" :loading="loading" @click="submit">保存偏好</el-button>
    </div>
    </template>
  </div>
</template>
