import hero from '@/assets/hero-travel.svg'
import scenicCard from '@/assets/scenic-card.svg'
import foodCard from '@/assets/food-card.svg'
import routeCard from '@/assets/route-card.svg'
import socialCard from '@/assets/social-card.svg'
import type {
  AuthUser,
  Building,
  Campus,
  CongestionSnapshot,
  Diary,
  ExpenseRecord,
  FavoriteItem,
  Facility,
  FoodPlace,
  GroupPlan,
  HistoryItem,
  NearbyFacility,
  PhotoSpot,
  ScenicArea,
  SearchResultItem,
  FileAsset,
} from '@/types/domain'

export { routeCard }

export const mockMedia = {
  hero,
  scenicCard,
  foodCard,
  routeCard,
  socialCard,
}

export const mockUser: AuthUser = {
  id: 1001,
  username: 'jctraveler',
  nickname: '岚川',
  avatar:
    'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80',
  phone: '188****2388',
  email: 'lanchuan@example.com',
}

export const scenicAreas: ScenicArea[] = [
  {
    id: 1,
    scenicType: 'scenic',
    name: '云栖湖文化度假区',
    city: '杭州',
    address: '杭州市西湖区云栖路 88 号',
    cover: scenicCard,
    intro: '湖景、古风街区与夜游线路并行，适合周末慢旅行与拍照打卡。',
    rating: 4.9,
    heatScore: 9821,
    crowdLevel: 'medium',
    openTime: '08:30-22:00',
    ticketPrice: '¥68 起',
    tags: ['湖景', '古风', '夜游', '拍照'],
    highlights: ['夜景灯光', '文化展馆', '湖畔餐饮', '亲子游线'],
  },
  {
    id: 2,
    scenicType: 'campus',
    name: '栖山理工校园',
    city: '南京',
    address: '南京市江宁区大学城创新大道 18 号',
    cover: socialCard,
    intro: '兼具校园景观、展陈空间与食堂美食，是校园漫游和协同导航的试验场。',
    rating: 4.8,
    heatScore: 7350,
    crowdLevel: 'low',
    openTime: '全天开放',
    ticketPrice: '免费',
    tags: ['校园', '文艺', '二次元', '散步'],
    highlights: ['图书馆', '校园咖啡', '林荫道路', '地标建筑'],
  },
  {
    id: 3,
    scenicType: 'scenic',
    name: '星港滨海夜游中心',
    city: '厦门',
    address: '厦门市思明区海滨东路 2 号',
    cover: routeCard,
    intro: '将滨海观景、夜游巴士和观景点串联，适合路线规划与拥挤度查看。',
    rating: 4.7,
    heatScore: 6832,
    crowdLevel: 'high',
    openTime: '09:00-23:30',
    ticketPrice: '¥39 起',
    tags: ['海景', '夜游', '拍照', '美食'],
    highlights: ['观景栈道', '海边餐厅', '夜游接驳', '航拍视角'],
  },
  {
    id: 4,
    scenicType: 'campus',
    name: '南山文创学院',
    city: '成都',
    address: '成都市武侯区文创路 6 号',
    cover: hero,
    intro: '面向年轻人和创意活动，提供日记、组队和协同活动的典型场景。',
    rating: 4.6,
    heatScore: 5122,
    crowdLevel: 'medium',
    openTime: '全天开放',
    ticketPrice: '免费',
    tags: ['创意', '动漫', '展览', '协同'],
    highlights: ['活动广场', '展厅', '共享工坊', '社群路线'],
  },
]

export const buildings: Building[] = [
  {
    id: 201,
    scenicAreaId: 1,
    name: '云栖书院',
    buildingType: '展馆',
    floorNumber: '2 层',
    cover: scenicCard,
    description: '以历史展陈与阅读空间为主的复合建筑，适合慢逛和拍照。',
    openingHours: '09:00-20:00',
    heatScore: 3642,
    tags: ['历史', '古风', '拍照'],
  },
  {
    id: 202,
    scenicAreaId: 1,
    name: '湖畔夜游中心',
    buildingType: '服务中心',
    floorNumber: '1 层',
    cover: routeCard,
    description: '夜游接驳与多媒体导览集中区，承载路线规划与拥挤度展示。',
    openingHours: '08:30-22:30',
    heatScore: 4201,
    tags: ['夜景', '导览', '服务'],
  },
  {
    id: 301,
    scenicAreaId: 2,
    name: '创新工坊',
    buildingType: '教学楼',
    floorNumber: '6 层',
    cover: socialCard,
    description: '面向设计与协作的开放空间，支持小组协同与活动组织。',
    openingHours: '08:00-22:00',
    heatScore: 2867,
    tags: ['文艺', '协同', '打卡'],
  },
]

export const facilities: Facility[] = [
  {
    id: 401,
    scenicAreaId: 1,
    name: '游客中心',
    facilityType: '游客中心',
    cover: scenicCard,
    description: '提供票务、咨询、路线建议和雨天借伞。',
    nodeId: 8801,
    tags: ['服务', '咨询'],
  },
  {
    id: 402,
    scenicAreaId: 1,
    name: '停车场',
    facilityType: '停车场',
    cover: routeCard,
    description: '靠近东入口，适合自驾及旅游巴士停靠。',
    nodeId: 8802,
    tags: ['停车', '换乘'],
  },
  {
    id: 403,
    scenicAreaId: 2,
    name: '医务室',
    facilityType: '医疗点',
    cover: hero,
    description: '校园医疗服务点，支持轻度应急处理。',
    nodeId: 8803,
    tags: ['医疗', '应急'],
  },
]

export const foods: FoodPlace[] = [
  {
    id: 501,
    scenicAreaId: 1,
    buildingId: 201,
    nodeId: null,
    sourceFacilityId: null,
    name: '云栖观景餐厅',
    category: '餐厅',
    cuisineType: '杭帮菜',
    priceLevel: '中高',
    avgPrice: 96,
    priceRange: '¥68-168',
    latitude: 30.2331,
    longitude: 120.0812,
    floorNumber: '2 层',
    description: '主打湖景位与本地菜，适合午晚餐和约会。',
    openingHours: '10:00-21:30',
    images: [foodCard, scenicCard],
    tags: ['湖景', '拍照', '本帮菜'],
    contactInfo: '0571-88889999',
    rating: 4.8,
    reviewCount: 1241,
    heatScore: 4188,
    recommendScore: 95,
    status: 'online',
    isDeleted: false,
  },
  {
    id: 502,
    scenicAreaId: 1,
    buildingId: null,
    nodeId: 8812,
    sourceFacilityId: 402,
    name: '夜游轻食站',
    category: '小吃',
    cuisineType: '小吃快餐',
    priceLevel: '亲民',
    avgPrice: 32,
    priceRange: '¥18-45',
    latitude: 30.2348,
    longitude: 120.0842,
    floorNumber: '1 层',
    description: '支持外带，夜游和赶路场景都适合。',
    openingHours: '09:00-23:00',
    images: [foodCard],
    tags: ['夜景', '快餐', '补给'],
    contactInfo: '0571-88887777',
    rating: 4.6,
    reviewCount: 786,
    heatScore: 3621,
    recommendScore: 89,
    status: 'online',
    isDeleted: false,
  },
  {
    id: 503,
    scenicAreaId: 2,
    buildingId: 301,
    nodeId: null,
    sourceFacilityId: null,
    name: '校园咖啡工坊',
    category: '饮品',
    cuisineType: '咖啡',
    priceLevel: '中等',
    avgPrice: 28,
    priceRange: '¥16-38',
    latitude: 31.9822,
    longitude: 118.8043,
    floorNumber: '1 层',
    description: '适合自习、会谈和社群会议。',
    openingHours: '08:30-21:30',
    images: [socialCard],
    tags: ['文艺', '自习', '咖啡'],
    contactInfo: '025-66667777',
    rating: 4.7,
    reviewCount: 612,
    heatScore: 2840,
    recommendScore: 92,
    status: 'online',
    isDeleted: false,
  },
]

export const campuses: Campus[] = [
  {
    id: 801,
    scenicAreaId: 2,
    name: '栖山理工校园',
    city: '南京',
    cover: socialCard,
    intro: '校园漫游、社群协同、建筑/餐饮联动的重点样例。',
    tags: ['校园', '协同', '文艺'],
  },
  {
    id: 802,
    scenicAreaId: 4,
    name: '南山文创学院',
    city: '成都',
    cover: hero,
    intro: '适合日记、组队、活动与灵感收集。',
    tags: ['创意', '打卡', '社群'],
  },
]

export const diaries: Diary[] = [
  {
    id: 901,
    author: '岚川',
    title: '夜游云栖湖的 8 个细节',
    summary: '路线、餐厅、拍照点和拥挤度的一次完整记录。',
    cover: scenicCard,
    content: '从东门进场后先吃夜游轻食站，再沿湖边慢走到云栖书院，最后在观景平台拍下整片灯光。',
    scenicName: '云栖湖文化度假区',
    createTime: '2026-05-06 21:30',
    likeCount: 128,
    commentCount: 24,
    tags: ['夜游', '拍照', '攻略'],
  },
  {
    id: 902,
    author: '清砚',
    title: '校园里的咖啡路线',
    summary: '适合周末散步、会友和组队讨论的一条轻路线。',
    cover: socialCard,
    content: '先去创新工坊打卡，再到咖啡工坊坐一小时，最后在林荫路边完成散步和拍照。',
    scenicName: '栖山理工校园',
    createTime: '2026-05-03 14:10',
    likeCount: 87,
    commentCount: 16,
    tags: ['校园', '文艺', '咖啡'],
  },
]

export const groups: GroupPlan[] = [
  {
    id: 10001,
    name: '周末云栖轻游',
    scenicName: '云栖湖文化度假区',
    dateRange: '05.11 - 05.12',
    members: 4,
    status: '筹备中',
    summary: '已完成路线和餐饮偏好收集，等待生成协同方案。',
  },
  {
    id: 10002,
    name: '校园拍照组',
    scenicName: '栖山理工校园',
    dateRange: '本周六',
    members: 6,
    status: '进行中',
    summary: '正在收集建筑打卡点与自动生成行走建议。',
  },
]

export const expenses: ExpenseRecord[] = [
  { id: 1101, title: '云栖观景餐厅', category: '餐饮', amount: 96, time: '05-06 18:42', scenicName: '云栖湖文化度假区', method: '微信支付' },
  { id: 1102, title: '夜游门票', category: '门票', amount: 68, time: '05-06 17:10', scenicName: '云栖湖文化度假区', method: '支付宝' },
  { id: 1103, title: '校园咖啡工坊', category: '饮品', amount: 28, time: '05-03 15:20', scenicName: '栖山理工校园', method: '银行卡' },
]

export const favorites: FavoriteItem[] = [
  { id: 1201, favoriteType: 0, entityId: 1, entityName: '云栖湖文化度假区', entityCover: scenicCard, scenicName: '杭州', createdAt: '05-06 09:20' },
  { id: 1202, favoriteType: 2, entityId: 201, entityName: '云栖书院', entityCover: scenicCard, scenicName: '云栖湖文化度假区', createdAt: '05-06 10:02' },
  { id: 1203, favoriteType: 3, entityId: 401, entityName: '游客中心', entityCover: routeCard, scenicName: '云栖湖文化度假区', createdAt: '05-06 10:15' },
  { id: 1204, favoriteType: 6, entityId: 501, entityName: '云栖观景餐厅', entityCover: foodCard, scenicName: '云栖湖文化度假区', createdAt: '05-06 10:34' },
]

export const historyItems: HistoryItem[] = [
  { id: 1301, type: 'view', title: '云栖湖文化度假区', description: '浏览景点详情', createdAt: '05-06 21:30' },
  { id: 1302, type: 'search', title: '夜游', description: '搜索关键词', createdAt: '05-06 19:10' },
  { id: 1303, type: 'navigation', title: '云栖湖夜游路线', description: '查看导航轨迹', createdAt: '05-06 18:50' },
]

export const files: FileAsset[] = [
  { id: 1401, key: 'image-001', name: 'hero-cover.jpg', url: scenicCard, type: 'image', size: '1.2 MB', createdAt: '05-06 09:10' },
  { id: 1402, key: 'video-001', name: 'night-tour.mp4', url: routeCard, type: 'video', size: '18.4 MB', createdAt: '05-06 09:15' },
]

export const searchResults: SearchResultItem[] = [
  { id: 1, type: 'scenic', name: '云栖湖文化度假区', subtitle: '杭州 · 景区', latitude: 30.233, longitude: 120.081, scenicType: 'scenic', heatScore: 9821 },
  { id: 2, type: 'scenic', name: '栖山理工校园', subtitle: '南京 · 校园', latitude: 31.982, longitude: 118.804, scenicType: 'campus', heatScore: 7350 },
  { id: 8801, type: 'node', name: '东门入口', subtitle: '交通节点 · walk/bike', latitude: 30.234, longitude: 120.082, nodeType: 'road_node' },
]

export const congestionSnapshots: CongestionSnapshot[] = [
  { scenicAreaId: 1, scenicName: '云栖湖文化度假区', crowdLevel: 'medium', crowdRatio: 0.56, updatedAt: '2026-05-08 10:10' },
  { scenicAreaId: 2, scenicName: '栖山理工校园', crowdLevel: 'low', crowdRatio: 0.22, updatedAt: '2026-05-08 10:10' },
]

export const nearbyFacilities: NearbyFacility[] = [
  { id: 1, name: '游客中心', facilityType: '游客中心', distance: 120, scenicName: '云栖湖文化度假区', crowdLevel: 'medium' },
  { id: 2, name: '停车场', facilityType: '停车场', distance: 280, scenicName: '云栖湖文化度假区', crowdLevel: 'low' },
]

export const photoSpots: PhotoSpot[] = [
  { id: 1, scenicAreaId: 1, name: '湖畔观景台', latitude: 30.2336, longitude: 120.0834, note: '适合黄昏与夜景拍摄' },
  { id: 2, scenicAreaId: 2, name: '图书馆台阶', latitude: 31.9828, longitude: 118.8051, note: '适合校园人像与广角构图' },
]
