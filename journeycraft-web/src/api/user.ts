import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { AuthUser } from '@/types/domain'

export function getUserInfo(userId: number) {
  return invoke(
    async () => (await mockService.user.info(userId)).data as AuthUser,
    () => request<AuthUser>({ url: '/user/info', method: 'GET', params: { userId } }),
  )
}

export function updateUserInfo(userId: number, payload: Partial<AuthUser>) {
  return invoke(
    async () => (await mockService.user.updateInfo(userId, payload)).data as AuthUser,
    () => request<AuthUser>({ url: '/user/info', method: 'PUT', params: { userId }, data: payload }),
  )
}

export function updateUserPassword(userId: number, payload: { oldPassword: string; newPassword: string }) {
  return invoke(
    async () => (await mockService.user.password()).data as boolean,
    () => request<boolean>({ url: '/user/password', method: 'PUT', params: { userId }, data: payload }),
  )
}

export function getUserPreferences(userId: number) {
  return invoke(
    async () => (await mockService.user.preferences(userId)).data,
    () => request<Record<string, unknown>>({ url: '/user/preferences', method: 'GET', params: { userId } }),
  )
}

export function updateUserPreferences(userId: number, payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.user.updatePreferences(userId, payload)).data,
    () => request<Record<string, unknown>>({ url: '/user/preferences', method: 'PUT', params: { userId }, data: payload }),
  )
}

