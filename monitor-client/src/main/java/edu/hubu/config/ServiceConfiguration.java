package edu.hubu.config;


import com.alibaba.fastjson2.JSONObject;
import edu.hubu.entity.BaseDetail;
import edu.hubu.entity.ConnectConfig;
import edu.hubu.utils.MonitorUtils;
import edu.hubu.utils.NetUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@Configuration
public class ServiceConfiguration implements ApplicationRunner {
    @Resource
    NetUtils netUtils;
    @Resource
    MonitorUtils monitorUtils;
    @Bean
    ConnectConfig connectConfig(){
        log.info("正在加载服务端连接配置...");
        ConnectConfig connectConfig = this.readConfigurationFromFile();
        if(connectConfig == null){
            return registerToServer();
        }
        return connectConfig;
    }
    private void saveAsFile(ConnectConfig connectConfig){
        File dir = new File("config");
        if (!dir.exists() && dir.mkdir()) {
            log.info("正在创建用于保存基本信息的目录...");
        }
        File file = new File("config/service.json");
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(JSONObject.toJSONString(connectConfig));
        } catch (IOException e) {
            log.info("写入文件时出现错误");
        }
        log.info("服务端配置文件写入成功");
    }
    private ConnectConfig registerToServer(){
        Scanner scanner = new Scanner(System.in);
        String token,address;
        do {
            log.info("请输入需要注册的服务端访问地址，地址类似于 'http://192.168.0.22:8080' 这种写法");
            address = scanner.nextLine();
            log.info("请输入服务端生成的用于注册客户端的Token密钥：");
            token = scanner.nextLine();
        }while (!netUtils.registerToService(address,token));
        ConnectConfig connectConfig = new ConnectConfig(address,token);
        this.saveAsFile(connectConfig);
        return connectConfig;
    }
    private ConnectConfig readConfigurationFromFile(){
        File configurationFile = new  File("config/service.json");
        if(configurationFile.exists()){
            try(FileInputStream fileInputStream = new FileInputStream(configurationFile)) {
                String raw = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
                return JSONObject.parseObject(raw).to(ConnectConfig.class);
            } catch (IOException e) {
                log.error("文件读取失败！！！");
                return null;
            }
        }
        return null;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("正在向服务端更新基本信息...");
        BaseDetail baseDetail = monitorUtils.monitorBaseDetail();
        netUtils.updateBaseDetails(baseDetail);
    }
}
