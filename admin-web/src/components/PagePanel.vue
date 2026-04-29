<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: "",
  },
  endpoint: {
    type: String,
    default: "",
  },
});
</script>

<template>
  <section class="page-panel">
    <div class="page-panel__header">
      <div>
        <h2 class="page-panel__title">{{ title }}</h2>
        <p v-if="description" class="page-panel__description">{{ description }}</p>
      </div>
      <code v-if="endpoint" class="page-panel__endpoint">{{ endpoint }}</code>
    </div>
    <div class="page-panel__body">
      <slot />
    </div>
  </section>
</template>
