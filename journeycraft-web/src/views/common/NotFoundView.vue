<template>
  <div class="page-card page-card--center">
    <div class="not-found">
      <div class="not-found__code">404</div>
      <h1>页面未找到</h1>
      <p>当前路径还没有对应页面，请返回首页继续浏览旅行产品结构。</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

