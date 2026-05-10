<script setup lang="ts">
defineProps<{
  title: string
  subtitle: string
  image: string
  tags?: string[]
  note?: string
  score?: string
  linkText?: string
}>()

defineEmits<{
  (event: 'click'): void
}>()
</script>

<template>
  <article class="entity-card" @click="$emit('click')">
    <div class="entity-card__image" :style="{ backgroundImage: `url(${image})` }" />
    <div class="entity-card__body">
      <div class="entity-card__head">
        <div>
          <h3>{{ title }}</h3>
          <p>{{ subtitle }}</p>
        </div>
        <span v-if="score" class="pill pill--accent">{{ score }}</span>
      </div>
      <div v-if="tags?.length" class="entity-card__tags">
        <span v-for="tag in tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
      <div v-if="note" class="entity-card__note">{{ note }}</div>
      <div v-if="linkText" class="entity-card__link">{{ linkText }}</div>
    </div>
  </article>
</template>

