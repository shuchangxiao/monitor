package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
@AllArgsConstructor
@Data
public class ConfirmResetVO {
    @Email
    String email;
    @Length(max = 6,min = 6)
    String code;
}
