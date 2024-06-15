package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
public class ChangeEmailVO {
    @Email
    String now_email;
    @Email
    String email;
    @Length(min = 6,max = 6)
    String code;
}
