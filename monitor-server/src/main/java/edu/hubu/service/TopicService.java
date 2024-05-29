package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.Interact;
import edu.hubu.entity.dto.Topic;
import edu.hubu.entity.dto.TopicType;
import edu.hubu.entity.vo.request.AddCommentVO;
import edu.hubu.entity.vo.request.TopicCreateVO;
import edu.hubu.entity.vo.request.TopicUpdateVO;
import edu.hubu.entity.vo.response.CommentVO;
import edu.hubu.entity.vo.response.TopicDetailVO;
import edu.hubu.entity.vo.response.TopicPreviewVO;
import edu.hubu.entity.vo.response.TopicTopVO;

import java.util.List;

public interface TopicService extends IService<Topic> {
    List<TopicType> topicType();
    String createTopic(int uid, TopicCreateVO vo);
    List<TopicPreviewVO> listTopicPreview(int page,int type);
    List<TopicTopVO> listTopTopics();
    TopicDetailVO getTopic(int tid,int uid);
    void interact(Interact interact,boolean state);
    List<TopicPreviewVO> listTopicCollects(int uid);
    String updateTopic(int uid, TopicUpdateVO vo);
    String createComment(AddCommentVO vo,int uid);
    List<CommentVO> comments(int tid,int pageNum);
    void deleteComment(int id,int uid);
}
