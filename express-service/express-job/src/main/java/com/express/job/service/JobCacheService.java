package com.express.job.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.common.util.IPageUtil;
import com.express.common.util.TimeUtil;
import com.express.job.domain.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class JobCacheService {


    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private JobService jobService;
    /**
     * 查询job
     * */
    public IPage getJobs(long currentPage, long pageSize, Map<String, String> condition,String account) {
        try {
            List<Object> list = redisTemplate.opsForHash().values("jobs"); // 字符串数组
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
                JSONObject json = JSON.parseObject(String.valueOf(list.get(i))); // 每个job
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
                    if(enshrine.get("type").equals("job")&&enshrine.get("collectionId").equals(json.get("jobId"))&&enshrine.get("collector").equals(account)){ // 当前用户点了赞
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
     * 添加job
     */
    public boolean addJob(Job job) {
        try {
            String id = IdWorker.getIdStr(Job.class);
            job.setJobId(id);
            job.setDeleted(false);
            job.setAnonymous(false);
            job.setCollected(0);
            job.setEnshrined(false);
            job.setCreateTime(TimeUtil.getCurrentTime());
            job.setUpdateTime(TimeUtil.getCurrentTime());
            if(job.getCategory()==null||job.getCategory().length()==0){
                job.setCategory("其他");
            }
            if(job.getMoney()==null||job.getMoney().length()==0){
                job.setMoney("面议");
            }
            //写入redis
            redisTemplate.opsForHash().put("jobs", id, JSONObject.toJSON(job).toString());
            //异步写入数据库
            kafkaTemplate.send("job","addJob" ,JSONObject.toJSON(job).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询job详情
     * */
    public Job getDetail(String jobId){
        String job = String.valueOf(redisTemplate.opsForHash().get("jobs",jobId));
        Job ret = JSONObject.toJavaObject(JSON.parseObject(job),Job.class);
        String[] list = ret.getPhoto().split("#");
        for (int i = 0; i < list.length; i++) {
            list[i] = String.valueOf(redisTemplate.opsForHash().get("photos",list[i]));
        }
        ret.setPictures(list);
        return ret;
    }

    /**
     * 删除job
     * */
    public boolean delete(String jobId){
        try{
            String job = String.valueOf(redisTemplate.opsForHash().get("jobs",jobId));
            Job ret = JSONObject.toJavaObject(JSON.parseObject(job),Job.class);
            ret.setDeleted(true);
            //异步写入数据库
            kafkaTemplate.send("job","updateJob" ,JSONObject.toJSON(ret).toString());
            //修改redis
            redisTemplate.opsForHash().put("jobs",String.valueOf(ret.getJobId()),JSONObject.toJSON(ret).toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Job> getMyCollection(String account){
        List<Object> list = redisTemplate.opsForHash().values("enshrine");
        List<Job> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject((String.valueOf(list.get(i))));
            if(jsonObject.get("type").equals("job")&&jsonObject.get("collector").equals(account)){
                String json = String.valueOf(redisTemplate.opsForHash().get("jobs",String.valueOf(jsonObject.get("collectionId"))));
                Job job = JSONObject.parseObject(json,Job.class);
                if(!job.isDeleted())
                    ret.add(job);
            }
        }
        return ret;
    }

    public boolean refresh(){
        List<Object> list = redisTemplate.opsForHash().values("jobs"); // 字符串数组
        List<Job> jobs = jobService.getJobs();
        for (int i = 0; i < list.size(); i++) { // 清除已删除的
            Job job = JSONObject.toJavaObject(JSON.parseObject(String.valueOf(list.get(i))),Job.class);
            if(job.isDeleted()){
                redisTemplate.boundHashOps("jobs").delete(job.getJobId());
            }
        }
        for (int i = 0; i < jobs.size(); i++) { // 插入未缓存的数据
            Job job = jobs.get(i);
            if(Boolean.FALSE.equals(redisTemplate.boundHashOps("jobs").hasKey(job.getJobId()))){
                redisTemplate.opsForHash().put("jobs",job.getJobId(),JSONObject.toJSON(job).toString());
            }
        }
        return true;
    }
}
