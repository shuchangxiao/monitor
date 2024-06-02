package edu.hubu.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import edu.hubu.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_client")
public class Client implements BaseData {
    @TableId
    Integer id;
    String name;
    String token;
    Date registerTime;
    String location;
    String node;
}
