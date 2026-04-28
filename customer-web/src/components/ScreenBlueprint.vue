<script setup>
defineProps({
  sections: {
    type: Array,
    default: () => [],
  },
});
</script>

<template>
  <div class="screen-blueprint">
    <article
      v-for="section in sections"
      :key="section.title"
      class="screen-blueprint__card"
    >
      <h3>{{ section.title }}</h3>
      <p>{{ section.description }}</p>
      <ul>
        <li v-for="item in section.items" :key="item">{{ item }}</li>
      </ul>
    </article>
  </div>
</template>
