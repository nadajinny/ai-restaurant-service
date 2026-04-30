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
});
</script>

<template>
  <section class="page-panel">
    <div class="page-panel__header">
      <div>
        <h2 class="page-panel__title">{{ title }}</h2>
        <p v-if="description" class="page-panel__description">{{ description }}</p>
      </div>
    </div>
    <div class="page-panel__body">
      <slot />
    </div>
  </section>
</template>
