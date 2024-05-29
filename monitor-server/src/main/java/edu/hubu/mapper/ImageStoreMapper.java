package edu.hubu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.entity.dto.StoreImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageStoreMapper extends BaseMapper<StoreImage> {
}
