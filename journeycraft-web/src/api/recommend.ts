import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { FoodPlace, Diary, ScenicArea } from '@/types/domain'

export function recommendScenic() {
  return invoke(
    async () => (await mockService.recommend.scenic()).data as ScenicArea[],
    () => request<ScenicArea[]>({ url: '/recommend/scenic', method: 'GET' }),
  )
}

export function recommendPersonalized() {
  return invoke(
    async () => (await mockService.recommend.personalized()).data as ScenicArea[],
    () => request<ScenicArea[]>({ url: '/recommend/personalized', method: 'GET' }),
  )
}

export function recommendFoods() {
  return invoke(
    async () => (await mockService.recommend.foods()).data as FoodPlace[],
    () => request<FoodPlace[]>({ url: '/recommend/foods', method: 'GET' }),
  )
}

export function recommendDiaries() {
  return invoke(
    async () => (await mockService.recommend.diaries()).data as Diary[],
    () => request<Diary[]>({ url: '/recommend/diaries', method: 'GET' }),
  )
}

