package edu.hubu.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONArray;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.service.AccountService;

import java.util.List;
import java.util.Objects;

/**
 * @Package edu.hubu.utils
 * @Author 舒聪聪
 * @Date 2024/8/19 16:05
 * @description: 用于判断十分对主机有权限
 */
public class AccountAccessClientsUtils {
    private static AccountService accountService;

    static {
        AccountAccessClientsUtils.accountService = SpringUtil.getBean(AccountService.class);
    }
    private static List<Integer> accountAccessClients(int uid){
        AccountDto accountDto = accountService.getById(uid);
        if (accountDto==null) return null;
        return JSONArray.parseArray(accountDto.getClients()).toList(Integer.class);
    }
    private static boolean isAdminAccount(String role){
        // 前面有 ROLE_ 5个字符
        role = role.substring(5);
        return Const.ROLE_ADMIN.equals(role);
    }
    public static boolean permissionCheck(int uid,String role,int clientId){
        if(AccountAccessClientsUtils.isAdminAccount(role)) return true;
        return Objects.requireNonNull(AccountAccessClientsUtils.accountAccessClients(uid)).contains(clientId);
    }
}
