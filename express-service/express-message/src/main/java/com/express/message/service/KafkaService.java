package com.express.message.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.express.message.domain.Enshrine;
import com.express.message.domain.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    EnshrineService enshrineService;

    @Autowired
    MessageService messageService;

    @KafkaListener(topics = "enshrine")
    public void enshrineListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        switch (record.key()){
            case "addEnshrine":{
                JSON json = JSON.parseObject(record.value());
                Enshrine enshrine = JSONObject.toJavaObject(json, Enshrine.class);
                enshrineService.addEnshrine(enshrine);
                break;
            }
            case "deleteEnshrine":{
                long id = Long.parseLong(record.value());
                enshrineService.deleteEnshrine(id);
                break;
            }
        }
        ack.acknowledge();
    }
    @KafkaListener(topics = "message")
    public void messageListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        switch (record.key()){
            case "addMessage":{
                JSON json = JSON.parseObject(record.value());
                Message message = JSONObject.toJavaObject(json, Message.class);
                messageService.addMessage(message);
                break;
            }
            case "updateMessage":{
                JSON json = JSON.parseObject(record.value());
                Message message = JSONObject.toJavaObject(json, Message.class);
                messageService.updateMessage(message);
                break;
            }
        }
        ack.acknowledge();
    }
}
