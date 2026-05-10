import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { ExpenseRecord } from '@/types/domain'

export function myBill() {
  return invoke(
    async () => (await mockService.expense.myBill()).data as ExpenseRecord[],
    () => request<ExpenseRecord[]>({ url: '/expense/my-bill', method: 'GET' }),
  )
}

export function expenseSummary() {
  return invoke(
    async () => (await mockService.expense.summary()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/expense/summary', method: 'GET' }),
  )
}

export function createExpense(payload: Partial<ExpenseRecord>) {
  return invoke(
    async () => (await mockService.expense.create(payload)).data as ExpenseRecord,
    () => request<ExpenseRecord>({ url: '/expense', method: 'POST', data: payload }),
  )
}

export function autoGenerateExpense() {
  return invoke(
    async () => (await mockService.expense.autoGenerate()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/expense/auto-generate', method: 'POST' }),
  )
}

