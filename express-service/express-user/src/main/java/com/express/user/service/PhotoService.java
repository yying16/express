package com.express.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.user.domain.Photo;
import com.express.user.dao.PhotoMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.cn;

@Service
public class PhotoService {
    @Autowired
    PhotoMapper photoMapper;

    /**
     * 获得文件后缀名
     */
    public String getSuffix(String fileName) {
        try {
            if (fileName.lastIndexOf(".") == -1) {//文件没有后缀名的情况
                return " ";
            }
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return " ";
        }
    }

    /**
     * 获得文件前缀名
     */
    public String getPrefix(String fileName) {
        try {
            if (fileName.lastIndexOf(".") == -1) {//文件没有后缀名的情况
                return fileName;
            }
            return fileName.substring(0, fileName.length() - getSuffix(fileName).length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return fileName;
        }
    }

    /**
     * 获取自增id
     */
    public int getIncrement() {
        return photoMapper.selectList(new QueryWrapper<>()).size() + 1;
    }

    /**
     * 解决重名问题
     */
    public String repeatedNaming(String fileName, int fileId) { //解决重复命名问题
        String prefix = getPrefix(fileName);
        String suffix = getSuffix(fileName);
        return prefix + "_" + fileId + "." + suffix;
    }

    /**
     * 添加图片
     */
    public boolean insert(String uid,String photoName, String photoSource, String photoType) {
        try {
            Photo photo = new Photo();
            photo.setPhotoId(uid);
            photo.setPhotoName(photoName);
            photo.setPhotoSource(photoSource);
            photo.setPhotoType(photoType);
            photoMapper.insert(photo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得文件名
     * */
    private String getFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String[] IEBrowerKeyWords = {"MSIE", "Trident", "Edge"};    //IE不同版本User-Agent中出现的关键词
        String userAgent = request.getHeader("User-Agent");    //获得请求头代理信息
        for (String keyWord : IEBrowerKeyWords) {
            if (userAgent.contains(keyWord)) {
                //统一为UTF-8编码显示，并对转换的+进行更正
                return URLEncoder.encode(fileName, "UTF-8").replace("+", " ");
            }
        }
        return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);//其他浏览器统一为ISO-8859-1编码显示
    }

    /**
     * 文件下载
     */
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, String fileName, int fileId) {
        try {
            fileName = repeatedNaming(fileName, fileId);
            String dir = "D:\\temp";
            java.io.File file = new java.io.File(dir + java.io.File.separator + fileName);
            fileName = getFileName(request, fileName);  //对下载名进行转码
            HttpHeaders headers = new HttpHeaders();    //设置响应头
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); //以流的形式下载文件数据
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
