package com.express.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.express.common.vo.R;
import com.express.user.bean.WebSocket;
import com.express.user.feign.MessageClient;
import com.express.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Resource
    WebSocket webSocket;

    @Autowired
    MessageClient messageClient;

    @GetMapping("/checkUserHasRegister/{account}")
    public boolean checkUserHasRegister(@PathVariable String account){
        return userService.checkUserHasRegister(account);
    }

    @GetMapping("/changePassword/{account}/{oldPsw}/{newPsw}")
    public boolean changePassword(@PathVariable String account,@PathVariable String oldPsw, @PathVariable String newPsw) {
        return userService.changePassword(account,oldPsw,newPsw);
    }

}
