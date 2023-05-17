package com.express.user.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.express.user.service.PhotoService;
import com.express.user.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
public class PhotoController {

    @Autowired
    PhotoService photoService;

    @Autowired
    RedisService redisService;

    @PostMapping("/upload/{uid}")
    public String upload(@RequestBody MultipartFile file, @PathVariable String uid) throws IOException {
        try{
            String dir = SpringUtil.getBean("filePath");  //存放目录
            java.io.File path = new java.io.File(dir);  //确认路径存在
            if (!path.exists()) {
                path.mkdirs();
            }
            String photoName = file.getOriginalFilename();
            String pPath =  photoService.repeatedNaming(photoName,photoService.getIncrement());
            String photoPath = dir + pPath;
            String photoType = photoService.getSuffix(photoName);
            file.transferTo(new java.io.File(photoPath));
            photoService.insert(uid,photoName,photoPath,photoType);
            redisService.addUrlToCache(uid,"photo/"+pPath); // 写入键值对
            return uid;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    //文件下载
    @GetMapping("/toDownload/{fileName}/{fileId}")
    public ResponseEntity<byte[]> toDownload(@PathVariable("fileName")String fileName, @PathVariable("fileId")int fileId, HttpServletRequest request) {
        return photoService.downloadFile(request, fileName, fileId);
    }


}
