package edu.hubu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.entity.dto.StoreImage;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ImageService extends IService<StoreImage> {
    String uploadAvatar(MultipartFile file,int id) throws IOException;
    void fetchImageFromMinio(OutputStream stream, String image) throws Exception;
    String uploadImage(MultipartFile file,int id) throws IOException;
}
