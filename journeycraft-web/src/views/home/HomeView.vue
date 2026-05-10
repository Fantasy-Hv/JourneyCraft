<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, MapLocation, Star, Notebook, UserFilled, Van, Files, Tickets, Clock } from '@element-plus/icons-vue'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import EntityCard from '@/components/EntityCard.vue'
import StatePanel from '@/components/StatePanel.vue'
import { scenicAreas, foods, diaries, groups, historyItems } from '@/mock/data'
import { listScenic } from '@/api/scenic'
import { recommendFoods, recommendScenic } from '@/api/recommend'
import { listGroups } from '@/api/group'
import { listDiaries } from '@/api/diary'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const appStore = useAppStore()
const loading = ref(true)
const scenicPicks = ref(scenicAreas.slice(0, 3))
const foodPicks = ref(foods.slice(0, 3))
const diaryPicks = ref(diaries.slice(0, 2))
const groupPicks = ref(groups.slice(0, 2))

const shortcuts = [
  { label: '景点', icon: MapLocation, path: '/scenic/list', desc: '目的地探索' },
  { label: '路线', icon: Van, path: '/navigation', desc: '多目标规划' },
  { label: '推荐', icon: Star, path: '/recommend', desc: '个性化推荐' },
  { label: '日记', icon: Notebook, path: '/diary', desc: '游记灵感' },
  { label: '小组', icon: UserFilled, path: '/group', desc: '协同出行' },
  { label: '文件', icon: Files, path: '/file', desc: '上传预览' },
  { label: '账单', icon: Tickets, path: '/expense', desc: '消费统计' },
  { label: '历史', icon: Clock, path: '/history', desc: '浏览记录' },
]

const quickHistory = computed(() => historyItems.slice(0, 3))

async function load() {
  loading.value = true
  try {
    const [scenicData, scenicRec, foodRec, diaryData, groupData] = await Promise.all([
      listScenic().catch(() => scenicAreas),
      recommendScenic().catch(() => scenicAreas),
      recommendFoods().catch(() => foods),
      listDiaries().catch(() => diaries),
      listGroups().catch(() => groups),
    ])
    scenicPicks.value = (scenicRec.length ? scenicRec : scenicData).slice(0, 3)
    foodPicks.value = foodRec.slice(0, 3)
    diaryPicks.value = diaryData.slice(0, 2)
    groupPicks.value = groupData.slice(0, 2)
  } finally {
    loading.value = false
  }
}

function go(path: string) {
  router.push(path)
}

function search() {
  const keyword = appStore.globalKeyword.trim()
  router.push({ path: '/scenic/search', query: keyword ? { keyword } : {} })
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      title="下一段旅行，从这里开始"
      subtitle="把景点、导航、美食、日记、协同和收藏汇集到一套精致的旅行产品体验里。"
      image="/media/hero-travel.svg"
      :badges="['真实联调 / Mock 切换', '景点 · 路线 · 美食', '桌面端 / 移动端']"
    >
      <template #actions>
        <div class="hero-search">
          <el-input v-model="appStore.globalKeyword" placeholder="搜索景点、美食、路线、游记" clearable @keyup.enter="search" />
          <el-button type="primary" @click="search"><el-icon><Search /></el-icon>立即搜索</el-button>
        </div>
      </template>
    </PageHero>

    <div class="shortcut-grid">
      <button v-for="item in shortcuts" :key="item.label" class="shortcut-card" type="button" @click="go(item.path)">
        <el-icon><component :is="item.icon" /></el-icon>
        <strong>{{ item.label }}</strong>
        <span>{{ item.desc }}</span>
      </button>
    </div>

    <div class="page-grid page-grid--two">
      <section class="panel panel--wide">
        <SectionHeader title="热门景点" subtitle="推荐、热度和标签都来自当前项目的数据模型或 mock 层。" />
        <div v-if="loading" class="card-grid">
          <StatePanel title="正在载入目的地" description="获取景点列表、推荐列表与协同数据。" kind="loading" />
        </div>
        <div v-else class="card-grid">
          <EntityCard
            v-for="item in scenicPicks"
            :key="item.id"
            :title="item.name"
            :subtitle="`${item.city} · ${item.address}`"
            :image="item.cover"
            :tags="item.tags"
            :note="item.intro"
            :score="`${item.rating.toFixed(1)} · ${item.heatScore}`"
            link-text="查看详情"
            @click="go(`/scenic/${item.id}`)"
          />
        </div>
      </section>

      <section class="panel">
        <SectionHeader title="我的旅程面板" subtitle="快捷入口和最近动作，方便前端联调时快速定位场景。" />
        <div class="quick-stack">
          <div class="metric-card">
            <strong>模块入口</strong>
            <span>首页 / 景点 / 导航 / 推荐 / 我的</span>
          </div>
          <div class="metric-card">
            <strong>最近浏览</strong>
            <span v-for="item in quickHistory" :key="item.id" class="metric-card__line">{{ item.title }}</span>
          </div>
          <div class="metric-card">
            <strong>当前模式</strong>
            <span>{{ appStore.apiMode === 'mock' ? 'Mock 数据优先' : '真实接口联调' }}</span>
          </div>
        </div>
      </section>
    </div>

    <section class="panel">
      <SectionHeader title="精选美食" subtitle="FoodPlace 已单独收口，可直接按 tags / cuisineType / priceLevel / heatScore 做推荐。" />
      <div class="card-grid card-grid--three">
        <EntityCard
          v-for="item in foodPicks"
          :key="item.id"
          :title="item.name"
          :subtitle="`${item.category} · ${item.cuisineType} · ${item.priceRange}`"
          :image="item.images[0]"
          :tags="item.tags"
          :note="item.description"
          :score="`${item.rating.toFixed(1)} · ${item.recommendScore}`"
          link-text="美食详情"
          @click="go(`/scenic/foods/${item.id}`)"
        />
      </div>
    </section>

    <div class="page-grid page-grid--two">
      <section class="panel">
        <SectionHeader title="游记与灵感" subtitle="日记、推荐和内容入口在这里形成闭环。" />
        <div class="stack stack--tight">
          <EntityCard
            v-for="item in diaryPicks"
            :key="item.id"
            :title="item.title"
            :subtitle="`${item.author} · ${item.scenicName}`"
            :image="item.cover"
            :tags="item.tags"
            :note="item.summary"
            link-text="阅读日记"
            @click="go(`/diary/${item.id}`)"
          />
        </div>
      </section>

      <section class="panel">
        <SectionHeader title="协同出行" subtitle="小组、偏好、路线和账单后续均可串联到同一条旅程里。" />
        <div class="stack stack--tight">
          <div v-for="item in groupPicks" :key="item.id" class="insight-card">
            <div class="insight-card__head">
              <strong>{{ item.name }}</strong>
              <span class="pill pill--soft">{{ item.status }}</span>
            </div>
            <p>{{ item.summary }}</p>
            <small>{{ item.scenicName }} · {{ item.dateRange }} · {{ item.members }} 人</small>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>
