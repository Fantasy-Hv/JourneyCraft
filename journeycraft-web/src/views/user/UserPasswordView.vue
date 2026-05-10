<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { updateUserPassword } from '@/api/user'
import StatePanel from '@/components/StatePanel.vue'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const userId = computed(() => authStore.user?.id ?? null)
const isLoggedIn = computed(() => authStore.isLoggedIn && Boolean(userId.value))
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

async function submit() {
  if (!userId.value) {
    router.push('/auth/login')
    return
  }
  loading.value = true
  try {
    await updateUserPassword(userId.value, {
      oldPassword: form.oldPassword,
      newPassword: form.newPassword,
    })
    router.push('/user')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-card">
    <div class="page-card__head">
      <div>
        <h2>修改密码</h2>
        <p>当前阶段仍按显式 userId 提交。</p>
      </div>
      <el-button text @click="router.push('/user')">返回中心</el-button>
    </div>
    <StatePanel
      v-if="!isLoggedIn"
      title="当前未登录"
      description="请先登录，再修改密码。"
      kind="unauthorized"
    >
      <el-button type="primary" @click="router.push('/auth/login')">前往登录</el-button>
    </StatePanel>
    <template v-else>
    <el-form label-position="top" class="form-grid">
      <el-form-item label="旧密码">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="form.confirmPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <div class="page-actions">
      <el-button @click="router.push('/user')">取消</el-button>
      <el-button type="primary" :loading="loading" @click="submit">更新密码</el-button>
    </div>
    </template>
  </div>
</template>
