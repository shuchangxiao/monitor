package edu.hubu.service.Imp;

import edu.hubu.entity.dto.AccountDto;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;


public class MyUserDetail implements UserDetails {
    @Setter
    private AccountDto accountDto;
    private List<GrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return accountDto.getPassword();
    }

    @Override
    public String getUsername() {
        return accountDto.getUsername();
    }
    public Integer getId() {
        return accountDto.getId();
    }

    public String getEmail() {
        return accountDto.getEmail();
    }

    public String getRole() {
        return accountDto.getRole();
    }

    public Date getRegisterTime() {
        return accountDto.getRegisterTime();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object obj) {
        //会话并发生效，使用username判断是否是同一个用户

        if (obj instanceof MyUserDetail){
            //字符串的equals方法是已经重写过的
            return ((MyUserDetail) obj).getUsername().equals(this.accountDto.getUsername());
        }else {
            return false;
        }
    }

}
