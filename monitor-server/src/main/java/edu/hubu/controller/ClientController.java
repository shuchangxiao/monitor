package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.service.ClientService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class ClientController {
    @Resource
    ClientService clientService;
    @GetMapping("/register")
    public RestBean<Void> registerClient(@RequestHeader("Authorization") String token){
        return clientService.verifyAndRegister(token)
                ?RestBean.success()
                :RestBean.failure(401,"注册失败，请检查Token是否正确");
    }
}
