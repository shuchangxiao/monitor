<script setup>
defineProps({
  name:String,
  activate:Boolean
})
</script>

<template>
  <div :class="`tab-item ${activate ? 'active':''}`">
    {{name}}
  </div>
</template>

<style scoped>
.tab-item{
  font-size: 15px;
  width: 55px;
  height: 55px;
  text-align: center;
  line-height: 55px;
  box-sizing: border-box;
  transition: .03s;
  &:hover{
    cursor: pointer;
    color: var(--el-color-primary);
  }
}
.active{
  border-bottom: solid 2px var(--el-color-primary);
}

</style>