import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { HistoryItem } from '@/types/domain'

export function viewHistory() {
  return invoke(
    async () => (await mockService.history.view()).data as HistoryItem[],
    () => request<HistoryItem[]>({ url: '/history/view', method: 'GET' }),
  )
}

export function searchHistory() {
  return invoke(
    async () => (await mockService.history.search()).data as HistoryItem[],
    () => request<HistoryItem[]>({ url: '/history/search', method: 'GET' }),
  )
}

export function navigationHistory() {
  return invoke(
    async () => (await mockService.history.navigation()).data as HistoryItem[],
    () => request<HistoryItem[]>({ url: '/history/navigation', method: 'GET' }),
  )
}

export function addHistoryView() {
  return invoke(
    async () => (await mockService.history.addView()).data as boolean,
    () => request<boolean>({ url: '/history/view', method: 'POST' }),
  )
}

export function clearHistory() {
  return invoke(
    async () => (await mockService.history.clear()).data as boolean,
    () => request<boolean>({ url: '/history/clear', method: 'DELETE' }),
  )
}

