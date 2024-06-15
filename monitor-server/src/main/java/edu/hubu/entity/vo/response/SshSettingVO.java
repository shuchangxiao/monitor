package edu.hubu.entity.vo.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class SshSettingVO {
    int id;
    int port;
    @NotNull @Length(min = 1)
    String username;
    @NotNull @Length(min = 1)
    String password;
    String ip;
}
