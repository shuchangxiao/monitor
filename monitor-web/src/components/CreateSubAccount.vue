<script setup>
import {reactive,ref} from "vue";
import {Lock, Message, User} from "@element-plus/icons-vue"
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";

defineProps({
  list:Array
})
const emits = defineEmits(['create'])
const form = reactive({
  username:'',
  email:'',
  password:''
})
const formRef = ref()
const valid = ref(false)
const onValidate = (props,isValid)=>valid.value=isValid
const validateUsername = (rule,value,callback) =>{
  if(value===""){
    callback(new Error("请输入用户名"))
  }else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊符号"))
  }else{
    callback()
  }
}

const rule = {
  username:[
    {validator:validateUsername,trigger:['blur','change']},
    {min:4,max:10,message:"用户民的长度必须在4到10个长度直接",trigger:['blur','change']}
  ],
  password:[
    {required:true,message:"请输入密码",trigger:['blur','change']},
    {min:6,max:16,message:"密码长度需在6-16个长度之间",trigger:['blur','change']}
  ],
  email:[
    {required:true,message:"请输入邮箱",trigger:['blur','change']},
    {type:'email',message: "请输入合法的电子邮件",trigger: ['blur','change']}
  ],

}
const checkedClients = []
function onCheck(state,id){
  if(state){
    checkedClients.push(id)
  }else {
    const index = checkedClients.indexOf(id);
    checkedClients.splice(index,1)
  }
}
const createSubAccount = () => {
    formRef.value.validate(isValid => {
      if(checkedClients.length === 0){
        ElMessage.warning("未选择任何服务器")
        return
      }
      if(isValid){
        post("/api/user/sub/create",{
          ...form,
          clients:checkedClients
        },()=>{
          ElMessage.success('子账号创建成功')
          emits('create')
        })
      }
    })
}

</script>

<template>
  <div style="padding: 15px 20px;height: 100%">
    <div style="display: flex;flex-direction: column">
      <div>
        <div class="title">
          <i class="fa-solid fa-user-plus"></i> 添加子账号
        </div>
        <div class="desc">子账号同样用于管理服务器，但是可以自由分配之多的服务器，子账号只能访问被分配到的服务器</div>
        <el-divider style="margin: 10px 0"/>
      </div>
      <div>
        <el-form label-position="top" :model="form" :rules="rule" ref="formRef" @validate="onValidate">
          <el-form-item prop="username" label="请输入用户名">
            <el-input v-model="form.username" maxlength="10" type="text" placeholder="请输入用户名">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email" label="请输入邮箱">
            <el-input v-model="form.email" type="text" placeholder="请输入注册邮箱">
              <template #prefix>
                <el-icon><Message/></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password" label="请输入密码">
            <el-input v-model="form.password" type="password" placeholder="请输入密码">
              <template #prefix>
                <el-icon><Lock/></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <el-divider style="margin: 10px 0"/>
      <el-scrollbar style="flex: 1;">
        <div style="font-size: 13px;color: gray">请在下方选择子账号访问的服务器列表</div>
        <div v-for="item in list" class="item-card">
          <el-checkbox @change="state=>onCheck(state,item.id)" style="align-self: center"/>
          <div>
            <div>
              <span :class="`flag-icon flag-icon-${item.location}`"></span>
              <span style="margin-left: 10px;font-weight: bold;font-size: 14px">{{ item.name}}</span>
            </div>
            <div style="margin-top: 3px;color: gray;font-size: 12px">
              {{ `操作系统：${item.osName} ${item.osVersion}`}}
            </div>
            <div style="margin-top: 3px;color: gray;font-size: 12px">
              {{ `公网ip：${item.ip}` }}
            </div>
          </div>
        </div>
      </el-scrollbar>
      <div style="text-align: center;margin-top: 10px">
        <el-button @click="createSubAccount" type="success"
                   :disabled="!valid" plain>确认创建</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.title{
  font-size: 18px;
  font-weight: bold;
  color: dodgerblue;
}
.desc{
  font-size: 13px;
  color: grey;
  line-height: 16px;
}
.item-card{
  background-color: var(--el-bg-color-page);
  margin-top: 10px;
  padding: 10px;
  border-radius: 5px;
  display: flex;
  gap: 10px;
}
</style>