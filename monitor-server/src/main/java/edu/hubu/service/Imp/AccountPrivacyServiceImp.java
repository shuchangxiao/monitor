package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.entity.dto.AccountPrivacy;
import edu.hubu.entity.vo.request.PrivacySaveVO;
import edu.hubu.mapper.AccountPrivacyMapper;
import edu.hubu.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImp extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    @Override
    @Transactional
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id))
                .orElse(new AccountPrivacy(id));
        boolean status = vo.isStatus();
        switch (vo.getType()){
            case "phone"->privacy.setPhone(status);
            case "email"->privacy.setEmail(status);
            case "gender"->privacy.setGender(status);
            case "qq"->privacy.setQq(status);
            case "wechat"->privacy.setWechat(status);
        }
        this.saveOrUpdate(privacy);
    }
    @Override
    public AccountPrivacy accountPrivacy(int id){
        return Optional.ofNullable(this.getById(id))
                .orElse(new AccountPrivacy(id));
    }
}
