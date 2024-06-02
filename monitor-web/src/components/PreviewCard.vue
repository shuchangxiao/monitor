<script setup>
import {fitByUnit} from "@/tools/index.js"
import {useClipboard} from "@vueuse/core";
import {ElMessage, ElMessageBox} from "element-plus";
import {post} from "@/net/index.js";
const props = defineProps({
  data:Array,
  update:Function
})
const {copy} = useClipboard()
const copyIp = ()=>copy(props.data.ip).then(()=>ElMessage.success("成功赋值ip到剪切板"))
function rename(){
  ElMessageBox.prompt('请输入新服务器主机名称','修改名称',{
    confirmButtonText:'确认',
    cancelButtonText:'取消',
    inputValue:props.data.name,
    inputPattern:
    /^[a-zA-Z0-9_\u4e00-\u9fa5]{1,10}$/,
  }).then(({value})=>{
    post('/api/monitor/rename',{
      id:props.data.id,
      name:value
    },()=>{
      ElMessage.success("主机名字已更新")
      props.update()
    })
  })

}
</script>

<template>
  <div class="instance-card">
    <div style="display: flex;justify-content: space-between">
      <div>
        <div class="name">
          <span :class="`flag-icon flag-icon-${props.data.location}`"></span>&nbsp;
          <span>{{ props.data.name }}</span>
          <i @click="rename" style="margin-left: 5px" class="fa-solid fa-pen-to-square"></i>
        </div>
        <div class="os">
          操作系统:&nbsp;{{ props.data.osName }}{{ props.data.osVersion }}
        </div>
      </div>
      <div class="status" v-if="props.data.online">
        <i style="color: #1818" class="fa-solid fa-circle-play"></i>
        <span>运行中</span>
      </div>
      <div class="status" v-else>
        <i style="color: #8a8a8a" class="fa-solid fa-circle-play"></i>
        <span>离线</span>
      </div>
    </div>
    <el-divider style="margin: 10px 0"></el-divider>
    <div class="network">
      <span style="">公网IP号:&nbsp;{{ props.data.ip }}</span>
      <i @click.stop="copyIp" style="color: dodgerblue;margin-left: 5px" class="fa-solid fa-copy interact-item"></i>
    </div>
    <div class="cpu">
      <span>处理器：{{props.data.cpuName}}</span>
    </div>
    <div class="hardware">
      <i style="margin-right: 10px" class="fa-solid fa-microchip"></i>
      <span>{{ props.data.cpuCore }} CPU</span>
      <i style="margin:0 10px" class="fa-solid fa-memory"></i>
      <span>{{ props.data.memory.toFixed(2) }}GB</span>
    </div>
    <div class="progress">
      <span>CPU: {{ (props.data.cpuUsage*100).toFixed(2) }}%</span>
      <el-progress :percentage="(props.data.cpuUsage*100).toFixed(2)" :stroke-width="5" :show-text="false"/>
    </div>
    <div class="progress">
      <span>内存: {{ `${props.data.memoryUsage.toFixed(2)}%` }}</span>
      <el-progress :percentage="(props.data.memoryUsage/props.data.memory*100).toFixed(2)" :stroke-width="5" :show-text="false"/>
    </div>
    <div class="network-flow">
      <span>网络情况</span>
      <div>
        <i class="fa-solid fa-upload"></i>
        <span>{{ `${fitByUnit(props.data.networkDownload,'KB')}/s` }}</span>
        <el-divider direction="vertical"/>
        <i class="fa-solid fa-download"></i>
        <span>{{ `${fitByUnit(props.data.networkUpload,'KB')}/s` }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-progress-bar__inner){
  background-color: #18cb18;
}
:deep(.el-progress-bar__outer){
  background-color: #18cb1822;
}
.instance-card{
  width: 320px;
  padding: 15px;
  background-color: var(--el-bg-color);
  border-radius: 5px;
  box-sizing: border-box;
  color:#6b6b6b;
  .name{
    font-size: 15px;
    font-weight: bold;
  }
  .status{
    font-size: 13px;
  }
  .os{
    font-size: 13px;
    color: grey;
  }
  .network{
    font-size: 13px;
  }
  .hardware{
    margin-top: 5px;
    font-size: 13px;
  }
  .progress{
    margin-top: 8px;
    font-size: 12px;
  }
  .network-flow{
    margin-top: 10px;
    font-size: 12px;
    display: flex;
    justify-content: space-between;
  }
  .cpu{
    font-size: 13px;
  }
}
.dark .instance-card{
  color: #d9d9d9;
}
.interact-item{
  transition: 0.3s;
  &:hover{
    cursor:pointer;
    scale: 1.1;
    opacity: 0.8;
  }
}
</style>