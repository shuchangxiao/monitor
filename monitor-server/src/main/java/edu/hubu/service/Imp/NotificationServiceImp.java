package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.entity.dto.Notification;
import edu.hubu.entity.vo.response.NotificationVO;
import edu.hubu.mapper.NotificationMapper;
import edu.hubu.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImp extends ServiceImpl<NotificationMapper,Notification> implements NotificationService {
    @Override
    public List<NotificationVO> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid",uid))
                .stream()
                .map(vo -> vo.asViewObject(NotificationVO.class))
                .toList();
    }

    @Override
    public void deleteUserNotification(int id, int uid) {
        this.remove(Wrappers.<Notification>query().eq("id",id).eq("uid",uid));
    }

    @Override
    public void deleteAllUserNotification(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid",uid));
    }

    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title); // 设置标题
        notification.setContent(content); // 设置内容
        notification.setType(type); // 设置类型
        notification.setUrl(url);
        this.save(notification);
    }
}
