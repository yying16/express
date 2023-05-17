package com.express.note.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.express.note.domain.Note;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaService {

    @Autowired
    NoteService noteService;

    @KafkaListener(topics = "note")
    public void noteListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        switch (record.key()){
            case "addNote":{
                JSON json = JSON.parseObject(record.value());
                Note note = JSONObject.toJavaObject(json, Note.class);
                noteService.addNote(note);
                break;
            }
            case "updateNote":{
                JSON json = JSON.parseObject(record.value());
                Note note = JSONObject.toJavaObject(json, Note.class);
                noteService.updateNote(note);
                break;
            }
        }
        ack.acknowledge();
    }
}