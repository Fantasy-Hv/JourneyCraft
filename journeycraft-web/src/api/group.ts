import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { GroupPlan } from '@/types/domain'

export function listGroups() {
  return invoke(
    async () => (await mockService.group.list()).data as GroupPlan[],
    () => request<GroupPlan[]>({ url: '/group/list', method: 'GET' }),
  )
}

export function createGroup(payload: Partial<GroupPlan>) {
  return invoke(
    async () => (await mockService.group.create(payload)).data as GroupPlan,
    () => request<GroupPlan>({ url: '/group/create', method: 'POST', data: payload }),
  )
}

export function getGroup(id: number) {
  return invoke(
    async () => (await mockService.group.detail(id)).data as GroupPlan,
    () => request<GroupPlan>({ url: `/group/${id}`, method: 'GET' }),
  )
}

export function joinGroup(id: number) {
  return invoke(
    async () => (await mockService.group.join(id)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/group/${id}/join`, method: 'POST' }),
  )
}

export function submitGroupPreference(id: number, payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.group.preference()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/group/${id}/preference`, method: 'POST', data: payload }),
  )
}

export function generateGroupPlan(id: number, payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.group.generatePlan()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/group/${id}/generate-plan`, method: 'POST', data: payload }),
  )
}

export function groupExpense(id: number) {
  return invoke(
    async () => (await mockService.group.expense()).data as unknown[],
    () => request<unknown[]>({ url: `/group/${id}/expense`, method: 'GET' }),
  )
}

