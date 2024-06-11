package edu.hubu.service.Imp;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.aop.Log;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.vo.request.*;
import edu.hubu.mapper.AccountMapper;
import edu.hubu.service.AccountService;
import edu.hubu.utils.Const;
import edu.hubu.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImp extends ServiceImpl<AccountMapper, AccountDto> implements AccountService, InitializingBean {
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FlowUtils flowUtils;

    @Resource
    BCryptPasswordEncoder encoder;

    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String key = Const.VERIFY_EMAIL_DATA + vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    @Override
    public String changePassword(int id,ChangePasswordVO vo) {
        AccountDto byId = this.getById(id);
        if (!encoder.matches(vo.getPassword(),byId.getPassword())) {
            return "密码错误";
        }
        this.update(Wrappers.<AccountDto>update()
                .eq("id",id)
                .set("password",encoder.encode(vo.getNewPassword())));
        return null;
    }

    @Override
    public void createSubAccount(CreateSubAccountVO vo) {
        AccountDto accountByNameOrEmail = this.findAccountByNameOrEmail(vo.getEmail());
        if( accountByNameOrEmail != null)
            throw new IllegalArgumentException("该电子邮件已被注册");
        accountByNameOrEmail = this.findAccountByNameOrEmail(vo.getUsername());
        if( accountByNameOrEmail != null)
            throw new IllegalArgumentException("该用户名已被注册");
        AccountDto accountDto = new AccountDto(null,
                vo.getUsername(),encoder.encode(vo.getPassword()),
                vo.getEmail(),Const.ROLE_NORMAL,new Date(),
                JSONArray.copyOf(vo.getClients()).toJSONString());
        this.save(accountDto);
    }

    @Override
    public void deleteSubAccount(int id) {
        this.removeById(id);
    }

    @Override
    public List<SubAccountVO> listSubAccount() {
         return this.list(Wrappers.<AccountDto>query()
                .eq("role", Const.ROLE_NORMAL))
                 .stream()
                 .map(item-> {
                     SubAccountVO viewObject = item.asViewObject(SubAccountVO.class);
                     viewObject.setClientList(JSONArray.parse(item.getClients()));
                     return viewObject;
                 })
                 .toList();

    }

    @Override
    public String resetPassword(EmailResetVO vo) {
        String verify = this.resetConfirm(new ConfirmResetVO(vo.getEmail(),vo.getCode()));
        if(verify !=null) return verify;
        String password = encoder.encode(vo.getPassword());
        boolean update = this.update().eq("email", vo.getEmail()).set("password", password).update();
        if(update){
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + vo.getEmail());
        }
        return null;
    }


    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()){
            if(!this.verifyLimit(ip)) return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999)+100000;
            Map<String, Object> data = Map.of("type",type,"email",email,"code",code);
            amqpTemplate.convertAndSend("mail",data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email,String.valueOf(code),5, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return UserDetails 用户信息
     * @throws UsernameNotFoundException 用户名不存在时抛出异常
     */
    @Override
    @Log(username = "username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDto accountDto = this.findAccountByNameOrEmail(username);
        if(accountDto ==null)
            throw new UsernameNotFoundException("用户名或密码错误");

        // 新写法
        MyUserDetail myUserDetail = new MyUserDetail();
        myUserDetail.setAccountDto(accountDto);
        myUserDetail.setAuthorities(accountDto.getRole());
        return myUserDetail;
    }


    @Override
    public AccountDto findAccountByNameOrEmail(String text){
        return  this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();

    }


    private boolean verifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key,60);
    }

    @Override
    public void afterPropertiesSet(){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }
}
