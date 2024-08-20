package edu.hubu.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "runtime")
public class RuntimeData {
    @Column(tag = true)
    int clientId;                    //主机id
    @Column(timestamp = true)
    private Instant timestamp;       //时间戳
    @Column
    private double cpuUsage;         //CPU使用率
    @Column
    private double memoryUsage;      //内存使用量
    @Column
    private double disUsage;         //磁盘使用情况
    @Column
    private double networkUpload;    //网络上传速度
    @Column
    private double networkDownload;  //网络下载速度
    @Column
    private double diskRead;         //磁盘读速度
    @Column
    private double diskWrite;        //磁盘写速度
}
