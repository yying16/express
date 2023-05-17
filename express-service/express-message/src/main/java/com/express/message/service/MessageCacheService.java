package com.express.message.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.common.util.IPageUtil;
import com.express.common.util.TimeUtil;
import com.express.message.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageCacheService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 获取当前用户未读消息
     */
    public List<String> getMessageUnRead(String account) {
        List<Object> list = redisTemplate.opsForHash().values("messages");
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Message message = JSON.parseObject(String.valueOf(list.get(i)), Message.class);
            if (message.getReceiver().equals(account) && message.getStatus().equals("未读")) {
                ret.add(JSONObject.toJSONString(message));
            }
        }
        return ret;
    }

    /**
     * 添加消息
     */
    public boolean addMessage(Message message) {
        try {
            message.setMessageId(IdWorker.getIdStr(Message.class));
            message.setDeleted(false);
            message.setSendTime(TimeUtil.getCurrentTime());
            message.setStatus("未读");
            //写入redis
            redisTemplate.opsForHash().put("messages", String.valueOf(message.getMessageId()), JSONObject.toJSON(message).toString());
            //kafka异步写入数据库
            kafkaTemplate.send("message", "addMessage", JSONObject.toJSON(message).toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查看所有消息（设置为已读，设置已读时间）
     *
     * @param way 取值(sender/receiver)
     */
    public IPage getMessage(long currentPage, long pageSize, Map<String, String> condition, String account, String way) {
        try {
            List<Object> list = redisTemplate.opsForHash().values("messages");
            List<Object> ret = new ArrayList<>();
            String type = "", content = "";
            if (!condition.isEmpty()) {//刷新
                if (condition.getOrDefault("type", "").length() > 0) {
                    type = condition.get("type");
                }
                if (condition.getOrDefault("content", "").length() > 0) {
                    content = condition.get("content");
                }
            }
            for (int i = 0; i < list.size(); i++) {
                JSONObject json = JSON.parseObject(String.valueOf(list.get(i))); // 每个message
                if (Boolean.parseBoolean(json.get("deleted").toString())) {
                    continue;
                }
                if (!json.get(way).equals(account)) // 与我无关的消息
                    continue;
                if (type.length() > 0 && content.length() > 0) {
                    if (type.equals("发送者")) {
                        if (!json.get("sender").toString().contains(content)) { // 不包含关键字
                            continue;
                        }
                    } else if (type.equals("接收者")) {
                        if (!json.get("receiver").toString().contains(content)) { // 不包含关键字
                            continue;
                        }
                    } else if (type.equals("内容")) {
                        if (!json.get("content").toString().contains(content)) { // 不包含关键字
                            continue;
                        }
                    }
                }
                if (way.equals("receiver")) {//作为接收者的角度，查看消息后消息状态改为已读
                    //修改redis
                    json.put("status", "已读");
                    json.put("receiveTime", TimeUtil.getCurrentTime());
                    redisTemplate.opsForHash().put("messages", String.valueOf(json.get("messageId")), json.toJSONString());
                    //修改数据库
                    kafkaTemplate.send("message", "updateMessage", json.toJSONString());
                }
                ret.add(json.toString());
            }
            // 按发布时间排序
            ret.sort((a, b) -> {
                try {
                    JSONObject jsonA = JSONObject.parseObject(String.valueOf(a));
                    JSONObject jsonB = JSONObject.parseObject(String.valueOf(b));
                    long dateA = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(jsonA.get("sendTime"))).getTime();
                    long dateB = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(jsonB.get("sendTime"))).getTime();
                    return Long.compare(dateB,dateA); // 降序
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            });
            return IPageUtil.listToIPage(ret, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查看消息详情
     */
    public Message getDetail(String jobId) {
        String message = String.valueOf(redisTemplate.opsForHash().get("messages", jobId));
        Message ret = JSONObject.toJavaObject(JSON.parseObject(message), Message.class);
        //图片切分
        String[] list = ret.getPhoto().split("#");
        for (int i = 0; i < list.length; i++) {
            list[i] = String.valueOf(redisTemplate.opsForHash().get("photos", list[i]));
        }
        ret.setPictures(list);
        return ret;
    }

    /**
     * 删除
     */
    public boolean delete(String messageId) {
        try {
            String message = String.valueOf(redisTemplate.opsForHash().get("messages", messageId));
            Message ret = JSONObject.toJavaObject(JSON.parseObject(message), Message.class);
            ret.setDeleted(true);
            //异步写入数据库
            kafkaTemplate.send("message", "updateMessage", JSONObject.toJSON(ret).toString());
            //修改redis
            redisTemplate.opsForHash().put("messages", String.valueOf(ret.getMessageId()), JSONObject.toJSON(ret).toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
