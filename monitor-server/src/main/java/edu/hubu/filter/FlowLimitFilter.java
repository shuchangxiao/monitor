package edu.hubu.filter;

import edu.hubu.entity.RestBean;
import edu.hubu.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String addr = request.getRemoteAddr();
        if(this.tryCount(addr)){
            super.doFilter(request,response,chain);
        }else {
            this.writerBlockMessage(response);
        }
    }
    private void writerBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden("操作频繁，请稍后再试").toString());
    }
    private boolean tryCount(String ip){
        synchronized (ip.intern()){
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip))){
                return false;
            }
            return this.limitPeriodCheck(ip);
        }
    }
    private boolean limitPeriodCheck(String ip){
        String key = Const.FLOW_LIMIT_COUNT + ip;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            Long increment = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(key)).orElse(0L);
            if(increment >=100){
                stringRedisTemplate.opsForValue().set(key,"",3,TimeUnit.SECONDS);
                return false;
            }
        }else {
            stringRedisTemplate.opsForValue().set(key,"1",3, TimeUnit.SECONDS);
        }
        return true;
    }
}































