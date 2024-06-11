<script setup>
import {watch,reactive,computed} from "vue";
import {get, post} from "@/net/index.js";
import {fitByUnit, cpuNameToImage, osNameToIcon, rename, copyIp, percentageToStatus} from "@/tools/index.js";
import {userStore} from "@/store/index.js";
import {ElMessage, ElMessageBox} from "element-plus";
import RuntimeHistory from "@/components/RuntimeHistory.vue";
import {Delete} from "@element-plus/icons-vue";

const user = userStore()
const location = [
  {name:'cn',desc:'中国大陆'},
  {name:'hk',desc:'香港'},
  {name:'jp',desc:'日本'},
  {name:'us',desc:'美国'},
  {name:'sg',desc:'新加坡'},
  {name:'kr',desc:'韩国'},
  {name:'de',desc:'德国'},
]
const props = defineProps({
  id:Number,
  update:Function
})
const details = reactive({
  base:{},
  runtime:{list:[]},
  editNode:false
})
const nodeEdit = reactive({
  name:'',
  location:''
})
const enableNodeEdit = ()=>{
  details.editNode = true
  nodeEdit.name = details.base.node
  nodeEdit.location = details.base.location
}
// const emit = defineEmits(['delete'])
function updateDetails(){
  props.update()
  init(props.id)
}
setInterval(()=>{
  if(props.id !==-1){
    get(`/api/monitor/runtime-now?clientId=${props.id}`,data=>{
      if(details.runtime.list.length>=360){
        details.runtime.list.splice(0,1)
      }
      details.runtime.list.push(data)
    })
  }
},10000)
const init = id=>{
  if(id !== -1){
    details.base={}
    details.runtime={list:[]}
    get(`/api/monitor/details?clientId=${id}`,data=>{
      console.log(data)
      Object.assign(details.base,data)
    })
    get(`/api/monitor/runtime-history?clientId=${id}`,data=>{
      Object.assign(details.runtime,data)
    })
  }
}
watch(()=>props.id,init,{immediate:true})
function submitNodeEdit(){
  post('/api/monitor/rename-node',{
    id:props.id,
    node:nodeEdit.name,
    location:nodeEdit.location
  },()=>{
    details.editNode = false
    updateDetails()
    ElMessage.success("节点信息已更新")
  })
}
function deleteClient() {
  ElMessageBox.confirm("删除主机后所有统计数据将丢失，您确定要这么做吗？","删除主机",{
    confirmButtonText:'确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(()=>{
    get(`/api/monitor/delete?clientId=${props.id}`,()=>{
      // emit('delete')
      props.update()
      ElMessage.success('主机移出成功')
    })
  }).catch((error) => {
    // 判断是否是用户取消的操作
    if (error === 'cancel') {
    } else {
      // 其他错误情况
      ElMessage.error("主机移出失败，请联系管理员");
    }
  })
}
const now = computed(()=>details.runtime.list[details.runtime.list.length-1])
</script>

<template>
  <el-scrollbar>
    <div class="client-details" v-loading="Object.keys(details.base).length === 0">
      <div v-if="Object.keys(details.base).length">
        <div style="display: flex;justify-content: space-between">
          <div class="title">
            <i style="color: #1818" class="fa-solid fa-circle-play"></i>
            <span>服务器信息</span>
          </div>
          <el-button :disabled="!user.isAdmin" @click="deleteClient" :icon="Delete" type="danger" size="large" plain text>删除此主机</el-button>
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
            <i @click="rename(details.base.id,details.base.name,updateDetails)" style="margin-left: 5px" class="fa-solid fa-pen-to-square interact-item"></i>
          </div>
          <div>
            <span>运行状态</span>
            <i style="color: #1818" class="fa-solid fa-circle-play"  v-if="details.base.online"></i>&nbsp;
            <i style="color: #8a8a8a" class="fa-solid fa-circle-play"  v-else></i>&nbsp;
            <span>{{details.base.online?'运行中':'离线'}}</span>
          </div>
          <div v-if="!details.editNode">
            <span>服务器节点</span>
            <span :class="`flag-icon flag-icon-${details.base.location}`"></span>&nbsp;
            <span>{{details.base.node}}</span>
            <i @click.stop="enableNodeEdit" style="color: dodgerblue;margin-left: 5px" class="fa-solid fa-copy interact-item"></i>
          </div>
          <div v-else>
            <span>服务器节点</span>
            <div style="display: inline-block;height: 15px">
              <div style="display: flex">
                <el-select v-model="nodeEdit.location" style="width: 80px" size="small">
                  <el-option v-for="item in location" :value="item.name">
                    <span :class="`flag-icon flag-icon-${item.name}`"></span>
                    {{item.desc}}
                  </el-option>
                </el-select>
                <el-input v-model="nodeEdit.name" style="margin-left: 10px"
                          size="small" placeholder="请输入节点名称" minlength="1" maxlength="10"/>
                <div style="margin-left: 10px">
                  <i @click.stop="submitNodeEdit" class="fa-solid fa-check interact-item"></i>
                </div>
              </div>
            </div>
          </div>
          <div>
            <span>公网IP地址</span>
            <span>
            {{details.base.ip}}
            <i @click.stop="copyIp(details.base.ip)" style="color: dodgerblue;margin-left: 5px" class="fa-solid fa-copy interact-item"></i>
          </span>
          </div>
          <div style="display: flex">
            <span>处理器</span>
            <span>{{ details.base.cpuName }}</span>
            <el-image style="height: 20px;margin-left: 10px" :src="`/cpu-icons/${cpuNameToImage(details.base.cpuName)}`"></el-image>
          </div>
          <div>
            <span>硬件配置信息</span>
            <span>
            <i style="margin-right: 10px" class="fa-solid fa-microchip"></i>
            <span>{{ details.base.cpuCore }} CPU</span>
            <i style="margin:0 10px" class="fa-solid fa-memory"></i>
            <span>{{ details.base.memory.toFixed(2) }}GB</span>
          </span>
          </div>
          <div>
            <span>操作系统</span>
            <i :style="{color:osNameToIcon(details.base.osName).color}"
               :class="`fa-brands ${osNameToIcon(details.base.osName).icon}`"></i>
            <span style="margin-left: 5px">{{ details.base.osName}}{{ details.base.osVersion }}</span>
          </div>
        </div>
        <div class="title">
          <i style="color: #1818" class="fa-solid fa-circle-play"></i>
          <span>实时监控</span>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div v-if="details.base.online" v-loading="!details.runtime.list.length">
          <div style="display: flex" v-if="details.runtime.list.length" >
            <el-progress type="dashboard" :width="100" :percentage="now.cpuUsage*100"
                         :status="percentageToStatus(now.cpuUsage*100)">
              <div style="font-size: 17px;font-weight: bold;color: initial">CPU</div>
              <div style="font-size: 14px;margin-top: 5px;color: gray">{{ (now.cpuUsage*100).toFixed(2) }}%</div>
            </el-progress>
            <el-progress style="margin-left: 20px"
                         type="dashboard" :width="100" :percentage="(now.memoryUsage/details.runtime.memory*100).toFixed(2)"
                         :status="percentageToStatus(now.memoryUsage/details.runtime.memory*100)">
              <div style="font-size: 17px;font-weight: bold;color: initial">内存</div>
              <div style="font-size: 14px;margin-top: 5px;color: gray">{{ (now.memoryUsage/details.runtime.memory*100).toFixed(2) }}</div>
            </el-progress>
            <div style="display: flex;margin-left: 30px;flex: 1;flex-direction: column;height: 80px;justify-content: space-between">
              <div style="font-size: 14px">
                <div>实时网络数据</div>
                <div style="margin-top: 5px">
                  <i style="color:orange;" class="fa-solid fa-upload"></i>
                  <span>{{ `${fitByUnit(now.networkDownload,'KB')}/s` }}</span>
                  <el-divider direction="vertical"/>
                  <i style="color: dodgerblue" class="fa-solid fa-download"></i>
                  <span>{{ `${fitByUnit(now.networkUpload,'KB')}/s` }}</span>
                </div>
              </div>
              <div style="font-size: 14px">
                <div style="font-size: 13px;display: flex;justify-content: space-between">
                  <div style="margin-bottom: 3px">
                    <i class="fa-solid fa-hard-drive"></i>
                    <span>磁盘容量</span>
                  </div>
                  <div>{{ now.disUsage.toFixed(1) }} GB/{{ details.runtime.disk.toFixed(1) }} GB</div>
                </div>
                <el-progress type="line" :status="percentageToStatus(now.disUsage/details.runtime.disk*100)"
                             :percentage="(now.disUsage/details.runtime.disk*100).toFixed(2)" :show-text="false"></el-progress>
              </div>
            </div>
          </div>
          <RuntimeHistory style="margin-top: 20px" :data="details.runtime.list"/>
        </div>
        <el-empty v-else description="服务器处于离线状态中"></el-empty>
      </div>
    </div>
  </el-scrollbar>
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
  display: flex;
  .title{
    color: dodgerblue;
    font-size: 18px;
    font-weight: bold;
    align-self: center;
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