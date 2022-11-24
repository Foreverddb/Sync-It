<template>
  <a-layout class="layout">
    <a-layout-sider
        class="layout-sider"
        width="256px">
      <Sider style="height: 100vh"/>
    </a-layout-sider>
    <a-layout>
      <a-layout-content class="layout-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script>
import Sider from "@/components/nav/Sider";


export default {
  name: 'Index',
  data() {
    return {
      i: 0,
      lastTime: 0
    }
  },
  components: {
    Sider
  },
  methods: {

  },
  mounted() {


  }
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
