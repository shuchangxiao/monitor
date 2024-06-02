package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientDetailVO {
    @NotNull
    String osArch;
    @NotNull
    String osName;
    @NotNull
    String osVersion;
    @NotNull
    @Min(0)
    int osBit;
    @NotNull
    String cpuName;
    @NotNull
    @Min(1)
    int cpuCore;
    @NotNull
    @Min(0)
    double memory;
    @NotNull
    @Min(0)
    double disk;
    @NotNull
    String ip;
}
