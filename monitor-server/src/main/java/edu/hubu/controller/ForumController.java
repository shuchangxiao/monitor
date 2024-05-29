package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.entity.dto.Interact;
import edu.hubu.entity.dto.TopicType;
import edu.hubu.entity.vo.request.AddCommentVO;
import edu.hubu.entity.vo.request.TopicCreateVO;
import edu.hubu.entity.vo.request.TopicUpdateVO;
import edu.hubu.entity.vo.response.*;
import edu.hubu.service.TopicService;
import edu.hubu.service.WeatherService;
import edu.hubu.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import edu.hubu.utils.Const;

@RestController
@RequestMapping("api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;
    @Resource
    ControllerUtils controllerUtils;
    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude,double latitude){
        WeatherVO vo = weatherService.fetchWeather(longitude, latitude);
        return vo==null
                ? RestBean.failure(400,"获取地理位置信息失败，请联系管理员")
                : RestBean.success(vo);
    }
    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> topicType(){
        return RestBean.success(topicService.topicType()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }
    @PostMapping("/creat-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo ,@RequestAttribute(Const.ATTR_USER_ID) int id){
        return controllerUtils.messageHandle(()->topicService.createTopic(id,vo));
    }
    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopicPreview(@RequestParam @Min(0) @Max(100) int page, @RequestParam @Min(0) int type){
        return RestBean.success(topicService.listTopicPreview(page,type));
    }
    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVO>> topTopic(){
        return RestBean.success(topicService.listTopTopics());
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailVO> topic(@RequestParam @Min(0) int tid,@RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.getTopic(tid,uid));
    }
    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int id){
        topicService.interact(new Interact(tid,id,new Date(),type),state);
        return RestBean.success();
    }
    @GetMapping("/collect")
    public RestBean<List<TopicPreviewVO>> collect(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(topicService.listTopicCollects(id));
    }
    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@Valid @RequestBody TopicUpdateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id){
        return controllerUtils.messageHandle(()->topicService.updateTopic(id,vo));
    }
    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@Valid @RequestBody AddCommentVO vo, @RequestAttribute(Const.ATTR_USER_ID) int id){
        return controllerUtils.messageHandle(()->topicService.createComment(vo,id));
    }
    @GetMapping("/comments")
    public RestBean<List<CommentVO>> comments(@RequestParam @Min(0) int tid,@RequestParam @Min(0) int page){
        return RestBean.success(topicService.comments(tid,page+1));
    }
    @GetMapping("/delete-comments")
    public RestBean<Void> deleteComment(@RequestParam @Min(0) int id,@RequestAttribute(Const.ATTR_USER_ID) int uid){
        topicService.deleteComment(id,uid);
        return RestBean.success();
    }

}
