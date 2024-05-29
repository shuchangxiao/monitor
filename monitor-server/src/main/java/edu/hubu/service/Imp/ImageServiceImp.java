package edu.hubu.service.Imp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.entity.dto.AccountDto;
import edu.hubu.entity.dto.StoreImage;
import edu.hubu.entity.vo.response.AccountVO;
import edu.hubu.mapper.AccountMapper;
import edu.hubu.mapper.ImageStoreMapper;
import edu.hubu.service.ImageService;
import edu.hubu.utils.Const;
import edu.hubu.utils.FlowUtils;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
public class ImageServiceImp  extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImageService {
    @Resource
    MinioClient minioClient;
    @Resource
    AccountMapper accountMapper;
    @Resource
    FlowUtils flowUtils;
    final String AVATAR_PARTITION = "/avatar/";
    final String CACHE_PARTITION = "/cache/";
    final String IMAGE_BUCKET = "study";
    @Transactional
    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-","");
        imageName = AVATAR_PARTITION + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(IMAGE_BUCKET)
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();

        try {
            minioClient.putObject(args);
            String avatar = accountMapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
            if(accountMapper.update(null, Wrappers.<AccountDto>update()
                    .eq("id",id).set("avatar",imageName))>0){
                return imageName;
            }else {
                return null;
            }
        } catch (Exception e){
            log.error("图片上传出现问题："+e.getMessage());
            return null;
        }
    }
    @Override
    public void fetchImageFromMinio(OutputStream stream,String image) throws Exception{
    GetObjectArgs args = GetObjectArgs.builder()
                .bucket(IMAGE_BUCKET)
                .object(image)
                .build();
        GetObjectResponse response = minioClient.getObject(args);
        IOUtils.copy(response,stream);
    }

    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if(!flowUtils.limitPeriodCounterCheck(key,20,3600))
            return null;
        String imageName = UUID.randomUUID().toString().replace("-","");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        imageName = CACHE_PARTITION+format.format(date)+"/"+imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(IMAGE_BUCKET)
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try {
            minioClient.putObject(args);
            if(this.save(new StoreImage(id,imageName,date)))
                return imageName;
            else return null;
        } catch (Exception e) {
            log.error("图片上传出现问题："+e.getMessage());
            return null;

        }
    }

    private void deleteOldAvatar(String avatar) throws Exception{
        if(avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(IMAGE_BUCKET)
                .object(avatar)
                .build();
        minioClient.removeObject(args);
    }
}
