<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import StatePanel from '@/components/StatePanel.vue'
import { getBuilding, getFacility, getFood } from '@/api/scenic'
import { buildings, facilities, foods } from '@/mock/data'
import { formatCurrency } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const kind = computed(() => String(route.meta.kind || 'building'))
const id = computed(() => Number(route.params.id))
const loading = ref(true)
const detail = ref<any>(null)

const titleMap: Record<string, string> = {
  building: '建筑详情',
  facility: '设施详情',
  food: '美食详情',
}

const subtitleMap: Record<string, string> = {
  building: 'tags 是风格/内容元素标签，不是建筑类别。',
  facility: '设施仅表示通用设施，不再承载美食主业务。',
  food: 'FoodPlace 作为独立实体直接服务推荐和详情页。',
}

async function load() {
  loading.value = true
  try {
    if (kind.value === 'building') detail.value = await getBuilding(id.value).catch(() => buildings.find((item) => item.id === id.value))
    if (kind.value === 'facility') detail.value = await getFacility(id.value).catch(() => facilities.find((item) => item.id === id.value))
    if (kind.value === 'food') detail.value = await getFood(id.value).catch(() => foods.find((item) => item.id === id.value))
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      :title="detail?.name || titleMap[kind]"
      :subtitle="subtitleMap[kind]"
      :image="kind === 'food' ? (detail?.images?.[0] || '/media/food-card.svg') : detail?.cover || '/media/scenic-card.svg'"
      :badges="detail?.tags || []"
    >
      <template #actions>
        <el-button type="primary" @click="router.back()">返回</el-button>
      </template>
    </PageHero>

    <section class="panel">
      <SectionHeader :title="titleMap[kind]" subtitle="前端已经预留四类详情页。" />
      <StatePanel v-if="loading" title="加载中" kind="loading" />
      <div v-else class="detail-grid">
        <div class="detail-grid__main">
          <p>{{ detail?.description }}</p>
          <div class="tag-cloud">
            <span v-for="tag in detail?.tags || []" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
        <div class="detail-grid__aside">
          <div class="detail-meta">
            <span>ID</span>
            <strong>{{ id }}</strong>
          </div>
          <div v-if="kind === 'food'" class="detail-meta">
            <span>价格</span>
            <strong>{{ formatCurrency(detail?.avgPrice || 0) }}</strong>
          </div>
          <div class="detail-meta">
            <span>开放时间</span>
            <strong>{{ detail?.openingHours || '全天' }}</strong>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
