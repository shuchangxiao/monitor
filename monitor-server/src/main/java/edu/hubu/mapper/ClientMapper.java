package edu.hubu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.entity.dto.Client;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper extends BaseMapper<Client> {
    // 这里可以根据具体业务需求添加自定义的SQL方法
}