package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.Notification;
import edu.hubu.entity.vo.response.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    List<NotificationVO> findUserNotification(int uid);
    void deleteUserNotification(int id,int uid);
    void deleteAllUserNotification(int uid);
    void addNotification(int uid,String title,String content,String type,String url);
}
