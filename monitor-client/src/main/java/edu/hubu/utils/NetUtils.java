package edu.hubu.utils;

import com.alibaba.fastjson2.JSONObject;
import edu.hubu.entity.BaseDetail;
import edu.hubu.entity.ConnectConfig;
import edu.hubu.entity.Response;
import edu.hubu.entity.RuntimeDetail;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class NetUtils {
    @Resource
    @Lazy
    ConnectConfig connectConfig;
    private final HttpClient client = HttpClient.newHttpClient();
    public boolean registerToService(String addr,String token){
        log.info("正在服务端注册，请稍后");
        Response response = this.daGet("/register",addr,token);
        if(response.success()){
            log.info("客户端注册成功！");
        }else {
            log.error("客户端注册失败：{}",response.message());
        }
        return response.success();
    }
    private Response doGet(String url){
        return this.daGet(url,connectConfig.getAddress(),connectConfig.getAddress());
    }
    private Response daGet(String url,String addr,String token){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(addr+"/monitor"+url))
                    .header("Authorization",token)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body()).to(Response.class);
        } catch (Exception e) {
            log.error("在发起服务端请求时出现问题",e);
            return Response.errorResponse(e);
        }
    }
    public void updateBaseDetails(BaseDetail detail){
        Response response = this.doPost("/detail", detail);
        if(response.success()){
            log.info("系统基本信息已经更新完成");
        }else {
            log.error("基本信息更新失败：{}",response.message());
        }
    }
    public void updateRuntimeDetails(RuntimeDetail detail){
        Response response = this.doPost("/runtime", detail);
        if(!response.success()){
            log.error("基本信息更新失败：{}",response.message());
        }
    }
    private Response doPost(String url ,Object data){
        String jsonString = JSONObject.from(data).toJSONString();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                    .uri(new URI(connectConfig.getAddress()+"/monitor"+url))
                    .header("Authorization",connectConfig.getToken())
                    .header("Content-Type","application/json")
                    .build();
            HttpResponse<String> send = client.send(request,HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(send.body()).to(Response.class);
        } catch (Exception e) {
            log.error("发送失败请求失败：{}",e.getMessage());
            return Response.errorResponse(e);
        }
    }

}
