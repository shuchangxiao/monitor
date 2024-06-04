<script setup>
import {watch,reactive} from "vue";
import {get} from "@/net/index.js";
import {fitByUnit} from "@/tools/index.js";
import {useClipboard} from "@vueuse/core";
import {ElMessage} from "element-plus";
const {copy} = useClipboard()
const props = defineProps({
  id:Number,
})
const details = reactive({
  base:{},
  runtime:{}
})
const copyIp = ()=>copy(details.base.ip).then(()=>ElMessage.success("成功赋值ip到剪切板"))

watch(()=>props.id,value => {
  if(value !== -1){
    details.base={}
    get(`/api/monitor/details?id=${value}`,data=>{
      console.log(data)
      Object.assign(details.base,data)
    })
  }
},{immediate:true})
</script>

<template>
  <div class="client-details" v-loading="Object.keys(details.base).length === 0">
    <div v-if="Object.keys(details.base)">
      <div class="title">
        <i style="color: #1818" class="fa-solid fa-circle-play"></i>
        <span>服务器信息</span>
      </div>
      <el-divider style="margin: 10px 0"/>
      <div class="details-list">
        <div>
          <span>服务器ID</span>
          <span>{{details.base.id}}</span>
        </div>
        <div>
          <span>服务器名称</span>
          <span>{{details.base.name}}</span>
        </div>
        <div>
          <span>运行状态</span>
          <i style="color: #1818" class="fa-solid fa-circle-play"  v-if="details.base.online"></i>&nbsp;
          <i style="color: #8a8a8a" class="fa-solid fa-circle-play"  v-else></i>&nbsp;
          <span>{{details.base.online?'运行中':'离线'}}</span>
        </div>
        <div>
          <span>服务器节点</span>
          <span :class="`flag-icon flag-icon-${details.base.location}`"></span>&nbsp;
          <span>{{details.base.node}}</span>
        </div>
        <div>
          <span>公网IP地址</span>
          <span>
            {{details.base.ip}}
            <i @click.stop="copyIp" style="color: dodgerblue;margin-left: 5px" class="fa-solid fa-copy interact-item"></i>
          </span>
        </div>
        <div>
          <span>处理器</span>
          <span>{{ details.base.cpuName }}</span>
        </div>
        <div>
          <span>硬件配置信息</span>
          <span>
            <i style="margin-right: 10px" class="fa-solid fa-microchip"></i>
            <span>{{ details.base.cpuCore }} CPU</span>
            <i style="margin:0 10px" class="fa-solid fa-memory"></i>
            <span>{{ details.base.memory?.toFixed(2) }}GB</span>
          </span>
        </div>
        <div>
          <span>操作系统</span>
          <span>{{ details.base.osName}}{{ details.base.osVersion }}</span>
        </div>
      </div>
      <div class="title">
        <i style="color: #1818" class="fa-solid fa-circle-play"></i>
        <span>实时监控</span>
      </div>
      <el-divider style="margin: 10px 0"/>
      <div style="display: flex">
        <el-progress type="dashboard" :width="100" :percentage="20" status="success">
          <div style="font-size: 17px;font-weight: bold;color: initial">CPU</div>
          <div style="font-size: 14px;margin-top: 5px;color: gray">20%</div>
        </el-progress>
        <el-progress style="margin-left: 20px"
            type="dashboard" :width="100" :percentage="20" status="success">
          <div style="font-size: 17px;font-weight: bold;color: initial">内存</div>
          <div style="font-size: 14px;margin-top: 5px;color: gray">12 GB</div>
        </el-progress>
        <div style="display: flex;margin-left: 30px;flex: 1;flex-direction: column;height: 80px;justify-content: space-between">
          <div style="font-size: 14px">
            <div>实时网络数据</div>
            <div style="margin-top: 5px">
              <i style="color:orange;" class="fa-solid fa-upload"></i>
              <span>20KB/s</span>
              <el-divider direction="vertical"/>
              <i style="color: dodgerblue" class="fa-solid fa-download"></i>
              <span>80KB/s</span>
            </div>
          </div>
          <div style="font-size: 14px">
            <div style="font-size: 13px;display: flex;justify-content: space-between">
              <div style="margin-bottom: 3px">
                <i class="fa-solid fa-hard-drive"></i>
                <span>磁盘容量</span>
              </div>
              <div>6.6 GB/40 GB</div>
            </div>
            <el-progress type="line" status="success" :percentage="25" :show-text="false"></el-progress>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.interact-item{
  transition: 0.3s;
  &:hover{
    cursor:pointer;
    scale: 1.1;
    opacity: 0.8;
  }
}
.client-details{
  height: 100%;
  padding: 20px;
  .title{
    color: dodgerblue;
    font-size: 18px;
    font-weight: bold;
  }
  .details-list{
    font-size: 14px;
    & div{
      margin-bottom: 10px;
      & span:first-child{
        color: gray;
        font-size: 13px;
        font-weight: normal;
        width: 120px;
        display: inline-block;
      }
      & span{
        font-weight: bold;
      }
    }
  }
}
</style>