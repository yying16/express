package com.express.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.common.vo.R;
import com.express.message.domain.Message;
import com.express.message.service.MessageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MessageController {

    @Autowired
    MessageCacheService messageCacheService;

    @PostMapping("/addMessage")
    public R addMessage(@RequestBody Message message){
        if(messageCacheService.addMessage(message)){
            return R.ok();
        }
        return R.failed();
    }

    @PostMapping("/getMessage/{currentPage}/{pageSize}/{account}/{way}")
    public R getMessage(@PathVariable long currentPage, @PathVariable long pageSize, @RequestBody Map<String, String> condition, @PathVariable String account, @PathVariable String way){
        IPage iPage = messageCacheService.getMessage(currentPage,pageSize,condition,account,way);
        if(iPage==null)
            return R.failed();
        return R.ok(iPage);
    }

    @GetMapping("/getDetail/{messageId}")
    public R getDetail(@PathVariable String messageId){
        Message job = messageCacheService.getDetail(messageId);
        if(job==null){
            return R.failed();
        }
        return R.ok(job);
    }

    @GetMapping("/delete/{messageId}")
    public R delete(@PathVariable String messageId){
        if(messageCacheService.delete(messageId)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getMessageUnRead/{account}")
    public R getMessageUnRead(@PathVariable String account){
        List<String> messages =  messageCacheService.getMessageUnRead(account);
        List<JSONObject> ret = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            JSONObject message = JSONObject.parseObject(messages.get(i));
            ret.add(new JSONObject(){{
                put("sender",message.get("sender"));
                put("content",message.get("content"));
            }});
        }
        return R.ok(ret);
    }
}
