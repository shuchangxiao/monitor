package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@Slf4j
@RestController
public class ObjectController {
    @Resource
    ImageService imageService;
    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type","image/png");
        this.fetchImage(request,response);
    }
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream outputStream = response.getOutputStream();
        if(imagePath.length()<= 13){
            outputStream.println(RestBean.failure(404,"404 not found").toString());
            response.setStatus(404);
        }
        try {
            imageService.fetchImageFromMinio(outputStream,imagePath);
            response.setHeader("Cache-Control","max-age=2592000");
        }catch (ErrorResponseException e){
            if(e.response().code() == 404){
                response.setStatus(404);
                outputStream.println(RestBean.failure(404,"404 not found").toString());
            }else {
                log.error("从Minio获取图片出现异常");
            }
        }

    }
}
