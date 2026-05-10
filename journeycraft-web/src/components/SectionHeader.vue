<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
  actionText?: string
}>()

defineEmits<{
  (event: 'action'): void
}>()
</script>

<template>
  <div class="section-header">
    <div>
      <div class="section-header__title">{{ title }}</div>
      <div v-if="subtitle" class="section-header__subtitle">{{ subtitle }}</div>
    </div>
    <el-button v-if="actionText" text class="section-header__action" @click="$emit('action')">
      {{ actionText }}
    </el-button>
  </div>
</template>

