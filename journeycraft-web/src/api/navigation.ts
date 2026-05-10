import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { NavigationRouteResult, NearbyFacility, SearchResultItem } from '@/types/domain'

export function searchKeyword(keyword = '', types = 'scenic,node', page = 1, size = 10) {
  return invoke(
    async () => (await mockService.navigation.search(keyword, types, page, size)).data as { scenicResults: SearchResultItem[]; nodeResults: SearchResultItem[]; total: number; page: number; size: number },
    () => request<{ scenicResults: SearchResultItem[]; nodeResults: SearchResultItem[]; total: number; page: number; size: number }>({ url: '/search', method: 'GET', params: { keyword, types, page, size } }),
  )
}

export function nearestEdge(params: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.navigation.nearestEdge()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/navigation/nearest-edge', method: 'GET', params }),
  )
}

export function accessNodes(scenicId: number, params: Record<string, unknown> = {}) {
  return invoke(
    async () => (await mockService.navigation.accessNodes(scenicId)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/navigation/scenic/${scenicId}/access-nodes`, method: 'GET', params }),
  )
}

export function poiNodes(scenicId: number, params: Record<string, unknown> = {}) {
  return invoke(
    async () => (await mockService.navigation.poiNodes(scenicId)).data as unknown[],
    () => request<unknown[]>({ url: `/navigation/scenic/${scenicId}/poi-nodes`, method: 'GET', params }),
  )
}

export function route(payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.navigation.route(payload)).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/navigation/route', method: 'POST', data: payload }),
  )
}

export function multiRoute(payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.navigation.multiRoute()).data as NavigationRouteResult,
    () => request<NavigationRouteResult>({ url: '/navigation/multi-route', method: 'POST', data: payload }),
  )
}

export function nearbyFacilities(params: Record<string, unknown> = {}) {
  return invoke(
    async () => (await mockService.navigation.nearbyFacilities()).data as NearbyFacility[],
    () => request<NearbyFacility[]>({ url: '/navigation/facilities/nearby', method: 'GET', params }),
  )
}

export function indoorRoute(payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.navigation.indoorRoute()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/navigation/indoor/route', method: 'POST', data: payload }),
  )
}

export function congestion(scenicId: number) {
  return invoke(
    async () => (await mockService.navigation.congestion(scenicId)).data as unknown as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: `/navigation/congestion/${scenicId}`, method: 'GET' }),
  )
}

export function photoSpots(scenicId: number) {
  return invoke(
    async () => (await mockService.navigation.photoSpots(scenicId)).data as unknown[],
    () => request<unknown[]>({ url: `/navigation/photo-spots/${scenicId}`, method: 'GET' }),
  )
}

export function alternativeRoute(params: Record<string, unknown> = {}) {
  return invoke(
    async () => (await mockService.navigation.alternativeRoute()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/navigation/alternative-route', method: 'GET', params }),
  )
}

export function health() {
  return invoke(
    async () => (await mockService.navigation.health()).data as Record<string, unknown>,
    () => request<Record<string, unknown>>({ url: '/navigation/health', method: 'GET' }),
  )
}
