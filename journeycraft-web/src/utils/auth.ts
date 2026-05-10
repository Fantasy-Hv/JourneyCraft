import type { AuthUser } from '@/types/domain'

const TOKEN_KEY = 'journeycraft.token'
const REFRESH_KEY = 'journeycraft.refreshToken'
const USER_KEY = 'journeycraft.user'

export function getSavedToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function saveSession(user: AuthUser) {
  if (user.token) {
    localStorage.setItem(TOKEN_KEY, user.token)
  }
  if (user.refreshToken) {
    localStorage.setItem(REFRESH_KEY, user.refreshToken)
  }
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getSavedUser(): AuthUser | null {
  const raw = localStorage.getItem(USER_KEY)
  return raw ? (JSON.parse(raw) as AuthUser) : null
}

