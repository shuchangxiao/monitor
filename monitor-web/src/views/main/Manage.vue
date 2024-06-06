<script setup>
import PreviewCard from "@/components/PreviewCard.vue";
import {ref,reactive} from "vue";
import {get} from "@/net/index.js";
import ClientDetail from "@/components/ClientDetail.vue";
import RegisterCard from "@/components/RegisterCard.vue";
import {Plus} from "@element-plus/icons-vue";

const list = ref([])
const updateList = ()=>get("/api/monitor/list",data =>{
  list.value = data
})

setInterval(updateList,10000)
updateList()

const detail = reactive({
  show:false,
  id:-1
})
const displayClientDetail = (id)=>{
  detail.show = true
  detail.id = id
}
const register = reactive({
  show:false,
  token:''
})
const refreshToken = ()=>get('/api/monitor/register',data=>register.token=data)
</script>

<template>
  <div class="manage-main">
    <div style="display: flex;justify-content: space-between">
      <div>
        <div class="manage-title">
          <i style="margin-right: 10px" class="fa-solid fa-house"></i>管理主机列表
        </div>
        <div class="desc">
          在这里管理已注册的主机实例，实时监控主机运行状态，快速进行管理和操作
        </div>
      </div>
      <el-button type="primary" @click="register.show=true" :icon="Plus">添加新主机</el-button>
    </div>

    <el-divider style="margin: 10px 0"/>
    <div class="card-list" v-if="list.length">
      <preview-card v-for="item in list" :data="item" :update="updateList" @click="displayClientDetail(item.id)"/>
    </div>
    <el-empty description="还没有任何的主机连接，点击右上脚添加一个吧" v-else/>
    <el-drawer size="520" :show-close="false" v-model="detail.show" :with-header="false" v-if="list.length" @close="detail.id = -1">
      <ClientDetail :id="detail.id" :update="updateList"/>
    </el-drawer>
    <el-drawer v-model="register.show" direction="btt" :with-header="false"
                style="width: 600px;margin: 60px auto" size="350" @open="refreshToken">
      <RegisterCard :token="register.token"/>
    </el-drawer>
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
:deep(.el-drawer){
  margin:10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}
</style>