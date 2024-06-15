<script setup>

import Card from "@/components/Card.vue";
import {EditPen, Message, Select} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import {userStore} from "@/store/index.js";
import {ElMessage} from "element-plus";
import {post,get} from "@/net/index.js";

const user = userStore()
const codeTime = ref(0)
const emailFormRef = ref()
const rules = {
  email:[
    {required:true,message:"请输入邮箱",trigger:['blur','change']},
    {type:'email',message: "请输入合法的电子邮件",trigger: ['blur','change']}
  ]
}
const emailForm = reactive({
  now_email:"",
  email:"",
  code:''
})
function sendEmailCode(){
  codeTime.value = 60
  get(`/api/auth/ask-code?email=${user.user.email}&type=modify`,()=>{
    ElMessage.success("验证码已经发送，请注意查收")
    const handle = setInterval(()=>{
      codeTime.value--
      if(codeTime.value===0){ clearInterval(handle)}

    },1000)
  },(message)=>{
    ElMessage.warning(message)
    codeTime.value = 0
  })
}
function modifyEmail(){
  emailFormRef.value.validate((isValid)=>{
    if(isValid){
      emailForm.now_email = user.user.email
      post("/api/auth/change-email",{...emailForm},()=>{
        ElMessage.success("邮件修改成功")
        user.user.email = emailForm.email
        emailForm.code = ''
      })
    }
  })
}
</script>

<template>
  <Card style="margin-top: 10px;" :icon="Message" title="电子邮件设置" desc="您可以在这里修改默认绑定的电子邮件">
    <el-form ref="emailFormRef"  :rules="rules" :model="emailForm" label-position="top" style="margin: 0 10px 10px 10px">
      <el-form-item label="现在个人邮箱">
        <el-input v-model="user.user.email" :disabled="true"/>
      </el-form-item>
      <el-form-item prop="code">
        <el-row style="width: 100%" gutter="10">
          <el-col :span="17">
            <el-input maxlength="6" type="text" placeholder="请输入验证码" v-model="emailForm.code">
              <template #prefix>
                <el-icon><EditPen/></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="6">
            <el-button  @click="sendEmailCode" type="success" :disabled="codeTime">
              {{codeTime>0?`请稍后在试${codeTime}秒`:"获取验证码"}}
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="新邮箱" prop="email">
        <el-input v-model="emailForm.email"/>
      </el-form-item>
      <div>
        <div>
          <el-button  @click="modifyEmail" :icon="Select" type="success">保存用户信息</el-button>
        </div>
      </div>
    </el-form>
  </Card>
</template>

<style scoped>

</style>