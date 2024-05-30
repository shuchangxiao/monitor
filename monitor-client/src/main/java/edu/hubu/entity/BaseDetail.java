package edu.hubu.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseDetail {
    String orArch;
    String name;
    String osVersion;
    int osBit;
    String CPUName;
    int CPUCore;
    double memory;
    double disk;
    String IP;
}
