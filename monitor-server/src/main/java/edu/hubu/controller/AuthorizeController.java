package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.request.ChangeEmailVO;
import edu.hubu.entity.vo.request.ChangePasswordVO;
import edu.hubu.entity.vo.request.EmailResetVO;
import edu.hubu.service.AccountService;
import edu.hubu.utils.Const;
import edu.hubu.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {
    @Resource
    AccountService accountService;
    @Resource
    ControllerUtils controllerUtils;
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam String email,
                                        @RequestParam @Pattern(regexp ="(reset|modify)") String type, HttpServletRequest request){
        if(Const.isValidEmail(email)){
            return controllerUtils.messageHandle(
                    () -> accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr()));

        }
        return RestBean.failure(400,"请求参数有误");
    }

    @PostMapping("/reset-password")
    public RestBean<Void> restPassword(@RequestBody EmailResetVO vo){
        return this.messageHandle(vo,accountService::resetPassword);
    }
    private <T> RestBean<Void> messageHandle(T vo, Function<T,String> function){
        return controllerUtils.messageHandle(()->function.apply(vo));
    }
    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO vo, @RequestAttribute(Const.ATTR_USER_ID) int id){
        return controllerUtils.messageHandle(()->accountService.changePassword(id,vo));
    }
    @PostMapping("/change-email")
    public RestBean<Void> changePassword(@RequestBody @Valid ChangeEmailVO vo, @RequestAttribute(Const.ATTR_USER_ID) int id){
        return controllerUtils.messageHandle(()->accountService.changeEmail(id,vo));
    }
}
