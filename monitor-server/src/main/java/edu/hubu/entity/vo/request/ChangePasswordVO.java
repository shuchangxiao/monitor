package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordVO {
    @NotNull
    private String password;
    @NotNull
    private String newPassword;
}
