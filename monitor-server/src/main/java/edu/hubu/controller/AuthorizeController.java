package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.request.ConfirmResetVO;
import edu.hubu.entity.vo.request.EmailRegisterVO;
import edu.hubu.entity.vo.request.EmailResetVO;
import edu.hubu.service.AccountService;
import edu.hubu.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AccountService accountService;
    @Resource
    ControllerUtils controllerUtils;
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp ="(register|reset|modify)") String type, HttpServletRequest request){
        return controllerUtils.messageHandle(
                () -> accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return controllerUtils.messageHandle(
                ()->accountService.registerEmailAccount(vo));
    }
    @PostMapping("/reset-config")
    public RestBean<Void> restConfig(@RequestBody ConfirmResetVO vo){
        return this.messageHandle(vo,accountService::resetConfirm);
    }
    @PostMapping("/reset-password")
    public RestBean<Void> restPassword(@RequestBody EmailResetVO vo){
        return this.messageHandle(vo,accountService::resetPassword);
    }
    private <T> RestBean<Void> messageHandle(T vo, Function<T,String> function){
        return controllerUtils.messageHandle(()->function.apply(vo));
    }

}
