<script setup>
import PreviewCard from "@/components/PreviewCard.vue";
import {ref} from "vue";
import {get} from "@/net/index.js";

const list = ref([])
const updateList = ()=>get("/api/monitor/list",data =>{
  list.value = data
})
setInterval(updateList,10000)
updateList()
</script>

<template>
  <div class="manage-main">
    <div class="manage-title">
      <i style="margin-right: 10px" class="fa-solid fa-house"></i>管理主机列表
    </div>
    <div class="desc">
      在这里管理已注册的主机实例，实时监控主机运行状态，快速进行管理和操作
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="card-list">
      <preview-card v-for="item in list" :data="item" :update="updateList"/>
    </div>
  </div>
</template>

<style scoped>
.manage-main{
  margin: 0 20px;
  .manage-title{
    font-size: 22px;
    font-weight: bold;
  }
  .desc{
    font-size: 15px;
    color: grey;
  }
  .card-list{
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
  }
}
</style>