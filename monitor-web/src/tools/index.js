import {ElMessage, ElMessageBox} from "element-plus";
import {post} from "@/net/index.js";
import {useClipboard} from "@vueuse/core";

function fitByUnit(value, unit) {
    const units = ['B', 'KB', 'MB', 'GB', "TB"];
    let index = units.indexOf(unit);

    while ((value < 1 && value !== 0 || value > 1024) && (index >= 0 && index < units.length)) {
        if (value > 1024) {
            value = value / 1024;
            index++;
        } else {
            value = value * 1024;
            index--;
        }
    }

    // 添加类型检查
    if (typeof value === 'number') {
        return value.toFixed(2) + " " + units[index];
    } else {
        // 处理错误情况，可能是抛出错误或者返回一个错误信息
        console.error('Value is not a number:', value);
        return null; // 或者其他适当的错误处理
    }
}
function percentageToStatus(percentage) {
    if(percentage < 50) {
        return 'success';
    }else if(percentage < 80) {
        return 'warning';
    }else {
        return 'exception';
    }
}
function cpuNameToImage(name) {
    if(name.indexOf('Intel') >= 0) {
        return 'Intel.png';
    }else if(name.indexOf('AMD') >= 0) {
        return 'AMD.png';
    }else {
        return 'Apple.png';
    }
}
function osNameToIcon(name){
    if(name.indexOf('Ubuntu')>=0){
        return {icon:'fa-ubuntu',color:'#db4c1a'}
    }else if(name.indexOf('Windows')>=0){
        return {icon:'fa-windows',color:'#3578b9'}
    }else if (name.indexOf('MacOs')>=0){
        return {icon:'fa-apple',color:'#9dcd30'}
    }else if(name.indexOf('Debian')>=0){
        return {icon:'fa-debian',color:'#a80836'}
    }else {
        return {icon: 'fa-linux',color: 'grey'}
    }
}
function rename(id,name,update){
    ElMessageBox.prompt('请输入新服务器主机名称','修改名称',{
        confirmButtonText:'确认',
        cancelButtonText:'取消',
        inputValue:name,
        inputPattern:
            /^[a-zA-Z0-9_\u4e00-\u9fa5]{1,10}$/,
    }).then(({value})=>{
        post('/api/monitor/rename',{
            id:id,
            name:value
        },()=>{
            ElMessage.success("主机名字已更新")
            update()
        })
    })

}
const {copy} = useClipboard()
const copyIp = (ip)=>copy(ip).then(()=>ElMessage.success("成功赋值ip到剪切板"))

export {fitByUnit,percentageToStatus,cpuNameToImage,osNameToIcon,rename,copyIp}