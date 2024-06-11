package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.request.CreateSubAccountVO;
import edu.hubu.entity.vo.request.SubAccountVO;
import edu.hubu.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Resource
    AccountService accountService;
    @PostMapping("/sub/create")
    public RestBean<Void> createSubAccount(@RequestBody @Valid CreateSubAccountVO vo){
        accountService.createSubAccount(vo);
        return RestBean.success();
    }
    @GetMapping("/sub/delete")
    public RestBean<Void> deleteSubAccount(@RequestParam int id){
        accountService.deleteSubAccount(id);
        return RestBean.success();
    }
    @GetMapping("/sub/list")
    public RestBean<List<SubAccountVO>> subAccountList(){
        return RestBean.success(accountService.listSubAccount());
    }
}
