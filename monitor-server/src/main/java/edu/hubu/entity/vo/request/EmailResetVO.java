package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
@Data
public class EmailResetVO {
    @Email
    String email;
    @Length(max = 6,min = 6)
    String code;
    @Length(min = 6,max = 16)
    String password;

}
