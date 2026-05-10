export type ScenicKind = 'scenic' | 'campus'
export type CrowdLevel = 'low' | 'medium' | 'high'
export type EntityStatus = 'online' | 'offline' | 'closed'
export type NavigationMode = 'walk' | 'bike' | 'shuttle'

export interface AuthUser {
  id: number
  username: string
  nickname: string
  avatar: string
  phone?: string
  email?: string
  token?: string
  refreshToken?: string
}

export interface ScenicArea {
  id: number
  scenicType: ScenicKind
  name: string
  city: string
  address: string
  cover: string
  intro: string
  rating: number
  heatScore: number
  crowdLevel: CrowdLevel
  openTime: string
  ticketPrice: string
  tags: string[]
  highlights: string[]
}

export interface Building {
  id: number
  scenicAreaId: number
  name: string
  buildingType: string
  floorNumber: string
  cover: string
  description: string
  openingHours: string
  heatScore: number
  tags: string[]
}

export interface Facility {
  id: number
  scenicAreaId: number
  name: string
  facilityType: string
  cover: string
  description: string
  nodeId?: number | null
  tags: string[]
}

export interface FoodPlace {
  id: number
  scenicAreaId: number
  buildingId: number | null
  nodeId: number | null
  sourceFacilityId: number | null
  name: string
  category: string
  cuisineType: string
  priceLevel: string
  avgPrice: number
  priceRange: string
  latitude: number
  longitude: number
  floorNumber: string
  description: string
  openingHours: string
  images: string[]
  tags: string[]
  contactInfo: string
  rating: number
  reviewCount: number
  heatScore: number
  recommendScore: number
  status: EntityStatus
  isDeleted: boolean
}

export interface Campus {
  id: number
  scenicAreaId: number
  name: string
  city: string
  cover: string
  intro: string
  tags: string[]
}

export interface Diary {
  id: number
  author: string
  title: string
  summary: string
  cover: string
  content: string
  scenicName: string
  createTime: string
  likeCount: number
  commentCount: number
  tags: string[]
}

export interface GroupPlan {
  id: number
  name: string
  scenicName: string
  dateRange: string
  members: number
  status: string
  summary: string
}

export interface ExpenseRecord {
  id: number
  title: string
  category: string
  amount: number
  time: string
  scenicName: string
  method: string
}

export interface FavoriteItem {
  id: number
  favoriteType: number
  entityId: number
  entityName: string
  entityCover: string
  scenicName: string
  createdAt: string
}

export interface HistoryItem {
  id: number
  type: 'view' | 'search' | 'navigation'
  title: string
  description: string
  createdAt: string
}

export interface FileAsset {
  id: number
  key: string
  name: string
  url: string
  type: 'image' | 'video'
  size: string
  createdAt: string
}

export interface NavigationRouteResult {
  routeId: number
  title: string
  transportMode: NavigationMode
  totalDistance: number
  estimatedTime: number
  summary: string
}

export interface SearchResultItem {
  id: number
  type: 'scenic' | 'node'
  name: string
  subtitle: string
  latitude?: number
  longitude?: number
  scenicType?: ScenicKind
  nodeType?: string
  heatScore?: number
}

export interface NavigationAccessNode {
  nodeId: number
  name: string
  transportMode: NavigationMode
  distance: number
  isPrimary?: boolean
  note?: string
}

export interface NearbyFacility {
  id: number
  name: string
  facilityType: string
  distance: number
  scenicName: string
  crowdLevel: CrowdLevel
}

export interface CongestionSnapshot {
  scenicAreaId: number
  scenicName: string
  crowdLevel: CrowdLevel
  crowdRatio: number
  updatedAt: string
}

export interface PhotoSpot {
  id: number
  scenicAreaId: number
  name: string
  latitude: number
  longitude: number
  note: string
}

export interface RouteSegment {
  type: 'walk' | 'bike' | 'push_bike' | 'shuttle' | 'walk_access' | 'walk_egress'
  distance: number
  time: number
  nodeIds: number[]
}

