<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Lock, User, Avatar } from '@element-plus/icons-vue'
import { login, register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import PageHero from '@/components/PageHero.vue'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '', nickname: '' })

async function submit() {
  loading.value = true
  try {
    await register(form)
    const result = await login({
      username: form.username,
      password: form.password,
    })
    authStore.setSession({
      ...result.user,
      token: result.token,
      refreshToken: result.refreshToken,
    })
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-card">
    <PageHero
      title="创建新账户"
      subtitle="注册后即可保存旅程偏好、收藏、协同计划和游记内容。"
      image="/media/route-card.svg"
      :badges="['旅行', '协同', '内容']"
    />

    <el-form class="auth-form" label-position="top" @submit.prevent="submit">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="设置登录账号">
          <template #prefix><el-icon><User /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" placeholder="对外展示的昵称">
          <template #prefix><el-icon><Avatar /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" show-password placeholder="至少 6 位">
          <template #prefix><el-icon><Lock /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-button :loading="loading" type="primary" class="wide-button" @click="submit">注册并进入</el-button>
      <div class="auth-links">
        <el-button text @click="router.push('/auth/login')">返回登录</el-button>
        <el-button text @click="router.push('/')">浏览首页</el-button>
      </div>
    </el-form>
  </div>
</template>
