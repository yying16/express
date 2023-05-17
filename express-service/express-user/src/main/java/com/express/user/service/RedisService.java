package com.express.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    /**写入uid和url的键值对*/
    public boolean addUrlToCache(String uid,String url){
        try{
            redisTemplate.opsForHash().put("photos",uid,url);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
