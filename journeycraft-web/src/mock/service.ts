import type {
  AuthUser,
  Diary,
  ExpenseRecord,
  FavoriteItem,
  GroupPlan,
  RouteSegment,
} from '@/types/domain'
import {
  buildings,
  campuses,
  congestionSnapshots,
  diaries,
  expenses,
  favorites,
  files,
  facilities,
  foods,
  groups,
  historyItems,
  nearbyFacilities,
  mockUser,
  photoSpots,
  routeCard,
  scenicAreas,
  searchResults,
} from './data'

const wait = (ms = 260) => new Promise((resolve) => window.setTimeout(resolve, ms))

const clone = <T,>(value: T): T => JSON.parse(JSON.stringify(value)) as T

const response = <T,>(data: T) => ({ code: 200, message: 'success', data, success: true })

const pagination = <T,>(list: T[], page = 1, size = 10) => {
  const start = (page - 1) * size
  return {
    list: clone(list.slice(start, start + size)),
    total: list.length,
    page,
    size,
  }
}

function byId<T extends { id: number }>(source: T[], id: number) {
  return source.find((item) => item.id === id) ?? null
}

export const mockService = {
  auth: {
    async login(payload: { username: string; password: string }) {
      await wait()
      return response({
        token: 'mock-token-' + payload.username,
        refreshToken: 'mock-refresh-token',
        expiresIn: 7200,
        user: {
          ...clone(mockUser),
          username: payload.username,
        } satisfies AuthUser,
      })
    },
    async register(payload: { username: string; password: string; nickname: string }) {
      await wait()
      return response({
        token: 'mock-token-' + payload.username,
        refreshToken: 'mock-refresh-token',
        expiresIn: 7200,
        user: {
          ...clone(mockUser),
          username: payload.username,
          nickname: payload.nickname,
        } satisfies AuthUser,
      })
    },
    async refresh() {
      await wait(150)
      return response({
        token: 'mock-token-refreshed',
        refreshToken: 'mock-refresh-token',
        expiresIn: 7200,
      })
    },
    async logout() {
      await wait(100)
      return response(true)
    },
  },
  user: {
    async info(userId: number) {
      await wait(160)
      return response({
        ...clone(mockUser),
        id: userId,
      })
    },
    async updateInfo(userId: number, payload: Partial<AuthUser>) {
      await wait(160)
      return response({
        ...clone(mockUser),
        id: userId,
        ...payload,
      })
    },
    async password() {
      await wait(140)
      return response(true)
    },
    async preferences(userId: number) {
      await wait(140)
      return response({
        userId,
        scenicTypes: ['scenic', 'campus'],
        interests: ['夜游', '拍照', '文艺', '美食'],
        travelMode: 'walk',
        budgetLevel: 'medium',
        dietaryPreference: 'none',
      })
    },
    async updatePreferences(userId: number, payload: Record<string, unknown>) {
      await wait(140)
      return response({ userId, ...payload })
    },
  },
  scenic: {
    async list() {
      await wait()
      return response(clone(scenicAreas))
    },
    async search(keyword = '') {
      await wait()
      const query = keyword.trim().toLowerCase()
      const filtered = query
        ? scenicAreas.filter((item) => item.name.toLowerCase().includes(query) || item.tags.some((tag) => tag.includes(keyword)))
        : scenicAreas
      return response(clone(filtered))
    },
    async detail(id: number) {
      await wait()
      const scenic = byId(scenicAreas, id)
      return response(clone(scenic))
    },
    async buildings(id: number) {
      await wait()
      return response(clone(buildings.filter((item) => item.scenicAreaId === id)))
    },
    async building(id: number) {
      await wait()
      return response(clone(byId(buildings, id)))
    },
    async facilities(id: number) {
      await wait()
      return response(clone(facilities.filter((item) => item.scenicAreaId === id)))
    },
    async facility(id: number) {
      await wait()
      return response(clone(byId(facilities, id)))
    },
    async foods(id: number) {
      await wait()
      return response(clone(foods.filter((item) => item.scenicAreaId === id)))
    },
    async food(id: number) {
      await wait()
      return response(clone(byId(foods, id)))
    },
    async campuses() {
      await wait()
      return response(clone(campuses))
    },
    async reportCrowd() {
      await wait(120)
      return response(true)
    },
  },
  navigation: {
    async search(keyword = '', types = 'scenic,node', page = 1, size = 10) {
      await wait()
      const query = keyword.trim().toLowerCase()
      const list = searchResults.filter((item) => {
        const matchKeyword = !query || item.name.toLowerCase().includes(query) || item.subtitle.toLowerCase().includes(query)
        const matchType = types.includes(item.type)
        return matchKeyword && matchType
      })
      return response({ scenicResults: list.filter((item) => item.type === 'scenic'), nodeResults: list.filter((item) => item.type === 'node'), ...pagination(list, page, size) })
    },
    async nearestEdge() {
      await wait()
      return response({
        edgeId: 262800,
        snapLat: 30.2338,
        snapLng: 120.083,
        snapPosition: 0.35,
        distance: 12.3,
        edgeInfo: { fromNodeId: 418811, toNodeId: 418812, highwayType: 'service', transportModes: ['walk', 'bike'], name: null },
        connectedComponentSize: 15623,
        confidence: 'high',
        alternatives: [{ type: 'nearest_walkable', nodeId: 67890, distance: 850 }],
      })
    },
    async accessNodes(id: number) {
      await wait()
      return response({
        scenicAreaId: id,
        scenicAreaName: scenicAreas.find((item) => item.id === id)?.name || '',
        scenicCenter: { latitude: 30.233, longitude: 120.081 },
        accessNodes: {
          walk: [{ nodeId: 12345, name: '东门入口', nodeType: 0, latitude: 30.234, longitude: 120.082, distance: 120, isPrimary: true }],
          bike: [{ nodeId: 12345, name: '东门入口', nodeType: 0, latitude: 30.234, longitude: 120.082, distance: 120, viaWalk: true }],
          shuttle: [{ nodeId: 99999, name: '停车场换乘点', distance: 580 }],
        },
      })
    },
    async poiNodes(id: number) {
      await wait()
      return response([
        { nodeId: 54321, name: '云栖观景餐厅', nodeType: 2, poiType: 'restaurant', latitude: 30.2336, longitude: 120.0821, description: '湖景餐厅', facilityId: null, scenicAreaId: id },
        { nodeId: 54322, name: '游客中心', nodeType: 2, poiType: 'toilet', latitude: 30.2338, longitude: 120.0813, description: '服务中心', facilityId: 401, scenicAreaId: id },
      ])
    },
    async route(payload: Record<string, unknown>) {
      await wait()
      const segments: RouteSegment[] = [
        { type: 'walk_access', distance: 220, time: 210, nodeIds: [12345, 12346] },
        { type: 'walk', distance: 980, time: 840, nodeIds: [12346, 12347] },
      ]
      return response({
        routeId: 42,
        totalDistance: 1200,
        estimatedTime: 1050,
        transportMode: (payload.transportMode as string) || 'walk',
        strategy: (payload.strategy as string) || 'shortest_distance',
        segments,
        path: [
          { lat: 30.233, lng: 120.081, segmentType: 'walk_access' },
          { lat: 30.234, lng: 120.082, segmentType: 'walk' },
          { lat: 30.236, lng: 120.084, segmentType: 'walk' },
        ],
        startInfo: { inputType: 'scenicArea', scenicAreaName: scenicAreas[0]?.name, resolvedNodeId: 12345, latitude: 30.233, longitude: 120.081 },
        endInfo: { inputType: 'coordinate', resolvedNodeId: 12347, latitude: 30.236, longitude: 120.084 },
      })
    },
    async multiRoute() {
      await wait()
      return response({
        routeId: 43,
        totalDistance: 1580,
        estimatedTime: 1320,
        summary: '串联东门、云栖书院和夜游餐厅的三点联动路线。',
      })
    },
    async nearbyFacilities() {
      await wait()
      return response(clone(nearbyFacilities))
    },
    async indoorRoute() {
      await wait()
      return response({
        routeId: 44,
        level: 'B1 -> 2F',
        summary: '室内导航当前为占位，支持后续接入楼层级路线。',
      })
    },
    async congestion(id: number) {
      await wait()
      const match = congestionSnapshots.find((item) => item.scenicAreaId === id) || congestionSnapshots[0]
      return response(clone(match))
    },
    async photoSpots(id: number) {
      await wait()
      return response(clone(photoSpots.filter((item) => item.scenicAreaId === id)))
    },
    async alternativeRoute() {
      await wait()
      return response({
        title: '反向游览建议',
        summary: '先逛建筑再去湖边，避开晚间高峰。',
        alternatives: [
          { label: '逆向路线 A', distance: 1120, estimatedTime: 940 },
          { label: '逆向路线 B', distance: 1460, estimatedTime: 1160 },
        ],
      })
    },
    async health() {
      await wait(60)
      return response({ status: 'ok', timestamp: new Date().toISOString() })
    },
  },
  recommend: {
    async scenic() {
      await wait()
      return response(clone(scenicAreas))
    },
    async personalized() {
      await wait()
      return response(clone(scenicAreas.slice(0, 3)))
    },
    async foods() {
      await wait()
      return response(clone(foods))
    },
    async diaries() {
      await wait()
      return response(clone(diaries))
    },
  },
  group: {
    async list() {
      await wait()
      return response(clone(groups))
    },
    async create(payload: Partial<GroupPlan>) {
      await wait()
      return response({ id: Date.now(), ...payload })
    },
    async detail(id: number) {
      await wait()
      return response(clone(groups.find((item) => item.id === id) || groups[0]))
    },
    async join(id: number) {
      await wait()
      return response({ id, joined: true })
    },
    async preference() {
      await wait()
      return response({ submitted: true })
    },
    async generatePlan() {
      await wait()
      return response({ status: 'queued', planId: 30001 })
    },
    async expense() {
      await wait()
      return response(clone(expenses))
    },
  },
  diary: {
    async list() {
      await wait()
      return response(clone(diaries))
    },
    async detail(id: number) {
      await wait()
      return response(clone(diaries.find((item) => item.id === id) || diaries[0]))
    },
    async create(payload: Partial<Diary>) {
      await wait()
      return response({ id: Date.now(), ...payload })
    },
    async update(id: number, payload: Partial<Diary>) {
      await wait()
      return response({ id, ...payload })
    },
    async delete(id: number) {
      await wait(100)
      return response({ id, deleted: true })
    },
    async like(id: number) {
      await wait(80)
      return response({ id, liked: true })
    },
    async comment(id: number, content: string) {
      await wait(80)
      return response({ id, content, createdAt: new Date().toISOString() })
    },
    async comments(id: number) {
      await wait()
      return response([{ id: 1, diaryId: id, author: '岚川', content: '很实用的路线。', createdAt: '05-06 21:50' }])
    },
    async autoGenerate() {
      await wait()
      return response({ status: 'queued', draftId: 40001 })
    },
  },
  expense: {
    async myBill() {
      await wait()
      return response(clone(expenses))
    },
    async summary() {
      await wait()
      return response({ total: 192, categories: [{ name: '餐饮', value: 124 }, { name: '门票', value: 68 }] })
    },
    async create(payload: Partial<ExpenseRecord>) {
      await wait()
      return response({ id: Date.now(), ...payload })
    },
    async autoGenerate() {
      await wait()
      return response({ status: 'queued' })
    },
  },
  favorite: {
    async list() {
      await wait()
      return response(clone(favorites))
    },
    async collections() {
      await wait()
      return response([
        { id: 1, name: '必去景点', count: 2 },
        { id: 2, name: '拍照路线', count: 1 },
      ])
    },
    async check(entityId: number) {
      await wait(80)
      return response({ entityId, favorite: favorites.some((item) => item.entityId === entityId) })
    },
    async add(payload: Partial<FavoriteItem>) {
      await wait(80)
      return response({ id: Date.now(), ...payload })
    },
    async remove(id: number) {
      await wait(80)
      return response({ id, deleted: true })
    },
    async move(id: number, collectionId: number) {
      await wait(80)
      return response({ id, collectionId })
    },
    async batch(ids: number[]) {
      await wait(80)
      return response({ ids, deleted: true })
    },
    async createCollection(name: string) {
      await wait(80)
      return response({ id: Date.now(), name })
    },
    async updateCollection(id: number, name: string) {
      await wait(80)
      return response({ id, name })
    },
    async deleteCollection(id: number) {
      await wait(80)
      return response({ id, deleted: true })
    },
  },
  history: {
    async view() {
      await wait()
      return response(clone(historyItems.filter((item) => item.type === 'view')))
    },
    async search() {
      await wait()
      return response(clone(historyItems.filter((item) => item.type === 'search')))
    },
    async navigation() {
      await wait()
      return response(clone(historyItems.filter((item) => item.type === 'navigation')))
    },
    async addView() {
      await wait(60)
      return response(true)
    },
    async clear() {
      await wait(60)
      return response(true)
    },
  },
  file: {
    async uploadImage(file: File) {
      await wait()
      return response({ id: Date.now(), key: 'image-' + Date.now(), name: file.name, url: scenicAreas[0].cover, type: 'image', size: `${Math.max(1, Math.round(file.size / 1024))} KB`, createdAt: new Date().toISOString() })
    },
    async uploadVideo(file: File) {
      await wait()
      return response({ id: Date.now(), key: 'video-' + Date.now(), name: file.name, url: routeCard, type: 'video', size: `${Math.max(1, Math.round(file.size / 1024))} KB`, createdAt: new Date().toISOString() })
    },
    async detail(key: string) {
      await wait()
      return response(files.find((item) => item.key === key) || files[0])
    },
    async remove(key: string) {
      await wait(80)
      return response({ key, deleted: true })
    },
  },
}

export const mockLookup = {
  scenicAreas,
  buildings,
  facilities,
  foods,
  campuses,
  diaries,
  groups,
  expenses,
  favorites,
  historyItems,
  files,
  searchResults,
  congestionSnapshots,
  nearbyFacilities,
  photoSpots,
}
