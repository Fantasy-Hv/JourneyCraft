<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import PageHero from '@/components/PageHero.vue'
import StatePanel from '@/components/StatePanel.vue'

const route = useRoute()

const meta = computed(() => route.meta as Record<string, unknown>)
const title = computed(() => String(meta.value.title || '模块建设中'))
const subtitle = computed(() => String(meta.value.subtitle || '这里是预留页面结构，后续接口和业务联调会在这里接入。'))
const features = computed(() => (Array.isArray(meta.value.features) ? (meta.value.features as string[]) : []))
const image = computed(() => String(meta.value.image || '/media/social-card.svg'))
const stateText = computed(() => String(meta.value.stateText || '待开放'))
</script>

<template>
  <div class="stack">
    <PageHero :title="title" :subtitle="subtitle" :image="image" :badges="[stateText]">
      <template #actions>
        <el-button type="primary">接口预留</el-button>
        <el-button plain>查看文档</el-button>
      </template>
    </PageHero>

    <div class="page-grid page-grid--two">
      <StatePanel :title="title" :description="subtitle" kind="empty">
        <div class="placeholder-grid">
          <div v-for="feature in features" :key="feature" class="placeholder-card">
            <strong>{{ feature }}</strong>
            <span>待后端能力接入</span>
          </div>
        </div>
      </StatePanel>

      <StatePanel title="当前状态" description="未实现接口以 mock / 占位方式呈现，页面入口和导航已连通。" kind="loading">
        <div class="placeholder-note">
          <p>当前模块保持完整路由和页面结构，不会出现缺页。</p>
          <p>后续接入真实接口时，只需替换数据源，不改页面骨架。</p>
        </div>
      </StatePanel>
    </div>
  </div>
</template>
