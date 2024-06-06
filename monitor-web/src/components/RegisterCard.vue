<script setup>
import axios from "axios";

defineProps({
  token:String
})
</script>

<template>
  <div class="register-card">
    <div class="title"><i class="fa-regular fa-square-plus"></i>添加新主机 </div>
    <div class="desc">请按照一下步骤完成新主机的添加，添加后即可实时管理服务器以及检测运行状态</div>
    <el-divider style="margin: 10px 0"/>
    <div class="sub-title">1.部属服务器</div>
    <div class="desc">在需要监控的服务器上运行监控客户端，客户端程序依赖与Java17运行环境，请提前安装好，启动完成后即可进行下一步。</div>
    <div class="sub-title" style="margin-top: 10px">2.输入监控服务器地址</div>
    <div class="desc">此地址用于客户端上报运行时数据，请务必保证正确填写</div>
    <el-input v-model="axios.defaults.baseURL" readonly/>
    <div class="sub-title" style="margin-top: 10px">3.输入授权码</div>
    <div class="desc">客户端所需所有请求必须携带授权码才能被服务端正确识别</div>
    <el-input :placeholder="token" readonly/>
  </div>
</template>

<style scoped>
.register-card{
  margin: 15px 20px;
  .title{
    font-size: 18px;
    font-weight: bold;
  }
  .sub-title{
    font-size: 16px;
    font-weight: bold;
    color: dodgerblue;
  }
  .desc{
    font-size: 13px;
  }
}
</style>