package edu.hubu.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    MailSender sender;
    @Value("${spring.mail.username}")
    String username;
    @RabbitHandler
    public void sendMailMessage(Map<String,Object> data){
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register" -> createMessage("欢迎注册我们的网站","您的邮箱注册码为："+code+",有效时间3分钟",email);
            case "reset" -> createMessage("你的密码重置邮件","你好，您验证码为："+code+",有效时间3分钟",email);
            case "modify" -> createMessage("您的账号正在重置邮箱地址，","您验证码为："+code+",有效时间3分钟"+"如非本人操作请忽略",email);
            default -> null;
        };
        if (message == null) return;
        sender.send(message);
    }
    private SimpleMailMessage createMessage(String title,String content,String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
