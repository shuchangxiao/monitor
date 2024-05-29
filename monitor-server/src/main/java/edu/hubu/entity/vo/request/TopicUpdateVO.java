package edu.hubu.entity.vo.request;
import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TopicUpdateVO {
    @Min(0)
    Integer id;
    @Min(1)
    int type;
    @Length(min = 1,max = 30)
    String title;
    JSONObject content;
}
