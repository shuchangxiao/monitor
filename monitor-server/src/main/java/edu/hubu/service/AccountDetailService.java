package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.AccountDetail;
import edu.hubu.entity.vo.request.DetailSaveVO;

public interface AccountDetailService extends IService<AccountDetail> {
    AccountDetail findAccountDetailById(int id);
    boolean saveAccountDetail(int id, DetailSaveVO vo);
}
