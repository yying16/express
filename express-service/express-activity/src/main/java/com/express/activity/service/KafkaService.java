package com.express.activity.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.express.activity.domain.Activity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaService {

    @Autowired
    ActivityService activityService;

    @KafkaListener(topics = "activity")
    public void activityListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        switch (record.key()){
            case "addActivity":{
                JSON json = JSON.parseObject(record.value());
                Activity activity = JSONObject.toJavaObject(json, Activity.class);
                activityService.addActivity(activity);
                break;
            }
            case "updateActivity":{
                JSON json = JSON.parseObject(record.value());
                Activity activity = JSONObject.toJavaObject(json, Activity.class);
                activityService.updateActivity(activity);
                break;
            }
        }
        ack.acknowledge();
    }
}