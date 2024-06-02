package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RuntimeDetailVO {
    @NotNull
    private long timestamp;
    @NotNull
    private double cpuUsage;
    @NotNull
    private double memoryUsage;
    @NotNull
    private double disUsage;
    @NotNull
    private double networkUpload;
    @NotNull
    private double networkDownload;
    @NotNull
    private double diskRead;
    @NotNull
    private double diskWrite;
}