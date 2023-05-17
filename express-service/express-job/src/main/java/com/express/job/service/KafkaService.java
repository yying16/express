package com.express.job.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.express.job.domain.Job;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaService {

    @Autowired
    JobService jobService;

    @KafkaListener(topics = "job")
    public void jobListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        switch (record.key()){
            case "addJob":{
                JSON json = JSON.parseObject(record.value());
                Job job = JSONObject.toJavaObject(json, Job.class);
                jobService.addJob(job);
                break;
            }
            case "updateJob":{
                JSON json = JSON.parseObject(record.value());
                Job job = JSONObject.toJavaObject(json, Job.class);
                jobService.updateJob(job);
                break;
            }
        }
        ack.acknowledge();
    }
}