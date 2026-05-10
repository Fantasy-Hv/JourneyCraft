<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string
    description?: string
    kind?: 'empty' | 'loading' | 'error' | 'unauthorized'
  }>(),
  { kind: 'empty' },
)
</script>

<template>
  <div class="state-panel" :data-kind="kind">
    <div class="state-panel__title">{{ title }}</div>
    <div v-if="description" class="state-panel__description">{{ description }}</div>
    <slot />
  </div>
</template>

