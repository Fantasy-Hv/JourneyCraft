import { computed, ref, watch } from 'vue'
import { defineStore } from 'pinia'
import { getApiMode, setApiMode } from '@/utils/api-mode'
import type { ApiMode } from '@/types/api'

export const useAppStore = defineStore('app', () => {
  const apiMode = ref<ApiMode>(getApiMode())
  const mobileMenuOpen = ref(false)
  const globalKeyword = ref('')
  const activeModule = ref('home')

  const isRealMode = computed(() => apiMode.value === 'real')

  function toggleMode() {
    apiMode.value = apiMode.value === 'mock' ? 'real' : 'mock'
  }

  watch(apiMode, (value) => {
    setApiMode(value)
  }, { immediate: true })

  return {
    apiMode,
    isRealMode,
    mobileMenuOpen,
    globalKeyword,
    activeModule,
    toggleMode,
  }
})

