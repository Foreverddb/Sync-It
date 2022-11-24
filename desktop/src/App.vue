<template>
  <router-view/>
</template>

<style lang="scss">

</style>
<script>
import Sider from "@/components/nav/Sider";
export default {
  components: {Sider},
}
</script>
<style scoped>
#app {
  overflow: hidden;
}
.layout {
  height: 100vh;
  overflow: hidden;
}
.layout-sider {
  overflow: auto;
  overflow-x: hidden;
}
.layout-content {
  padding: 20px;
}
</style>
