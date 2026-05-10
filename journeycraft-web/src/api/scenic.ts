import { request } from './http'
import { mockService } from '@/mock/service'
import { invoke } from './common'
import type { Building, Campus, Facility, FoodPlace, ScenicArea } from '@/types/domain'
import type { PageResult } from '@/types/api'

type RawScenic = {
  id: number
  type: number
  name: string
  city: string
  address: string
  latitude?: number | null
  longitude?: number | null
  description?: string | null
  rating: number
  heatScore: number
  visitCount?: number
  ticketPrice?: number | string | null
  openingHours?: Record<string, unknown> | null
  images?: string[] | null
  tags?: string[] | null
}

function formatOpeningHours(openingHours?: Record<string, unknown> | null) {
  if (!openingHours) {
    return '开放时间待更新'
  }
  const daily = Array.isArray((openingHours as { daily?: unknown }).daily)
    ? ((openingHours as { daily?: Array<Record<string, string>> }).daily ?? [])
    : []
  const first = daily[0]
  if (first?.open && first?.close) {
    return `${first.open}-${first.close}`
  }
  const values = Object.values(openingHours)
  const firstObject = values.find((value): value is Record<string, string> => typeof value === 'object' && value !== null)
  if (firstObject?.open && firstObject?.close) {
    return `${firstObject.open}-${firstObject.close}`
  }
  return '开放时间待更新'
}

function toScenicArea(item: RawScenic): ScenicArea {
  return {
    id: item.id,
    scenicType: item.type === 1 ? 'campus' : 'scenic',
    name: item.name,
    city: item.city,
    address: item.address,
    cover: item.images?.[0] || '/media/scenic-card.svg',
    intro: item.description || '景点简介待补充',
    rating: Number(item.rating || 0),
    heatScore: Number(item.heatScore || 0),
    crowdLevel: Number(item.heatScore || 0) >= 9000 ? 'high' : Number(item.heatScore || 0) >= 5000 ? 'medium' : 'low',
    openTime: formatOpeningHours(item.openingHours),
    ticketPrice: item.ticketPrice == null ? '免费' : `¥${item.ticketPrice}`,
    tags: item.tags || [],
    highlights: item.tags || [],
  }
}

function normalizeScenicList(payload: ScenicArea[] | PageResult<RawScenic> | null | undefined): ScenicArea[] {
  if (Array.isArray(payload)) {
    return payload
  }
  if (payload && Array.isArray(payload.list)) {
    return payload.list.map(toScenicArea)
  }
  return []
}

export function listScenic() {
  return invoke(
    async () => (await mockService.scenic.list()).data as ScenicArea[],
    async () => normalizeScenicList(await request<PageResult<RawScenic>>({ url: '/scenic/list', method: 'GET' })),
  )
}

export function searchScenic(keyword = '') {
  return invoke(
    async () => (await mockService.scenic.search(keyword)).data as ScenicArea[],
    async () => normalizeScenicList(await request<PageResult<RawScenic>>({ url: '/scenic/search', method: 'GET', params: { keyword } })),
  )
}

export function getScenic(id: number) {
  return invoke(
    async () => (await mockService.scenic.detail(id)).data as ScenicArea | null,
    async () => {
      const data = await request<RawScenic | null>({ url: `/scenic/${id}`, method: 'GET' })
      return data ? toScenicArea(data) : null
    },
  )
}

export function listBuildings(scenicId: number) {
  return invoke(
    async () => (await mockService.scenic.buildings(scenicId)).data as Building[],
    () => request<Building[]>({ url: `/scenic/${scenicId}/buildings`, method: 'GET' }),
  )
}

export function getBuilding(id: number) {
  return invoke(
    async () => (await mockService.scenic.building(id)).data as Building | null,
    () => request<Building>({ url: `/scenic/buildings/${id}`, method: 'GET' }),
  )
}

export function listFacilities(scenicId: number) {
  return invoke(
    async () => (await mockService.scenic.facilities(scenicId)).data as Facility[],
    () => request<Facility[]>({ url: `/scenic/${scenicId}/facilities`, method: 'GET' }),
  )
}

export function getFacility(id: number) {
  return invoke(
    async () => (await mockService.scenic.facility(id)).data as Facility | null,
    () => request<Facility>({ url: `/scenic/facilities/${id}`, method: 'GET' }),
  )
}

export function listFoods(scenicId: number) {
  return invoke(
    async () => (await mockService.scenic.foods(scenicId)).data as FoodPlace[],
    () => request<FoodPlace[]>({ url: `/scenic/${scenicId}/foods`, method: 'GET' }),
  )
}

export function getFood(id: number) {
  return invoke(
    async () => (await mockService.scenic.food(id)).data as FoodPlace | null,
    () => request<FoodPlace>({ url: `/scenic/foods/${id}`, method: 'GET' }),
  )
}

export function listCampuses() {
  return invoke(
    async () => (await mockService.scenic.campuses()).data as Campus[],
    () => request<Campus[]>({ url: '/campus/list', method: 'GET' }),
  )
}

export function reportCrowd(scenicId: number, payload: Record<string, unknown>) {
  return invoke(
    async () => (await mockService.scenic.reportCrowd()).data as boolean,
    () => request<boolean>({ url: `/scenic/${scenicId}/report-crowd`, method: 'POST', data: payload }),
  )
}
