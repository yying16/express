package com.express.message.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.message.domain.Enshrine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnshrineCacheService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 添加收藏标签
     */
    public boolean addEnshrine(String type, String id, String account) {
        try {
            Enshrine enshrine = new Enshrine(IdWorker.getIdStr(Enshrine.class), type, id, account);
            //写入redis
            redisTemplate.opsForHash().put("enshrine", String.valueOf(enshrine.getEnshrineId()), JSONObject.toJSON(enshrine).toString());
            //异步写入数据库
            kafkaTemplate.send("enshrine", "addEnshrine", JSONObject.toJSON(enshrine).toString());
            //更新对应的表
            changeAssociationTable(type, id, 1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除收藏标签
     */
    public boolean deleteEnshrine(String type, String id, String account) {
        try {
            List<Object> list = redisTemplate.opsForHash().values("enshrine");
            String enshrineId = "";
            for (int i = 0; i < list.size(); i++) {
                JSONObject temp = JSONObject.parseObject(String.valueOf(list.get(i)));
                if (temp.get("type").equals(type) && temp.get("collectionId").toString().equals(String.valueOf(id)) && temp.get("collector").equals(account)) {
                    enshrineId = String.valueOf(temp.get("enshrineId"));
                    break;
                }
            }
            if (enshrineId.length() == 0)
                return false;
            redisTemplate.opsForHash().delete("enshrine", enshrineId);
            //异步写入数据库
            kafkaTemplate.send("enshrine", "deleteEnshrine", enshrineId);
            //更新对应的表
            changeAssociationTable(type, id, -1);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void changeAssociationTable(String type, String id, int n) {
        JSONObject json = JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(type + "s", String.valueOf(id))));
        json.put("collected", Integer.parseInt(json.get("collected").toString()) + n);
        redisTemplate.opsForHash().put(type + "s", String.valueOf(json.get(type + "Id")), json.toString());
        //异步写入数据库
        kafkaTemplate.send(type, getMethodName("update", type), json.toString());
    }

    public String getMethodName(String action, String type) {
        String t = Character.toUpperCase(type.charAt(0)) + type.substring(1);
        return action + t;
    }
}
