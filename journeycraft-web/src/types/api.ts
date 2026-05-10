export interface ApiResponse<T> {
  code?: number
  message?: string
  data: T
  success?: boolean
}

export type ApiMode = 'mock' | 'real'

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

