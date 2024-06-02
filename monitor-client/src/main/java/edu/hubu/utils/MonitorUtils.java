package edu.hubu.utils;

import edu.hubu.entity.BaseDetail;
import edu.hubu.entity.RuntimeDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
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
                .setOsArch(properties.getProperty("os.arch"))
                .setOsName(operatingSystem.getFamily())
                .setOsVersion(operatingSystem.getVersionInfo().getVersion())
                .setOsBit(operatingSystem.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCore(hardware.getProcessor().getLogicalProcessorCount())
                .setMemory((double) hardware.getMemory().getTotal() /1024/1024/1024)
                .setDisk(totalDiskSize /1024/1024/1024)
                .setIp(Objects.requireNonNull(this.findNetWorkInterface(hardware)).getIPv4addr()[0]);
    }
    public RuntimeDetail monitorRuntimeDetail(){
        double statisticTime = 0.5;
        HardwareAbstractionLayer hardware = info.getHardware();
        NetworkIF netWorkInterface = Objects.requireNonNull(this.findNetWorkInterface(hardware));
        CentralProcessor processor = hardware.getProcessor();
        double upload = netWorkInterface.getBytesSent();
        double download = netWorkInterface.getBytesRecv();
        double read = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
        double write = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();
        long[] systemCpuLoadTicks = processor.getSystemCpuLoadTicks();
        netWorkInterface = Objects.requireNonNull(this.findNetWorkInterface(hardware));
        try {
            Thread.sleep((long) (statisticTime*1000));
            upload = (netWorkInterface.getBytesSent()-upload) / statisticTime;
            download = (netWorkInterface.getBytesRecv()-download) / statisticTime;
            read = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum() - read)/statisticTime;
            write = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum() - write)/statisticTime;
            double memory = (double) (hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) /1024/1024/1024;
            double dist = (double) Arrays.stream(File.listRoots()).mapToLong(file -> file.getTotalSpace() - file.getFreeSpace()).sum() /1024/1024/1024;
            return new RuntimeDetail()
                    .setCpuUsage(this.calculateCpuUsage(processor,systemCpuLoadTicks))
                    .setMemoryUsage(memory)
                    .setDisUsage(dist)
                    .setNetworkUpload(upload/1024)
                    .setNetworkDownload(download/1024)
                    .setDiskRead(read/1024/1024)
                    .setDiskWrite(write/1024/1024)
                    .setTimestamp(new Date().getTime());
        } catch (InterruptedException e) {
            log.warn("读取运行时数据出现问题");
            return null;
        }
    }
    private double calculateCpuUsage(CentralProcessor processor, long[] prevTicks) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long cUser = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = cUser + nice + cSys + idle + ioWait + irq + softIrq + steal;
        return (cSys + cUser) * 1.0 / totalCpu;
    }

    private NetworkIF findNetWorkInterface(HardwareAbstractionLayer hardware){
        for (NetworkIF networkIF : hardware.getNetworkIFs()) {
            String[] ipv4 = networkIF.getIPv4addr();
            NetworkInterface networkInterface = networkIF.queryNetworkInterface();
            try {
                if(!networkInterface.isLoopback() && !networkInterface.isPointToPoint()
                        && networkInterface.isUp() && !networkInterface.isVirtual()
                        && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("en") || networkInterface.getName().startsWith("wlan"))
                        && ipv4.length>0 && !networkInterface.getDisplayName().startsWith("VMware")){
                    return networkIF;
                }
            } catch (SocketException e) {
                log.error("读取网络信息接口出错");
                return null;
            } catch (NullPointerException e) {
                log.error("读取网卡出现出错");
                return null;
            }
        }
        return null;
    }

}
