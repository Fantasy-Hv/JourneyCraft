import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { AuthUser } from '@/types/domain'

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  password: string
  nickname: string
}

export interface AuthResult {
  token: string
  refreshToken: string
  expiresIn: number
  user: AuthUser
}

export interface RegisterResult {
  userId: number
}

export function login(payload: LoginPayload) {
  return invoke(
    async () => (await mockService.auth.login(payload)).data as AuthResult,
    () => request<AuthResult>({ url: '/auth/login', method: 'POST', data: payload }),
  )
}

export function register(payload: RegisterPayload) {
  return invoke(
    async () => {
      const data = (await mockService.auth.register(payload)).data as AuthResult
      return { userId: data.user.id } satisfies RegisterResult
    },
    () => request<RegisterResult>({ url: '/auth/register', method: 'POST', data: payload }),
  )
}

export function refreshToken() {
  return invoke(
    async () => (await mockService.auth.refresh()).data as Omit<AuthResult, 'user'>,
    () => request<Omit<AuthResult, 'user'>>({ url: '/auth/refresh', method: 'POST' }),
  )
}

export function logout() {
  return invoke(
    async () => (await mockService.auth.logout()).data as boolean,
    () => request<boolean>({ url: '/auth/logout', method: 'POST' }),
  )
}
