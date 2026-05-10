<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Sort } from '@element-plus/icons-vue'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import EntityCard from '@/components/EntityCard.vue'
import StatePanel from '@/components/StatePanel.vue'
import { listScenic, searchScenic } from '@/api/scenic'
import { scenicAreas } from '@/mock/data'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const keyword = ref(String(route.query.keyword || ''))
const sortBy = ref<'heat' | 'rating'>('heat')
const items = ref(scenicAreas)

const filtered = computed(() => {
  const source = Array.isArray(items.value) ? [...items.value] : []
  return source.sort((a, b) => sortBy.value === 'heat' ? b.heatScore - a.heatScore : b.rating - a.rating)
})

async function load() {
  loading.value = true
  try {
    const next = keyword.value ? await searchScenic(keyword.value) : await listScenic()
    items.value = Array.isArray(next) ? next : []
  } catch {
    items.value = scenicAreas
  } finally {
    loading.value = false
  }
}

function search() {
  router.replace({ query: keyword.value ? { keyword: keyword.value } : {} })
  load()
}

onMounted(load)
</script>

<template>
  <div class="stack">
    <PageHero
      title="景点列表"
      subtitle="以图片、热度、评分和标签构建的旅行产品列表页，不是后台管理表格。"
      image="/media/scenic-card.svg"
      :badges="['Scenic', 'Campus', 'Search']"
    >
      <template #actions>
        <div class="hero-search">
          <el-input v-model="keyword" placeholder="搜索景点 / 校园 / 主题" clearable @keyup.enter="search">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select v-model="sortBy" style="width: 150px">
            <el-option label="按热度" value="heat" />
            <el-option label="按评分" value="rating" />
          </el-select>
          <el-button type="primary" @click="search"><el-icon><Sort /></el-icon>筛选</el-button>
        </div>
      </template>
    </PageHero>

    <section class="panel">
      <SectionHeader title="推荐目的地" subtitle="根据热度、标签和评分排列。" />
      <StatePanel v-if="loading" title="加载景点列表" kind="loading" />
      <div v-else class="card-grid card-grid--three">
        <EntityCard
          v-for="item in filtered"
          :key="item.id"
          :title="item.name"
          :subtitle="`${item.city} · ${item.address}`"
          :image="item.cover"
          :tags="item.tags"
          :note="item.intro"
          :score="`${item.rating.toFixed(1)} · ${item.heatScore}`"
          link-text="查看详情"
          @click="router.push(`/scenic/${item.id}`)"
        />
      </div>
    </section>
  </div>
</template>
