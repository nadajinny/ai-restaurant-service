<script setup>
defineProps({
  brand: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    required: true,
  },
  navigation: {
    type: Array,
    required: true,
  },
  theme: {
    type: String,
    default: "customer",
  },
});
</script>

<template>
  <div class="app-shell" :data-theme="theme">
    <aside class="app-shell__sidebar">
      <router-link to="/" class="app-shell__brand">
        <span class="app-shell__brand-kicker">{{ brand }}</span>
        <strong>{{ title }}</strong>
      </router-link>
      <p class="app-shell__subtitle">{{ subtitle }}</p>
      <nav class="app-shell__nav">
        <router-link
          v-for="item in navigation"
          :key="item.to"
          :to="item.to"
          class="app-shell__nav-link"
        >
          {{ item.label }}
        </router-link>
      </nav>
    </aside>
    <div class="app-shell__main">
      <header class="app-shell__header">
        <slot name="header" />
      </header>
      <main class="app-shell__content">
        <slot />
      </main>
    </div>
  </div>
</template>
