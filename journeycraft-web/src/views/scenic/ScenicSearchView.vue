<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Filter } from '@element-plus/icons-vue'
import PageHero from '@/components/PageHero.vue'
import SectionHeader from '@/components/SectionHeader.vue'
import EntityCard from '@/components/EntityCard.vue'
import StatePanel from '@/components/StatePanel.vue'
import { searchScenic } from '@/api/scenic'
import { scenicAreas } from '@/mock/data'

const router = useRouter()
const route = useRoute()
const keyword = ref(String(route.query.keyword || ''))
const loading = ref(false)
const results = ref(scenicAreas)

const grouped = computed(() => {
  const source = Array.isArray(results.value) ? results.value : []
  return source.reduce<{ scenic: typeof scenicAreas; campus: typeof scenicAreas }>((acc, item) => {
    acc[item.scenicType].push(item)
    return acc
  }, { scenic: [], campus: [] })
})

async function search() {
  loading.value = true
  try {
    const next = await searchScenic(keyword.value)
    results.value = Array.isArray(next) ? next : []
  } catch {
    results.value = scenicAreas.filter((item) => item.name.includes(keyword.value) || item.tags.some((tag) => tag.includes(keyword.value)))
  } finally {
    loading.value = false
  }
}

onMounted(search)
</script>

<template>
  <div class="stack">
    <PageHero
      title="搜索结果"
      subtitle="搜索同时保留景点与校园的分组展示。"
      image="/media/route-card.svg"
      :badges="['Search API', 'Scenic Results', 'Campus Results']"
    >
      <template #actions>
        <div class="hero-search">
          <el-input v-model="keyword" placeholder="输入关键词" clearable @keyup.enter="search">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button type="primary" @click="search"><el-icon><Filter /></el-icon>搜索</el-button>
        </div>
      </template>
    </PageHero>

    <section class="panel">
      <SectionHeader title="结果分组" subtitle="可用于前端联调的结果页结构。" />
      <StatePanel v-if="loading" title="搜索中" kind="loading" />
      <div v-else class="stack stack--tight">
        <div class="search-group">
          <h3>景区</h3>
          <div class="card-grid card-grid--three">
            <EntityCard
              v-for="item in grouped.scenic"
              :key="item.id"
              :title="item.name"
              :subtitle="item.city"
              :image="item.cover"
              :tags="item.tags"
              :note="item.intro"
              @click="router.push(`/scenic/${item.id}`)"
            />
          </div>
        </div>
        <div class="search-group">
          <h3>校园</h3>
          <div class="card-grid card-grid--three">
            <EntityCard
              v-for="item in grouped.campus"
              :key="item.id"
              :title="item.name"
              :subtitle="item.city"
              :image="item.cover"
              :tags="item.tags"
              :note="item.intro"
              @click="router.push(`/scenic/${item.id}`)"
            />
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
