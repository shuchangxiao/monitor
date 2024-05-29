package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.dto.AccountDetail;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.vo.request.ChangePasswordVO;
import edu.hubu.entity.vo.request.DetailSaveVO;
import edu.hubu.entity.vo.request.ModifyEmailVO;
import edu.hubu.entity.vo.request.PrivacySaveVO;
import edu.hubu.entity.vo.response.AccountDetailVO;
import edu.hubu.entity.vo.response.AccountPrivacyVO;
import edu.hubu.entity.vo.response.AccountVO;
import edu.hubu.service.AccountDetailService;
import edu.hubu.service.AccountPrivacyService;
import edu.hubu.service.AccountService;
import edu.hubu.utils.Const;
import edu.hubu.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailService accountDetailService;
    @Resource
    AccountPrivacyService accountPrivacyService;
    @Resource
    ControllerUtils controllerUtils;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDto accountById = accountService.findAccountById(id);
        AccountVO viewObject = accountById.asViewObject(AccountVO.class);
        return RestBean.success(viewObject);
    }

    @GetMapping("/detail")
    public RestBean<AccountDetailVO> detail(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        AccountDetail accountDetail = Optional
                .ofNullable(accountDetailService.findAccountDetailById(id))
                .orElseGet(AccountDetail::new);
        return RestBean.success(accountDetail.asViewObject(AccountDetailVO.class));
    }

    @PostMapping("/save-detail")
    public RestBean<Void> saveDetail(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid DetailSaveVO vo) {
        boolean success = accountDetailService.saveAccountDetail(id, vo);
        return success ? RestBean.success(null) : RestBean.failure(400, "用户名已被注册");
    }

    @PostMapping("/modify")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid ModifyEmailVO vo) {
        String result = accountService.modifyEmail(id, vo);
        return result == null ? RestBean.success() : RestBean.failure(400, result);
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid ChangePasswordVO vo) {
        return controllerUtils.messageHandle(()->accountService.changePassword(id,vo));
    }
    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id, @RequestBody @Valid PrivacySaveVO vo){
        accountPrivacyService.savePrivacy(id,vo);
        return RestBean.success();
    }
    @GetMapping("/privacy")
    public RestBean<AccountPrivacyVO> privacy(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(accountPrivacyService.accountPrivacy(id).asViewObject(AccountPrivacyVO.class));
    }


}
