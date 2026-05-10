<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import EntityCard from '@/components/EntityCard.vue'
import StatePanel from '@/components/StatePanel.vue'
import { getScenic, listBuildings, listFacilities, listFoods, reportCrowd } from '@/api/scenic'
import { scenicAreas, buildings, facilities, foods } from '@/mock/data'
import { formatNumber } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const scenicId = computed(() => Number(route.params.id))
const scenic = ref(scenicAreas[0])
const scenicBuildings = ref(buildings.filter((item) => item.scenicAreaId === scenicId.value))
const scenicFacilities = ref(facilities.filter((item) => item.scenicAreaId === scenicId.value))
const scenicFoods = ref(foods.filter((item) => item.scenicAreaId === scenicId.value))
const loading = ref(true)
const reporting = ref(false)

async function load() {
  loading.value = true
  try {
    scenic.value = (await getScenic(scenicId.value).catch(() => scenicAreas.find((item) => item.id === scenicId.value))) || scenicAreas[0]
    scenicBuildings.value = await listBuildings(scenicId.value).catch(() => buildings.filter((item) => item.scenicAreaId === scenicId.value))
    scenicFacilities.value = await listFacilities(scenicId.value).catch(() => facilities.filter((item) => item.scenicAreaId === scenicId.value))
    scenicFoods.value = await listFoods(scenicId.value).catch(() => foods.filter((item) => item.scenicAreaId === scenicId.value))
  } finally {
    loading.value = false
  }
}

async function sendCrowd() {
  reporting.value = true
  try {
    await reportCrowd(scenicId.value, { scenicAreaId: scenicId.value, level: scenic.value.crowdLevel })
  } finally {
    reporting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      :title="scenic?.name || '景点详情'"
      :subtitle="`${scenic?.city || ''} · ${scenic?.address || ''}`"
      :image="scenic?.cover || '/media/scenic-card.svg'"
      :badges="scenic?.tags || []"
    >
      <template #actions>
        <el-button type="primary" @click="router.push(`/scenic/${scenicId}/buildings`)">建筑列表</el-button>
        <el-button plain @click="router.push(`/scenic/${scenicId}/facilities`)">设施列表</el-button>
        <el-button plain @click="router.push(`/scenic/${scenicId}/foods`)">美食列表</el-button>
        <el-button :loading="reporting" @click="sendCrowd">上报拥挤度</el-button>
      </template>
    </PageHero>

    <div class="page-grid page-grid--two">
      <section class="panel">
        <SectionHeader title="基础信息" subtitle="景点表 tags 直接用于推荐匹配。" />
        <StatePanel v-if="loading" title="加载中" kind="loading" />
        <div v-else class="info-grid">
          <div class="info-grid__item"><span>评分</span><strong>{{ Number(scenic?.rating || 0).toFixed(1) }}</strong></div>
          <div class="info-grid__item"><span>热度</span><strong>{{ formatNumber(scenic?.heatScore || 0) }}</strong></div>
          <div class="info-grid__item"><span>票价</span><strong>{{ scenic?.ticketPrice }}</strong></div>
          <div class="info-grid__item"><span>开放</span><strong>{{ scenic?.openTime }}</strong></div>
        </div>
      </section>
      <section class="panel">
        <SectionHeader title="拥挤与消费" subtitle="用于联动导航和推荐。" />
        <div class="insight-card">
          <div class="insight-card__head">
            <strong>{{ scenic?.crowdLevel }}</strong>
            <span class="pill pill--soft">{{ scenic?.heatScore }}</span>
          </div>
          <p>{{ scenic?.intro }}</p>
          <small>票价参考 {{ scenic?.ticketPrice }} · 建议先浏览建筑和美食。</small>
        </div>
      </section>
    </div>

    <section class="panel">
      <SectionHeader title="建筑 / 设施 / 美食" subtitle="三类实体的列表和详情都已在前端结构中预留。" />
      <div class="card-grid card-grid--three">
        <EntityCard
          v-for="item in scenicBuildings"
          :key="item.id"
          :title="item.name"
          :subtitle="`${item.buildingType} · ${item.floorNumber}`"
          :image="item.cover"
          :tags="item.tags"
          :note="item.description"
          :score="`热度 ${item.heatScore}`"
          link-text="建筑详情"
          @click="router.push(`/scenic/buildings/${item.id}`)"
        />
      </div>
    </section>

    <section class="panel">
      <SectionHeader title="通用设施" subtitle="Facilities 仅表示通用设施，不再承载美食主业务。" />
      <div class="card-grid card-grid--three">
        <EntityCard
          v-for="item in scenicFacilities"
          :key="item.id"
          :title="item.name"
          :subtitle="item.facilityType"
          :image="item.cover"
          :tags="item.tags"
          :note="item.description"
          link-text="设施详情"
          @click="router.push(`/scenic/facilities/${item.id}`)"
        />
      </div>
    </section>

    <section class="panel">
      <SectionHeader title="推荐美食" subtitle="FoodPlace 是独立主表，可直接通过 tags / cuisineType / priceLevel 做推荐。" />
      <div class="card-grid card-grid--three">
        <EntityCard
          v-for="item in scenicFoods"
          :key="item.id"
          :title="item.name"
          :subtitle="`${item.category} · ${item.cuisineType} · ${item.priceRange}`"
          :image="item.images[0]"
          :tags="item.tags"
          :note="item.description"
          :score="`${item.rating.toFixed(1)} · ${item.recommendScore}`"
          link-text="美食详情"
          @click="router.push(`/scenic/foods/${item.id}`)"
        />
      </div>
    </section>
  </div>
</template>
