<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Lock, User } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import PageHero from '@/components/PageHero.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function submit() {
  loading.value = true
  try {
    const result = await login(form)
    authStore.setSession({
      ...result.user,
      token: result.token,
      refreshToken: result.refreshToken,
    })
    router.push((route.query.redirect as string) || '/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-card">
    <PageHero
      title="登录 JourneyCraft"
      subtitle="进入完整的旅行消费产品体验。登录后可继续查看收藏、日记、小组、账单和资料。"
      image="/media/social-card.svg"
      :badges="['auth', 'userId', 'token']"
    />

    <el-form class="auth-form" label-position="top" @submit.prevent="submit">
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="用户名 / 手机号 / 邮箱">
          <template #prefix><el-icon><User /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" show-password placeholder="请输入密码">
          <template #prefix><el-icon><Lock /></el-icon></template>
        </el-input>
      </el-form-item>
      <el-button :loading="loading" type="primary" class="wide-button" @click="submit">登录</el-button>
      <div class="auth-links">
        <el-button text @click="router.push('/auth/register')">去注册</el-button>
        <el-button text @click="router.push('/')">先看看首页</el-button>
      </div>
    </el-form>
  </div>
</template>
