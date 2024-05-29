package edu.hubu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.entity.dto.TopicComment;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopicCommentMapper extends BaseMapper<TopicComment> {
}
