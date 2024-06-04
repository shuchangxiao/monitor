package edu.hubu.entity.vo.response;

import lombok.Data;

@Data
public class ClientDetailsVO {
    int id;
    String name;
    boolean online;
    String node;
    String location;
    String osName;
    String osVersion;
    String ip;
    String cpuName;
    int cpuCore;
    double disk;
    double memory;
}
