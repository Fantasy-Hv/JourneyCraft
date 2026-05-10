import type { ApiMode } from '@/types/api'

const STORAGE_KEY = 'journeycraft.apiMode'

export function getApiMode(): ApiMode {
  const value = localStorage.getItem(STORAGE_KEY)
  return value === 'real' ? 'real' : 'mock'
}

export function setApiMode(mode: ApiMode) {
  localStorage.setItem(STORAGE_KEY, mode)
}

