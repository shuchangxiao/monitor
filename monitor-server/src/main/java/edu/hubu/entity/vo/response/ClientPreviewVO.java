package edu.hubu.entity.vo.response;

import lombok.Data;

@Data
public class ClientPreviewVO {
    int id;
    boolean online;
    String location;
    String name;
    String osName;
    String osVersion;
    String ip;
    String cpuName;
    int cpuCore;
    double memory;
    double cpuUsage;
    double memoryUsage;
    double networkUpload;
    double networkDownload;
}
