package edu.hubu.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import edu.hubu.entity.BaseData;
import lombok.Data;

@Data
@TableName("db_client_ssh")
public class ClientSsh implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer port;
    String username;
    String password;
}
