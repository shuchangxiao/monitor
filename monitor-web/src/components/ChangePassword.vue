<script setup>
import {Lock} from "@element-plus/icons-vue";
import {ref,reactive} from "vue";
import {logout, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const form = reactive({
  password:'',
  new_password:'',
  repeat_new_password:''
})
const formRef = ref()
const valid = ref(false)
const validatePasswordRepeat = (rule,value,callback)=>{
  if(value===""){
    callback(new Error("请再此输入密码"))
  }else if(value !== form.new_password) {
    callback(new Error("两次输入密码不一致"))
  }else{
    callback()
  }
}
const rule = {
  password:[
    {required:true,message:"请输入原先密码",trigger:"blur"}
  ],
  new_password:[
    {required:true,message:"请输入新密码",trigger:"blur"},
    {min:6,max:6,message: "密码长度必须在6-16之间",trigger: ["blur"]}
  ],
  repeat_new_password:[
    {validator:validatePasswordRepeat,rigger:['blur','change']},
  ]
}
const onValidate = (prop,isValid) =>{
  valid.value = isValid
}
function resetPassword(){
  formRef.value.validate(valid=>{
    if(valid){
      post("/api/auth/change-password",{
        password:form.password,
        newPassword:form.new_password
      },()=>{
        ElMessage.success("修改密码成功")
        logout(()=>router.push("/"))
      },()=>{
        ElMessage.warning("修改密码失败,请检查密码是否正确")
      })
    }
  })
}
</script>

<template>
  <div class="main-password">
    <div style="font-size: 18px;font-weight: bold;color: dodgerblue">修改管理员密码</div>
    <el-divider style="margin: 10px 0"></el-divider>
    <el-form :rules="rule" @validate="onValidate" label-width="100px" :model="form" ref="formRef">
      <el-form-item label="当前密码" prop="password">
        <el-input :prefix-icon="Lock" placeholder="请输入当前密码" v-model="form.password"/>
      </el-form-item>
      <el-form-item label="新密码" prop="new_password">
        <el-input :prefix-icon="Lock" placeholder="请输入新的密码" v-model="form.new_password"/>
      </el-form-item>
      <el-form-item label="重复新密码" prop="repeat_new_password">
        <el-input :prefix-icon="Lock" placeholder="请再次输入新的密码" v-model="form.repeat_new_password"/>
      </el-form-item>
    </el-form>
    <div  style="text-align: center">
      <el-button @click="resetPassword" type="success">立即重置密码</el-button>
    </div>
  </div>
</template>

<style scoped>
.main-password{
  width: 500px;
  height: 250px;
  border-radius: 10px;
  padding: 15px 20px;
  background-color: var(--el-bg-color);
}
</style>