package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.vo.response.NotificationVO;
import edu.hubu.service.NotificationService;
import edu.hubu.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService notificationService;
    @GetMapping("/list")
    public RestBean<List<NotificationVO>> listNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(notificationService.findUserNotification(uid));
    }
    @GetMapping("/delete")
    public RestBean<Void> deleteNotification(@RequestParam @Min(0) int id, @RequestAttribute(Const.ATTR_USER_ID) int uid){
        notificationService.deleteUserNotification(id,uid);
        return RestBean.success();
    }
    @GetMapping("/delete-all")
    public RestBean<Void> deleteAllNotification( @RequestAttribute(Const.ATTR_USER_ID) int uid){
        notificationService.deleteAllUserNotification(uid);
        return RestBean.success();
    }
}
