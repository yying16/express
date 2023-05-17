package com.express.activity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.common.util.IPageUtil;
import com.express.common.util.TimeUtil;
import com.express.activity.domain.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ActivityCacheService {


    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ActivityService activityService;
    /**
     * 查询activity
     * */
    public IPage getActivitys(long currentPage, long pageSize, Map<String, String> condition,String account) {
        try {
            List<Object> list = redisTemplate.opsForHash().values("activitys"); // 字符串数组
            List<Object> enshrineList = redisTemplate.opsForHash().values("enshrine"); // 点赞列表
            List<Object> ret = new ArrayList<>();
            String category = "",type = "",content = "";
            if(!condition.isEmpty()){// 条件搜索
                if(condition.getOrDefault("category","").length()>0){
                    category = condition.get("category");
                }
                if(condition.getOrDefault("type","").length()>0){
                    type = condition.get("type");
                }
                if(condition.getOrDefault("content","").length()>0){
                    content = condition.get("content");
                }
            }
            for (int i = 0; i < list.size(); i++) {
                JSONObject json = JSON.parseObject(String.valueOf(list.get(i))); // 每个activity
                if(Boolean.parseBoolean(json.get("deleted").toString())){
                    continue;
                }
                if(category.length()>0 && !json.get("category").equals(category))
                    continue;
                if(type.length()>0 && content.length()>0){
                    if(type.equals("标题")){
                        if(!json.get("title").toString().contains(content)){ // 不包含关键字
                            continue;
                        }
                    }else if(type.equals("发布者")){
                        if(!json.get("promulgator").toString().contains(content)){ // 不包含关键字
                            continue;
                        }
                    }else if(type.equals("内容")){
                        if(!json.get("content").toString().contains(content)){ // 不包含关键字
                            continue;
                        }
                    }
                }
                for (int j = 0; j < enshrineList.size(); j++) {
                    JSONObject enshrine = JSONObject.parseObject(String.valueOf(enshrineList.get(j)));
                    if(enshrine.get("type").equals("activity")&&enshrine.get("collectionId").equals(json.get("activityId"))&&enshrine.get("collector").equals(account)){ // 当前用户点了赞
                        json.put("enshrined",true);
                    }
                }
                ret.add(json.toString());
            }
            return IPageUtil.listToIPage(ret, currentPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 添加activity
     */
    public boolean addActivity(Activity activity) {
        try {
            String id = IdWorker.getIdStr(Activity.class);
            activity.setActivityId(id);
            activity.setDeleted(false);
            activity.setAnonymous(false);
            activity.setCollected(0);
            activity.setEnshrined(false);
            activity.setCreateTime(TimeUtil.getCurrentTime());
            activity.setUpdateTime(TimeUtil.getCurrentTime());
            if(activity.getCategory()==null||activity.getCategory().length()==0){
                activity.setCategory("其他");
            }
            //写入redis
            redisTemplate.opsForHash().put("activitys", id, JSONObject.toJSON(activity).toString());
            //异步写入数据库
            kafkaTemplate.send("activity","addActivity" ,JSONObject.toJSON(activity).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询activity详情
     * */
    public Activity getDetail(String activityId){
        String activity = String.valueOf(redisTemplate.opsForHash().get("activitys",activityId));
        Activity ret = JSONObject.toJavaObject(JSON.parseObject(activity),Activity.class);
        String[] list = ret.getPhoto().split("#");
        for (int i = 0; i < list.length; i++) {
            list[i] = String.valueOf(redisTemplate.opsForHash().get("photos",list[i]));
        }
        ret.setPictures(list);
        return ret;
    }

    /**
     * 删除activity
     * */
    public boolean delete(String activityId){
        try{
            String activity = String.valueOf(redisTemplate.opsForHash().get("activitys",activityId));
            Activity ret = JSONObject.toJavaObject(JSON.parseObject(activity),Activity.class);
            ret.setDeleted(true);
            //异步写入数据库
            kafkaTemplate.send("activity","updateActivity" ,JSONObject.toJSON(ret).toString());
            //修改redis
            redisTemplate.opsForHash().put("activitys",String.valueOf(ret.getActivityId()),JSONObject.toJSON(ret).toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Activity> getMyCollection(String account){
        List<Object> list = redisTemplate.opsForHash().values("enshrine");
        List<Activity> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject((String.valueOf(list.get(i))));
            if(jsonObject.get("type").equals("activity")&&jsonObject.get("collector").equals(account)){
                String json = String.valueOf(redisTemplate.opsForHash().get("activitys",String.valueOf(jsonObject.get("collectionId"))));
                Activity activity = JSONObject.parseObject(json,Activity.class);
                if(!activity.isDeleted())
                    ret.add(activity);
            }
        }
        return ret;
    }


    public boolean refresh(){
        List<Object> list = redisTemplate.opsForHash().values("activitys"); // 字符串数组
        List<Activity> activitys = activityService.getActivitys();
        for (int i = 0; i < list.size(); i++) { // 清除已删除的
            Activity activity = JSONObject.toJavaObject(JSON.parseObject(String.valueOf(list.get(i))),Activity.class);
            if(activity.isDeleted()){
                redisTemplate.boundHashOps("activitys").delete(activity.getActivityId());
            }
        }
        for (int i = 0; i < activitys.size(); i++) { // 插入未缓存的数据
            Activity activity = activitys.get(i);
            if(Boolean.FALSE.equals(redisTemplate.boundHashOps("activitys").hasKey(activity.getActivityId()))){
                redisTemplate.opsForHash().put("activitys",activity.getActivityId(),JSONObject.toJSON(activity).toString());
            }
        }
        return true;
    }


}