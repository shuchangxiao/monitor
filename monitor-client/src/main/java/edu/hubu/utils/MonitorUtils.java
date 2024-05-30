package edu.hubu.utils;

import edu.hubu.entity.BaseDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Objects;
import java.util.Properties;

@Component
@Slf4j
public class MonitorUtils {
    private final SystemInfo info = new SystemInfo();
    private final Properties properties = System.getProperties();
    public BaseDetail monitorBaseDetail(){
        OperatingSystem operatingSystem = info.getOperatingSystem();
        HardwareAbstractionLayer hardware = info.getHardware();
        // 初始化磁盘总大小为0
        double totalDiskSize = 0;

        // 遍历所有的磁盘存储并累加大小
        for (HWDiskStore diskStore : hardware.getDiskStores()) {
            totalDiskSize += diskStore.getSize();
        }
        return new BaseDetail()
                .setOrArch(properties.getProperty("os.arch"))
                .setName(operatingSystem.getFamily())
                .setOsVersion(operatingSystem.getVersionInfo().getVersion())
                .setOsBit(operatingSystem.getBitness())
                .setCPUName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCPUCore(hardware.getProcessor().getLogicalProcessorCount())
                .setMemory((double) hardware.getMemory().getTotal() /1024/1024/1024)
                .setDisk(totalDiskSize /1024/1024/1024)
                .setIP(Objects.requireNonNull(this.findNetWorkInterface(hardware)).getIPv4addr()[0]);
    }
    private NetworkIF findNetWorkInterface(HardwareAbstractionLayer hardware){
        for (NetworkIF networkIF : hardware.getNetworkIFs()) {
            String[] ipv4 = networkIF.getIPv4addr();
            NetworkInterface networkInterface = networkIF.queryNetworkInterface();
            try {
                if(!networkInterface.isLoopback() && !networkInterface.isPointToPoint()
                        && networkInterface.isUp() && !networkInterface.isVirtual()
                        && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("en"))
                        && ipv4.length>0){
                    return networkIF;
                }
            } catch (SocketException e) {
                log.error("读取网络信息接口出错");
                return null;
            }
        }
        return null;
    }

}
