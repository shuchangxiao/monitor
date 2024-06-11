<script setup>

import ChangePassword from "@/components/ChangePassword.vue";
import {Delete, Plus} from "@element-plus/icons-vue";
import {ref} from "vue";
import CreateSubAccount from "@/components/CreateSubAccount.vue";
import {get, post} from "@/net/index.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {userStore} from "@/store/index.js";

const user = userStore()
const createAccount = ref(false)
const simpleList = ref([])
const accounts = ref([])
const initSubAccounts = ()=>
    get("/api/user/sub/list",list=>{
      accounts.value = list
    })
if(user.isAdmin){
  get("/api/monitor/simple-list",list=>{
    simpleList.value = list
    initSubAccounts()
  })
}
const deleteSubAccount = (id)=>{
  ElMessageBox.confirm("删除用户后关于子用户一起信息将删除，您确定要这么做吗？","删除子用户",{
    confirmButtonText:'确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(()=>{
    get("/api/user/sub/delete?id="+id,()=>{
      ElMessage.success('删除子用户成功')
      initSubAccounts()
    })
  }).catch((error) => {
    // 判断是否是用户取消的操作
    if (error === 'cancel') {
    } else {
      // 其他错误情况
      ElMessage.error("子用户删除失败，请联系管理员");
    }
  })

}
const createSuccess = ()=>{
  createAccount.value=false
  initSubAccounts()
}
</script>

<template>
  <div style="display: flex;flex-direction: row;gap: 10px">
    <ChangePassword/>
    <div class="info-card" style="flex: 1">
      <div class="title"><i class="fa-solid fa-users"></i>子用户管理</div>
      <el-divider style="margin: 10px 0"></el-divider>
      <div v-if="accounts.length !== 0" style="text-align: center">
        <div class="account-card"  v-for="item in accounts">
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" :size="50"/>
          <div>
            <div style="font-size: 14px;line-height: 18px">
              <span style="font-weight: bold">{{item.username}}</span>
              <span style="margin-left: 15px;color: gray">{{ `管理 ${item.clientList.length} 个服务器`}}</span>
            </div>
            <div style="font-size: 14px;line-height: 16px;color: gray;margin-top: 10px">
              {{ item.email}}
            </div>
          </div>
          <div style="align-self: center;flex: 1;text-align: right">
            <el-button :icon="Delete" text type="danger" @click="deleteSubAccount(item.id)">删除子账号</el-button>
          </div>
        </div>
        <el-button :icon="Plus" type="primary"
                   @click="createAccount = true" plain>添加更多子用户</el-button>
      </div>
      <div v-else>
        <el-empty :image-size="100" description="还没有添加子用户" v-if="user.isAdmin">
          <el-button @click="createAccount = true"
                     :icon="Plus" type="primary">添加子用户</el-button>
        </el-empty>
        <el-empty :image-size="100" description="子账号只能由管理员账号进行创建" v-else>
        </el-empty>
      </div>

    </div>
    <el-drawer v-model="createAccount" size="350" :with-header="false">
      <CreateSubAccount :list="simpleList" @create="createSuccess"/>
    </el-drawer>
  </div>
</template>

<style scoped>

.info-card{
  border-radius: 7px;
  padding: 15px 20px;
  background-color: var(--el-bg-color);
  height: fit-content;
  .title{
    font-weight: bold;
    font-size: 18px;
    color: dodgerblue;
  }
  .account-card{
    background-color: var(--el-bg-color-page);
    border-radius: 5px;
    padding: 10px;
    margin: 10px;
    display: flex;
    gap: 10px;
  }
}
:deep(.el-drawer){
  margin:10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}
</style>