package edu.hubu.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import edu.hubu.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@TableName("db_account_detail")
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetail implements BaseData {
    @TableId
    Integer id;
    int gender;
    String phone;
    String qq;
    String wechat;
    @TableField("`desc`")
    String desc;
}
