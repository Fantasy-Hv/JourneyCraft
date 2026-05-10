<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string
    subtitle: string
    image?: string
    badges?: string[]
  }>(),
  {
    badges: () => [],
  },
)
</script>

<template>
  <section class="page-hero">
    <div class="page-hero__content">
      <div class="page-hero__badges">
        <span v-for="badge in badges" :key="badge" class="pill pill--soft">{{ badge }}</span>
      </div>
      <h1>{{ title }}</h1>
      <p>{{ subtitle }}</p>
      <div class="page-hero__actions">
        <slot name="actions" />
      </div>
    </div>
    <div class="page-hero__image" :style="image ? { backgroundImage: `url(${image})` } : {}" />
  </section>
</template>

