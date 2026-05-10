import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import HomeView from '@/views/home/HomeView.vue'
import LoginView from '@/views/auth/LoginView.vue'
import RegisterView from '@/views/auth/RegisterView.vue'
import UserCenterView from '@/views/user/UserCenterView.vue'
import UserProfileEditView from '@/views/user/UserProfileEditView.vue'
import UserPreferencesView from '@/views/user/UserPreferencesView.vue'
import UserPasswordView from '@/views/user/UserPasswordView.vue'
import ScenicListView from '@/views/scenic/ScenicListView.vue'
import ScenicSearchView from '@/views/scenic/ScenicSearchView.vue'
import ScenicDetailView from '@/views/scenic/ScenicDetailView.vue'
import ScenicEntityListView from '@/views/scenic/ScenicEntityListView.vue'
import ScenicEntityDetailView from '@/views/scenic/ScenicEntityDetailView.vue'
import CampusListView from '@/views/scenic/CampusListView.vue'
import ModuleBoardView from '@/views/module/ModuleBoardView.vue'
import NotFoundView from '@/views/common/NotFoundView.vue'

const moduleMeta = (title: string, subtitle: string, features: string[], stateText: string, image = '/media/social-card.svg') => ({
  title,
  subtitle,
  features,
  stateText,
  image,
})

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior() {
    return { top: 0 }
  },
  routes: [
    {
      path: '/auth',
      component: AuthLayout,
      children: [
        { path: '', redirect: '/auth/login' },
        { path: 'login', component: LoginView },
        { path: 'register', component: RegisterView },
      ],
    },
    {
      path: '/',
      component: AppLayout,
      children: [
        { path: '', component: HomeView },
        { path: 'scenic/list', component: ScenicListView },
        { path: 'scenic/search', component: ScenicSearchView },
        { path: 'scenic/campus', component: CampusListView },
        { path: 'scenic/:id', component: ScenicDetailView },
        { path: 'scenic/:id/buildings', component: ScenicEntityListView, meta: { kind: 'building' } },
        { path: 'scenic/:id/facilities', component: ScenicEntityListView, meta: { kind: 'facility' } },
        { path: 'scenic/:id/foods', component: ScenicEntityListView, meta: { kind: 'food' } },
        { path: 'scenic/buildings/:id', component: ScenicEntityDetailView, meta: { kind: 'building' } },
        { path: 'scenic/facilities/:id', component: ScenicEntityDetailView, meta: { kind: 'facility' } },
        { path: 'scenic/foods/:id', component: ScenicEntityDetailView, meta: { kind: 'food' } },
        { path: 'user', component: UserCenterView },
        { path: 'user/profile', component: UserProfileEditView },
        { path: 'user/preferences', component: UserPreferencesView },
        { path: 'user/password', component: UserPasswordView },

        { path: 'navigation', component: ModuleBoardView, meta: moduleMeta('路线规划主页面', '路线规划、附近设施、拥挤度和多目标导航入口都在这里统一串联。', ['单目标路线', '多目标路线', '附近设施', '拥挤度', '室内导航'], '导航模块待联调', '/media/route-card.svg') },
        { path: 'navigation/route', component: ModuleBoardView, meta: moduleMeta('单目标路线规划', '支持 scenicArea / node / edge / coordinate 四种起终点输入。', ['路线表单', '路径分段', '坐标转节点'], '已预留', '/media/route-card.svg') },
        { path: 'navigation/multi-route', component: ModuleBoardView, meta: moduleMeta('多目标路线规划', '适合景点串联、校园巡游和美食联动。', ['多目标输入', '串联顺序', '路线预览'], '已预留', '/media/route-card.svg') },
        { path: 'navigation/nearby', component: ModuleBoardView, meta: moduleMeta('附近设施', '按路网距离查看厕所、停车场、游客中心和补给点。', ['设施卡片', '距离排序', '地图占位'], '待开放', '/media/scenic-card.svg') },
        { path: 'navigation/congestion', component: ModuleBoardView, meta: moduleMeta('拥挤度查看', '展示实时拥挤态势与替代游览建议。', ['拥挤热力', '时段提示', '替代方案'], '已实现占位', '/media/social-card.svg') },
        { path: 'navigation/indoor', component: ModuleBoardView, meta: moduleMeta('室内导航', '楼层级导航与室内路径当前仅保留页面结构。', ['楼层切换', '室内路径', '楼层地图'], '待接入', '/media/route-card.svg') },
        { path: 'navigation/photo-spots', component: ModuleBoardView, meta: moduleMeta('拍照点推荐', '根据 POI / 风景标签推荐拍照位置。', ['POI 点位', '打卡建议', '标签匹配'], '待实现', '/media/scenic-card.svg') },
        { path: 'navigation/alternative', component: ModuleBoardView, meta: moduleMeta('替代路线', '当某条路线拥挤时给出逆向游览建议。', ['反向游览', '避开高峰', '多方案对比'], '待实现', '/media/route-card.svg') },
        { path: 'navigation/search', component: ModuleBoardView, meta: moduleMeta('搜索', '搜索景区与路网节点的统一入口。', ['搜索景点', '搜索节点', '结果分组'], '待实现', '/media/scenic-card.svg') },
        { path: 'navigation/nearest-edge', component: ModuleBoardView, meta: moduleMeta('nearest-edge', '地图点击后吸附到最近道路边。', ['边吸附', '置信度', '备选点'], '待实现', '/media/route-card.svg') },
        { path: 'navigation/access-nodes', component: ModuleBoardView, meta: moduleMeta('access-nodes', '景区接入点按 walk / bike / shuttle 分组。', ['分组结果', '交通方式', '入口优先级'], '待实现', '/media/scenic-card.svg') },
        { path: 'navigation/poi-nodes', component: ModuleBoardView, meta: moduleMeta('poi-nodes', '景区内 POI 节点占位页。', ['restaurant', 'toilet', 'parking'], '待实现', '/media/social-card.svg') },

        { path: 'recommend', component: ModuleBoardView, meta: moduleMeta('推荐中心', '热门景点、个性化、美食和日记推荐统一入口。', ['热门景点', '个性化推荐', '美食推荐', '日记推荐'], '部分占位 / 部分 mock', '/media/social-card.svg') },
        { path: 'group', component: ModuleBoardView, meta: moduleMeta('协同小组', '组队出行、偏好提交和协同方案的入口。', ['小组列表', '创建小组', '提交偏好', '协同方案'], '待开放', '/media/social-card.svg') },
        { path: 'diary', component: ModuleBoardView, meta: moduleMeta('旅行日记', '日记列表、编辑和自动生成都在这里串联。', ['日记列表', '详情', '编辑', '自动生成'], '部分实现 / 占位', '/media/scenic-card.svg') },
        { path: 'expense', component: ModuleBoardView, meta: moduleMeta('我的账单', '消费记录与统计面板。', ['消费列表', '支出统计', '自动生成账单'], '待开放', '/media/route-card.svg') },
        { path: 'favorite', component: ModuleBoardView, meta: moduleMeta('收藏中心', '收藏列表、收藏夹和状态检查。', ['收藏列表', '收藏夹管理', '收藏状态检查'], '部分实现 / 占位', '/media/scenic-card.svg') },
        { path: 'history', component: ModuleBoardView, meta: moduleMeta('浏览历史', '浏览、搜索和导航历史统一展示。', ['浏览历史', '搜索历史', '导航历史'], '已实现占位', '/media/social-card.svg') },
        { path: 'file', component: ModuleBoardView, meta: moduleMeta('文件中心', '图片上传、视频上传和文件预览。', ['上传图片', '上传视频', '文件预览'], '已接入 / 待打磨', '/media/route-card.svg') },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      component: NotFoundView,
    },
  ],
})

export default router
