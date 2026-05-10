<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import EntityCard from '@/components/EntityCard.vue'
import StatePanel from '@/components/StatePanel.vue'
import { buildings, facilities, foods } from '@/mock/data'
import { listBuildings, listFacilities, listFoods } from '@/api/scenic'

const route = useRoute()
const router = useRouter()
const scenicId = computed(() => Number(route.params.id))
const kind = computed(() => String(route.meta.kind || 'building'))
const loading = ref(true)
const list = ref<any[]>([])

const titleMap: Record<string, string> = {
  building: '建筑列表',
  facility: '设施列表',
  food: '美食列表',
}

const subtitleMap: Record<string, string> = {
  building: '建筑详情标签用于推荐匹配，不表示功能分类。',
  facility: '通用设施，不再承载美食主业务。',
  food: 'FoodPlace 独立实体已拆出，可按 tags / cuisineType / priceLevel 检索。',
}

async function load() {
  loading.value = true
  try {
    if (kind.value === 'building') list.value = await listBuildings(scenicId.value).catch(() => buildings.filter((item) => item.scenicAreaId === scenicId.value))
    if (kind.value === 'facility') list.value = await listFacilities(scenicId.value).catch(() => facilities.filter((item) => item.scenicAreaId === scenicId.value))
    if (kind.value === 'food') list.value = await listFoods(scenicId.value).catch(() => foods.filter((item) => item.scenicAreaId === scenicId.value))
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      :title="titleMap[kind]"
      :subtitle="subtitleMap[kind]"
      :image="kind === 'food' ? '/media/food-card.svg' : '/media/scenic-card.svg'"
      :badges="[kind.toUpperCase(), '列表', '详情联动']"
    />

    <section class="panel">
      <SectionHeader :title="titleMap[kind]" subtitle="前端页面和后端接口路径已预留。" />
      <StatePanel v-if="loading" title="加载中" kind="loading" />
      <div v-else class="card-grid card-grid--three">
        <EntityCard
          v-for="item in list"
          :key="item.id"
          :title="item.name"
          :subtitle="kind === 'food' ? `${item.category} · ${item.priceRange}` : kind === 'building' ? `${item.buildingType} · ${item.floorNumber}` : item.facilityType"
          :image="kind === 'food' ? item.images?.[0] || '/media/food-card.svg' : item.cover"
          :tags="item.tags"
          :note="item.description"
          :score="kind === 'food' ? `${item.rating?.toFixed(1)} · ${item.recommendScore}` : `热度 ${item.heatScore || '-'}`"
          :link-text="kind === 'food' ? '美食详情' : kind === 'building' ? '建筑详情' : '设施详情'"
          @click="router.push(kind === 'food' ? `/scenic/foods/${item.id}` : kind === 'building' ? `/scenic/buildings/${item.id}` : `/scenic/facilities/${item.id}`)"
        />
      </div>
    </section>
  </div>
</template>
