package com.express.trade.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.express.common.util.IPageUtil;
import com.express.common.util.TimeUtil;
import com.express.trade.domain.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TradeCacheService {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TradeService tradeService;

    /**
     * 查询trade
     * */
    public IPage getTrades(long currentPage, long pageSize, Map<String, String> condition,String account) {
        try {
            List<Object> list = redisTemplate.opsForHash().values("trades"); // 字符串数组
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
                JSONObject json = JSON.parseObject(String.valueOf(list.get(i))); // 每个trade
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
                    if(enshrine.get("type").equals("trade")&&enshrine.get("collectionId").equals(json.get("tradeId"))&&enshrine.get("collector").equals(account)){ // 当前用户点了赞
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
     * 添加trade
     */
    public boolean addTrade(Trade trade) {
        try {
            String id = IdWorker.getIdStr(Trade.class);
            trade.setTradeId(id);
            trade.setDeleted(false);
            trade.setAnonymous(false);
            trade.setCollected(0);
            trade.setEnshrined(false);
            trade.setCreateTime(TimeUtil.getCurrentTime());
            trade.setUpdateTime(TimeUtil.getCurrentTime());
            if(trade.getCategory()==null||trade.getCategory().length()==0){
                trade.setCategory("其他");
            }
            if(trade.getMoney()==null||trade.getMoney().length()==0){
                trade.setMoney("面议");
            }
            //写入redis
            redisTemplate.opsForHash().put("trades", id, JSONObject.toJSON(trade).toString());
            //异步写入数据库
            kafkaTemplate.send("trade","addTrade" ,JSONObject.toJSON(trade).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询trade详情
     * */
    public Trade getDetail(String tradeId){
        String trade = String.valueOf(redisTemplate.opsForHash().get("trades",tradeId));
        Trade ret = JSONObject.toJavaObject(JSON.parseObject(trade),Trade.class);
        String[] list = ret.getPhoto().split("#");
        for (int i = 0; i < list.length; i++) {
            list[i] = String.valueOf(redisTemplate.opsForHash().get("photos",list[i]));
        }
        ret.setPictures(list);
        return ret;
    }

    /**
     * 删除trade
     * */
    public boolean delete(String tradeId){
        try{
            String trade = String.valueOf(redisTemplate.opsForHash().get("trades",tradeId));
            Trade ret = JSONObject.toJavaObject(JSON.parseObject(trade),Trade.class);
            ret.setDeleted(true);
            //异步写入数据库
            kafkaTemplate.send("trade","updateTrade" ,JSONObject.toJSON(ret).toString());
            //修改redis
            redisTemplate.opsForHash().put("trades",String.valueOf(ret.getTradeId()),JSONObject.toJSON(ret).toString());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Trade> getMyCollection(String account){
        List<Object> list = redisTemplate.opsForHash().values("enshrine");
        List<Trade> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject((String.valueOf(list.get(i))));
            if(jsonObject.get("type").equals("trade")&&jsonObject.get("collector").equals(account)){
                String json = String.valueOf(redisTemplate.opsForHash().get("trades",String.valueOf(jsonObject.get("collectionId"))));
                Trade trade = JSONObject.parseObject(json,Trade.class);
                if(!trade.isDeleted())
                    ret.add(trade);
            }
        }
        return ret;
    }



    public boolean refresh(){
        List<Object> list = redisTemplate.opsForHash().values("trades"); // 字符串数组
        List<Trade> trades = tradeService.getTrades();
        for (int i = 0; i < list.size(); i++) { // 清除已删除的
            Trade trade = JSONObject.toJavaObject(JSON.parseObject(String.valueOf(list.get(i))),Trade.class);
            if(trade.isDeleted()){
                redisTemplate.boundHashOps("trades").delete(trade.getTradeId());
            }
        }
        for (int i = 0; i < trades.size(); i++) { // 插入未缓存的数据
            Trade trade = trades.get(i);
            if(Boolean.FALSE.equals(redisTemplate.boundHashOps("trades").hasKey(trade.getTradeId()))){
                redisTemplate.opsForHash().put("trades",trade.getTradeId(),JSONObject.toJSON(trade).toString());
            }
        }
        return true;
    }

}