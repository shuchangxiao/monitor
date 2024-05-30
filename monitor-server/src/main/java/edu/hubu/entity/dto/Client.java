package edu.hubu.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@TableName("db_client")
public class Client {
    @TableId
    Integer id;
    String name;
    String token;
    Date registerTime;

}
