<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import EntityCard from '@/components/EntityCard.vue'
import StatePanel from '@/components/StatePanel.vue'
import { listCampuses } from '@/api/scenic'
import { campuses } from '@/mock/data'

const router = useRouter()
const loading = ref(true)
const list = ref(campuses)

async function load() {
  loading.value = true
  try {
    const next = await listCampuses().catch(() => campuses)
    list.value = Array.isArray(next) ? next : campuses
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      title="校园列表"
      subtitle="校园被作为 Scenic 的一个一级场景，支持景点、建筑、美食和导航联动。"
      image="/media/social-card.svg"
      :badges="['Campus', 'Scenic', 'FoodPlace']"
    />

    <section class="panel">
      <SectionHeader title="校园目的地" subtitle="展示校内旅行与协同场景。" />
      <StatePanel v-if="loading" title="加载中" kind="loading" />
      <div v-else class="card-grid card-grid--three">
        <EntityCard
          v-for="item in list"
          :key="item.id"
          :title="item.name"
          :subtitle="item.city"
          :image="item.cover"
          :tags="item.tags"
          :note="item.intro"
          link-text="进入校园"
          @click="router.push(`/scenic/${item.scenicAreaId}`)"
        />
      </div>
    </section>
  </div>
</template>
