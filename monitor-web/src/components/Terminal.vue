<script setup>
import {onMounted,ref,onBeforeUnmount} from "vue"
import axios from "axios";
import {ElMessage} from "element-plus";
import {AttachAddon} from "xterm-addon-attach/src/AttachAddon.ts";
import {Terminal} from "xterm";
import "xterm/css/xterm.css"
import {takeAccessToken} from "@/net/index.js";

const props = defineProps({
  id:Number
})
const emit = defineEmits(['dispose'])
const terminalRef = ref()
// const socket = new WebSocket(`ws:${axios.defaults.baseURL.slice(5,17)}8090/terminal?clientId=${props.id}&token=Bearer ${takeAccessToken()}`)
const socket = new WebSocket(`ws:${axios.defaults.baseURL.slice(5)}/terminal/${props.id}`)
socket.onclose = evt => {
  if(evt.code !== 1000){
    ElMessage.warning(`连接失败：${evt.reason}`)
  }else {
    ElMessage.success('远程连接以断开')
  }
  emit('dispose')
}
const attachAddon = new AttachAddon(socket)
const term = new Terminal({
  lineHeight:1.2,
  rows:20,
  fontSize:14,
  fontFamily:"Monaco,Menlo,Consolas",
  fontWeight:"bold",
  theme:{
    background:'#000000'
  },
  cursorBlink:true,
  cursorStyle:"underline",
  scrollback:100,
  tabStopWidth:4,
})
term.loadAddon(attachAddon)
onMounted(()=>{
  term.open(terminalRef.value)
  term.focus()
})
onBeforeUnmount(()=>{
  socket.close()
  term.dispose()
})
</script>

<template>
  <div ref="terminalRef" class="xterm"/>
</template>

<style scoped>

</style>