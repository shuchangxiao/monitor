package edu.hubu.controller;

import edu.hubu.entity.RestBean;
import edu.hubu.service.ImageService;
import edu.hubu.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Resource
    ImageService imageService;
    @PostMapping("/cache")
    public RestBean<String> uploadImage(@RequestParam("file")MultipartFile file, @RequestAttribute(Const.ATTR_USER_ID) int id, HttpServletResponse response) throws IOException {
        if(file.getSize() > 1024*1024*5) return RestBean.failure(400,"头像图片不能大于5Mb");
        log.info("正在进行图片上传操作...");
        String url = imageService.uploadImage(file,id);
        if (url != null) {
            log.info("图像上传成功，大小"+file.getSize());
            return RestBean.success(url);
        }else {
            response.setStatus(400);
            return RestBean.failure(400, "图片上传失败，请联系管理员");
        }
    }
    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file")MultipartFile file,@RequestAttribute(Const.ATTR_USER_ID) int id) throws IOException {
        if(file.getSize() > 1024*100) return RestBean.failure(400,"头像图片不能大于100kb");
        log.info("正在上传头像...");
        String url = imageService.uploadAvatar(file,id);
        if (url != null) {
            log.info("图像上传成功"+file.getSize());
            return RestBean.success(url);
        }else return RestBean.failure(400,"头像上传失败，请联系管理员");

    }
}
