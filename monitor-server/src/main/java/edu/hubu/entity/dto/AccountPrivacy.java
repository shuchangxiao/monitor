package edu.hubu.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import edu.hubu.entity.BaseData;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

@Data
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId
    final Integer id;
    boolean phone = true;
    boolean email = true;
    boolean wechat = true;
    boolean qq = true;
    boolean gender = true;

    public String[] hiddenFields() {
        List<String> list = new LinkedList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)) {
                    list.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                return null;
            }
        }
        return list.toArray(String[]::new);
    }
}
