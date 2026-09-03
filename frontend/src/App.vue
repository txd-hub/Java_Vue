<script setup lang="ts">
// App.vue根组件
// 功能：作为应用的根容器，包含路由视图
</script>

<template>
  <!-- 路由视图，根据路由配置渲染对应页面 -->
  <router-view />
</template>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}
</style>