package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.vo.request.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AccountService extends IService<AccountDto>, UserDetailsService {
    AccountDto findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type,String email,String ip);

    String resetConfirm(ConfirmResetVO vo);
    String resetPassword(EmailResetVO vo);
    String changePassword(int id,ChangePasswordVO vo);
    String changeEmail(int id,ChangeEmailVO vo);
    void createSubAccount(CreateSubAccountVO vo);
    void deleteSubAccount(int id);
    List<SubAccountVO> listSubAccount();
}
