package edu.hubu.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class CreateSubAccountVO {
    @Length(max = 10,min = 1)
    String username;
    @Email
    String email;
    @Length(min = 0,max = 20)
    String password;
    @Size(min = 1)
    List<Integer> clients;
}
