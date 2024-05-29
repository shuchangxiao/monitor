package edu.hubu.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Aspect
public class MyAspect {
    @Pointcut("@annotation(edu.hubu.aop.Log)")
    public void pointCut(){
    }
    @Around("pointCut()")
    public Object execute(ProceedingJoinPoint pjp) throws JsonProcessingException {
        LogData operLog = getOperLog(pjp);
        try {
            // 执行完成后再打印日志
            Object proceed = pjp.proceed();
            operLog.setJsonResult(proceed.toString());
            log.info(new ObjectMapper().writeValueAsString(operLog));
            return proceed;
        } catch (Throwable e) {
            operLog.setErrorMsg(e.getMessage());
            if (e instanceof NullPointerException) {
                operLog.setErrorMsg("java.lang.NullPointerException");
            }
            // 执行异常时打印日志
            log.error(new ObjectMapper().writeValueAsString(operLog));
            throw new RuntimeException(e);
        }

    }
    private LogData getOperLog(ProceedingJoinPoint joinPoint) {
        LogData LogData = new LogData();
        LogData.setDate(new Date());
        // just demo to get current user and os
        Map<String, String> map = System.getenv();
        // 获取注解中的用户名
        // 如果直接将自定义注解写在通知方法里，则获取自定义注解的值如下
        // LogData.setOperator(sysLog.userName());
        MethodSignature sign =  (MethodSignature)joinPoint.getSignature();
        Method method = sign.getMethod();
        Log annotation = method.getAnnotation(Log.class);
        LogData.setOperate(annotation.username());
        // 获取操作系统名
        String os = map.get("OS");
        LogData.setOs(os);
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        List<Object> collect = Arrays.stream(args).filter(Objects::nonNull).toList();
        if (!CollectionUtils.isEmpty(collect)) {
            LogData.setJsonResult(Arrays.toString(args));
        }
        return LogData;
    }
    
}
