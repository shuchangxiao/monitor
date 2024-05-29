package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChangePasswordVO {
    @Length(max = 16,min = 6)
    String password;
    @Length(max = 16,min = 6)
    String new_password;
}
