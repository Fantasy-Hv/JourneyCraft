<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getUserInfo, updateUserInfo } from '@/api/user'
import { mockUser } from '@/mock/data'
import StatePanel from '@/components/StatePanel.vue'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const userId = computed(() => authStore.user?.id ?? null)
const isLoggedIn = computed(() => authStore.isLoggedIn && Boolean(userId.value))
const form = reactive({
  nickname: '',
  phone: '',
  email: '',
})

async function load() {
  if (!userId.value) {
    return
  }
  const data = await getUserInfo(userId.value).catch(() => mockUser)
  form.nickname = data.nickname
  form.phone = data.phone || ''
  form.email = data.email || ''
}

async function submit() {
  if (!userId.value) {
    router.push('/auth/login')
    return
  }
  loading.value = true
  try {
    await updateUserInfo(userId.value, form)
    authStore.patchUser(form)
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
        <h2>编辑资料</h2>
        <p>表单通过 userId 参数提交，不再依赖 Authorization header。</p>
      </div>
      <el-button text @click="router.push('/user')">返回中心</el-button>
    </div>
    <StatePanel
      v-if="!isLoggedIn"
      title="当前未登录"
      description="请先登录，再编辑个人资料。"
      kind="unauthorized"
    >
      <el-button type="primary" @click="router.push('/auth/login')">前往登录</el-button>
    </StatePanel>
    <template v-else>
    <el-form label-position="top" class="form-grid">
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" />
      </el-form-item>
    </el-form>
    <div class="page-actions">
      <el-button @click="router.push('/user')">取消</el-button>
      <el-button type="primary" :loading="loading" @click="submit">保存资料</el-button>
    </div>
    </template>
  </div>
</template>
