package edu.hubu.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

@Data
@Measurement(name = "runtime")
public class RuntimeData {
    @Column(tag = true)
    int clientId;
    @Column(timestamp = true)
    private Instant timestamp;
    @Column
    private double cpuUsage;
    @Column
    private double memoryUsage;
    @Column
    private double disUsage;
    @Column
    private double networkUpload;
    @Column
    private double networkDownload;
    @Column
    private double diskRead;
    @Column
    private double diskWrite;
}
