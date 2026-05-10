import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { clearSession, getSavedToken, getSavedUser, saveSession } from '@/utils/auth'
import type { AuthUser } from '@/types/domain'

type SessionUser = AuthUser & {
  token?: string
  refreshToken?: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(getSavedToken())
  const user = ref<AuthUser | null>(getSavedUser())

  const isLoggedIn = computed(() => Boolean(token.value))

  function setSession(next: SessionUser) {
    token.value = next.token ?? ''
    user.value = next
    saveSession(next)
  }

  function patchUser(patch: Partial<AuthUser>) {
    user.value = user.value ? { ...user.value, ...patch } : (patch as AuthUser)
    if (user.value) {
      saveSession(user.value)
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    clearSession()
  }

  return {
    token,
    user,
    isLoggedIn,
    setSession,
    patchUser,
    logout,
  }
})
