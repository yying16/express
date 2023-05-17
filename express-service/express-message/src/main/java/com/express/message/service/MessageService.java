package com.express.message.service;

import com.express.message.dao.MessageMapper;
import com.express.message.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;
    /**添加message*/
    public boolean addMessage(Message message){
        try{
            messageMapper.insert(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**更新message*/
    public boolean updateMessage(Message message){
        try{
            messageMapper.updateById(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
