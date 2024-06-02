package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.request.ClientDetailVO;
import edu.hubu.entity.vo.request.RuntimeDetailVO;
import edu.hubu.service.ClientService;
import edu.hubu.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/detail")
    public RestBean<Void> updateClientDetails(@RequestAttribute(Const.ATTR_Client) int id, @RequestBody @Valid ClientDetailVO vo){
        clientService.updateClientDetails(id,vo);
        return RestBean.success();
    }
    @PostMapping("/runtime")
    public RestBean<Void> updateRuntimeDetails(@RequestAttribute(Const.ATTR_Client)int id,  @RequestBody @Valid RuntimeDetailVO vo){
        clientService.runtimeUpdateDetail(id,vo);
        return RestBean.success();
    }
}
