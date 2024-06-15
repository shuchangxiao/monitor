package edu.hubu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.entity.dto.ClientSsh;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientSshMapper extends BaseMapper<ClientSsh> {
    // 这里可以根据具体业务需求添加自定义的SQL方法
}