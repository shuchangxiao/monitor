package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.entity.dto.AccountDetail;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.vo.request.DetailSaveVO;
import edu.hubu.mapper.AccountDetailMapper;
import edu.hubu.service.AccountDetailService;
import edu.hubu.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailServiceImp extends ServiceImpl<AccountDetailMapper, AccountDetail> implements AccountDetailService {
    @Resource
    AccountService accountService;
    @Override
    public AccountDetail findAccountDetailById(int id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public synchronized boolean saveAccountDetail(int id, DetailSaveVO vo) {
        AccountDto accountDto = accountService.findAccountByNameOrEmail(vo.getUsername());
        if(accountDto == null || accountDto.getId() == id){
            accountService.update()
                    .eq("id",id)
                    .set("username",vo.getUsername())
                    .update();
            this.saveOrUpdate(new AccountDetail
                    (id,vo.getGender(),vo.getPhone(),vo.getQq(),vo.getWechat(),vo.getDesc()));
            return true;
        }
        return false;
    }
}
