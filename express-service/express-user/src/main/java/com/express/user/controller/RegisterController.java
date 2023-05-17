package com.express.user.controller;

import com.express.user.domain.User;
import com.express.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegisterController {

    @Autowired
    UserService userService;
    
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/checkUser")
    //检验数据格式
    public String checkUser(@Valid @ModelAttribute User user, BindingResult bindingResult) {//注册数据校验
        if (bindingResult.hasErrors()) {//如果数据校验有误，则返回注册界面
            return "register";
        }
        //如果数据校验无误，则将数据添加到数据库中
        if (userService.createUser(user)) {
            return "login";
        } else {
            return "register";
        }
    }
}
