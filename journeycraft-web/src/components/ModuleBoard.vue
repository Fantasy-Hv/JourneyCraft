<script setup lang="ts">
import { CircleCheckFilled } from '@element-plus/icons-vue'

defineProps<{
  title: string
  subtitle: string
  accent?: string
  features?: string[]
  stateText?: string
}>()
</script>

<template>
  <section class="module-board">
    <div class="module-board__hero">
      <div>
        <div class="module-board__eyebrow">模块占位</div>
        <h2>{{ title }}</h2>
        <p>{{ subtitle }}</p>
      </div>
      <span v-if="stateText" class="pill pill--soft">{{ stateText }}</span>
    </div>
    <div v-if="$slots.default" class="module-board__slot">
      <slot />
    </div>
    <div v-if="features?.length" class="module-board__features">
      <div v-for="feature in features" :key="feature" class="module-board__feature">
        <el-icon><CircleCheckFilled /></el-icon>
        <span>{{ feature }}</span>
      </div>
    </div>
  </section>
</template>
