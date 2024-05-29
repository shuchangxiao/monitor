package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.aop.Log;
import edu.hubu.entity.dto.AccountDetail;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.dto.AccountPrivacy;
import edu.hubu.entity.vo.request.*;
import edu.hubu.mapper.AccountDetailMapper;
import edu.hubu.mapper.AccountMapper;
import edu.hubu.mapper.AccountPrivacyMapper;
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
    AccountDetailMapper accountDetailMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
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
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String code =getEmailVerifyCode(email);
        if (code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码输入错误，请重新输入";
        if(this.existsAccountByEmail(email)) return "此电子邮箱已经被注册";
        if(this.existsAccountByUsername(vo.getUsername())) return "此用户名已经被注册";
        String password = encoder.encode(vo.getPassword());
        AccountDto accountDto = new AccountDto(null,vo.getUsername(),password, vo.getEmail(), "user",new Date(),null);
        if(this.save(accountDto)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
            accountPrivacyMapper.insert(new AccountPrivacy(accountDto.getId()));
            AccountDetail accountDetail = new AccountDetail();
            accountDetail.setId(accountDto.getId());
            accountDetailMapper.insert(accountDetail);
            return null;
        }
        else return "内部出现错误，请联系管理员";

    }

    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<AccountDto>query().eq("email",email));
    }
    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<AccountDto>query().eq("username",username));
    }
    private String getEmailVerifyCode(String email){
        return stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
    }
    private void  deleteEmailVerify(String email){
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
    }

    @Override
    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id",id).one().getPassword();
        if (!encoder.matches(vo.getPassword(),password)){
            return "原密码错误，请重新输入";
        }
        boolean success = this.update().eq("id",id).set("password",encoder.encode(vo.getNew_password())).update();
        if (!success) return "出现错误，请联系管理员";
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
//        return User
//                .withUsername(username)
//                .password(accountDto.getPassword())
//                .roles(accountDto.getRole())
//                .build();
        // 新写法
        MyUserDetail myUserDetail = new MyUserDetail();
        myUserDetail.setAccountDto(accountDto);
        return myUserDetail;
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String code =getEmailVerifyCode(vo.getEmail());
        if (code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码输入错误，请重新输入";
        this.deleteEmailVerify(vo.getEmail());
        AccountDto accountDto = this.findAccountByNameOrEmail(vo.getEmail());
        if(accountDto !=null && accountDto.getId() != id){
            return "该电子邮件已经被注册,无法完成此操作";
        }
        this.update().eq("id",id).set("email",vo.getEmail()).update();
        return null;
    }

    @Override
    public AccountDto findAccountByNameOrEmail(String text){
        return  this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();

    }

    @Override
    public AccountDto findAccountById(int id) {
        return this.query().eq("id",id).one();
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
