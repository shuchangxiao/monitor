package edu.hubu.controller.exception;

import edu.hubu.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ValidationController {
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validateException(ValidationException validationException){
        // 打印警告日志，输出异常类名和异常信息
        log.warn("Revolve [{}:{}]",validationException.getClass().getName(),validationException.getMessage());
        // 返回请求参数有误的失败响应
        return RestBean.failure(400,"请求参数有误");
    }
}
