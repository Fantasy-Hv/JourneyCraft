import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { FavoriteItem } from '@/types/domain'

export function listFavorites() {
  return invoke(
    async () => (await mockService.favorite.list()).data as FavoriteItem[],
    () => request<FavoriteItem[]>({ url: '/favorite/list', method: 'GET' }),
  )
}

export function checkFavorite(entityId: number) {
  return invoke(
    async () => (await mockService.favorite.check(entityId)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/favorite/check', method: 'GET', params: { entityId } }),
  )
}

export function addFavorite(payload: Partial<FavoriteItem>) {
  return invoke(
    async () => (await mockService.favorite.add(payload)).data as FavoriteItem,
    () => request<FavoriteItem>({ url: '/favorite', method: 'POST', data: payload }),
  )
}

export function removeFavorite(id: number) {
  return invoke(
    async () => (await mockService.favorite.remove(id)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/favorite/${id}`, method: 'DELETE' }),
  )
}

export function moveFavorite(id: number, collectionId: number) {
  return invoke(
    async () => (await mockService.favorite.move(id, collectionId)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/favorite/${id}/move`, method: 'PUT', data: { collectionId } }),
  )
}

export function batchDeleteFavorites(ids: number[]) {
  return invoke(
    async () => (await mockService.favorite.batch(ids)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/favorite/batch', method: 'DELETE', data: { ids } }),
  )
}

export function listFavoriteCollections() {
  return invoke(
    async () => (await mockService.favorite.collections()).data as Array<{ id: number; name: string; count: number }>,
    () => request<Array<{ id: number; name: string; count: number }>>({ url: '/favorite/collections', method: 'GET' }),
  )
}

export function createFavoriteCollection(name: string) {
  return invoke(
    async () => (await mockService.favorite.createCollection(name)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/favorite/collection', method: 'POST', data: { name } }),
  )
}

export function updateFavoriteCollection(id: number, name: string) {
  return invoke(
    async () => (await mockService.favorite.updateCollection(id, name)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/favorite/collection/${id}`, method: 'PUT', data: { name } }),
  )
}

export function deleteFavoriteCollection(id: number) {
  return invoke(
    async () => (await mockService.favorite.deleteCollection(id)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/favorite/collection/${id}`, method: 'DELETE' }),
  )
}

