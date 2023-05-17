package com.express.message.controller;

import com.express.common.vo.R;
import com.express.message.service.EnshrineCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnshrineController {

    @Autowired
    EnshrineCacheService enshrineCacheService;

    @GetMapping("/addEnshrine/{type}/{id}/{account}")
    public R addEnshrine(@PathVariable String type, @PathVariable String id, @PathVariable String account){
        if(enshrineCacheService.addEnshrine(type,id,account)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/deleteEnshrine/{type}/{id}/{account}")
    public R deleteEnshrine(@PathVariable String type, @PathVariable String id,@PathVariable String account){
        if(enshrineCacheService.deleteEnshrine(type,id,account)){
            return R.ok();
        }
        return R.failed();
    }

}
