<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  badge: {
    type: String,
    default: "",
  },
});
</script>

<template>
  <section class="page-hero">
    <span v-if="badge" class="page-hero__badge">{{ badge }}</span>
    <h1 class="page-hero__title">{{ title }}</h1>
    <p class="page-hero__description">{{ description }}</p>
    <slot />
  </section>
</template>
