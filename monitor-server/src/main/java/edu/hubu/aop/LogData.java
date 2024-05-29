package edu.hubu.aop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class LogData {
    @JsonProperty("操作人员")
    private String operate;
    @JsonProperty("操作地点")
    private String location;
    @JsonProperty("操作时间")
    private Date date;
    @JsonProperty("操作参数")
    private String jsonResult;
    @JsonProperty("操作系统")
    private String os;
    @JsonProperty("错误信息")
    private String errorMsg;

}
