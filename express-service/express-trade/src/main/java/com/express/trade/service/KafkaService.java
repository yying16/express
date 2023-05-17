package com.express.trade.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.express.trade.domain.Trade;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaService {

    @Autowired
    TradeService tradeService;

    @KafkaListener(topics = "trade")
    public void tradeListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        switch (record.key()){
            case "addTrade":{
                JSON json = JSON.parseObject(record.value());
                Trade trade = JSONObject.toJavaObject(json, Trade.class);
                tradeService.addTrade(trade);
                break;
            }
            case "updateTrade":{
                JSON json = JSON.parseObject(record.value());
                Trade trade = JSONObject.toJavaObject(json, Trade.class);
                tradeService.updateTrade(trade);
                break;
            }
        }
        ack.acknowledge();
    }
}