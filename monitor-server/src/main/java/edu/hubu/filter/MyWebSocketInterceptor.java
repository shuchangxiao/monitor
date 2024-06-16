package edu.hubu.filter;

import com.alibaba.fastjson2.JSONArray;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.service.AccountService;
import edu.hubu.utils.Const;
import edu.hubu.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
@Slf4j
@Component
public class MyWebSocketInterceptor implements HandshakeInterceptor {
    @Resource
    AccountService accountService;
    @Resource
    JwtUtils jwtUtils;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String requestURI = servletRequest.getRequestURI();
        String[] split = requestURI.split("/");
        String string = split[split.length - 1];
        int clintId = Integer.parseInt(string.substring(0,6));
        String auth = string.substring(6);
        if (!auth.isEmpty()) {
            // 只使用第一个协议
            DecodedJWT jwt = jwtUtils.resolveJwtNoBear(auth);
            if(jwt != null){
                UserDetails userDetails = jwtUtils.toUser(jwt);
                int uid = jwtUtils.toInt(jwt);
                String role = new ArrayList<>(userDetails.getAuthorities()).get(0).getAuthority();
                boolean permissionCheck = this.permissionCheck(uid, role, clintId);
                log.info(permissionCheck+"uid:"+uid+"role: "+role+"clientId:"+clintId);
                return permissionCheck;
            }
            return false;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
    private List<Integer> accountAccessClients(int uid){
        AccountDto accountDto = accountService.getById(uid);
        if (accountDto==null) return null;
        return JSONArray.parseArray(accountDto.getClients()).toList(Integer.class);
    }
    private boolean isAdminAccount(String role){
        // 前面有 ROLE_ 5个字符
        role = role.substring(5);
        return Const.ROLE_ADMIN.equals(role);
    }
    public boolean permissionCheck(int uid,String role,int clientId){
        if(this.isAdminAccount(role)) return true;
        return Objects.requireNonNull(this.accountAccessClients(uid)).contains(clientId);
    }

}
