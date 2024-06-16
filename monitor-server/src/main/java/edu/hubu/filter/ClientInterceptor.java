//package edu.hubu.filter;
//
//import com.alibaba.fastjson2.JSONArray;
//import edu.hubu.annotation.HasPermission;
//import edu.hubu.annotation.IsAdmin;
//import edu.hubu.entity.dto.AccountDto;
//import edu.hubu.service.AccountService;
//import edu.hubu.utils.Const;
//import jakarta.annotation.Resource;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.HandlerMethod;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//public class ClientInterceptor implements HandlerInterceptor{
//    @Resource
//    AccountService accountService;
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 如果不是映射到方法直接通过
//        if (!(handler instanceof HandlerMethod handlerMethod)) {
//            return true;
//        }
//        // ①:START 方法注解级拦截器
//        Method method = handlerMethod.getMethod();
//        // 判断接口是否需要登录
//        IsAdmin admin = method.getAnnotation(IsAdmin.class);
//        HasPermission permission = method.getAnnotation(HasPermission.class);
//        // 有 @LoginRequired 注解，需要认证
//        if (admin != null) {
//            // 这写你拦截需要干的事儿，比如取缓存，SESSION，权限判断等
//            String role = (String) request.getAttribute(Const.Attr_USER_ROLE);
//            return isAdminAccount(role);
//        }
//        if (permission != null){
//            String role = (String) request.getAttribute(Const.Attr_USER_ROLE);
//            int uid = (int) request.getAttribute(Const.ATTR_USER_ID);
//            String requestURI = request.getRequestURI();
//            return permissionCheck(uid,role,1);
//        }
//        return false;
//    }
//    private boolean isAdminAccount(String role){
//        // 前面有 ROLE_ 5个字符
//        role = role.substring(5);
//        return Const.ROLE_ADMIN.equals(role);
//    }
//    public boolean permissionCheck(int uid,String role,int clientId){
//        if(this.isAdminAccount(role)) return true;
//        return Objects.requireNonNull(this.accountAccessClients(uid)).contains(clientId);
//    }
//    private List<Integer> accountAccessClients(int uid){
//        AccountDto accountDto = accountService.getById(uid);
//        if (accountDto==null) return null;
//        return JSONArray.parseArray(accountDto.getClients()).toList(Integer.class);
//    }
//}
