package edu.hubu.entity.vo.response;

import lombok.Data;

@Data
public class AccountPrivacyVO {
    boolean phone;
    boolean email;
    boolean wechat;
    boolean qq;
    boolean gender;

}
