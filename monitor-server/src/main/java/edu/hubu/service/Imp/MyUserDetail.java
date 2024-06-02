package edu.hubu.service.Imp;

import edu.hubu.entity.dto.AccountDto;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.ArrayList;
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

    public void setAuthorities(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList(roles.length);
        String[] var3 = roles;
        int var4 = roles.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String role = var3[var5];
            Assert.isTrue(!role.startsWith("ROLE_"), () -> {
                return role + " cannot start with ROLE_ (it is automatically added)";
            });
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        this.authorities = authorities;
    }
}
