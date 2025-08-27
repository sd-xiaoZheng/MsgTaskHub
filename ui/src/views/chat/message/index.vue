<template>
    <div class="MessageLayout">
       <BasicMessageLayout />
<!--      <AbleLayout v-if="true" />-->
    </div>
</template>

<script>
import BasicMessageLayout from './layout/basicLayout.vue'
import AbleLayout from './layout/ableLayout.vue'
export default {
  name: 'ChatMessage',
  components: {
    BasicMessageLayout,AbleLayout
  },
  data() {
    return {
    }
  },
  computed: {
  },
  created() {

  },
  beforeDestroy() {
  },
  watch: {
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
</style>
