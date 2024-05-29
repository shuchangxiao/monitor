package edu.hubu.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@AllArgsConstructor
@Data
@TableName("db_image_store")
public class StoreImage {
    Integer uid;
    String name;
    Date time;
}
