package com.express.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.express.common.vo.R;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 跨域控制器
 * 请求中url表示要访问的服务端
 * */
@RestController
public class CrossController {
    @Autowired
    private RestTemplate restTemplate;

    /**跨服务调用方法*/
    @GetMapping("/cross/get")
    public R Get(@Param("url") String url) {
        R r = restTemplate.getForObject(url,R.class);
        return r;
    }

    @PostMapping("/cross/post")
    public R Post(@Param("url") String url,@RequestBody Map body){
        return restTemplate.postForObject(url,body,R.class);
    }
}
