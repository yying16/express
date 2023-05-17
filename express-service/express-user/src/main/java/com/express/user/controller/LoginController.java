package com.express.user.controller;

import com.express.user.domain.User;
import com.express.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/toLogin")
    public String toLogin(User user, Model model, HttpSession httpSession) {//进行登录验证
        User temp;
        if ((temp = userService.getUser(user))!=null) {//验证账号密码是否已在用户数据库内
            user = temp;
            model.addAttribute("account",user.getAccount());
            model.addAttribute("password",user.getPassword());
            model.addAttribute("username",user.getUsername());
            model.addAttribute("telephone",user.getTelephone());
            model.addAttribute("email",user.getEmail());
            httpSession.setAttribute("onlineUser", user);
            log.info("用户"+user.getAccount()+"登录成功");
            return "index"; //进入对应的主界面
        }
        model.addAttribute("wrong", "用户名或密码错误，请重新登录！");//设置警告信息
        return "login";//返回登录界面
    }

    @GetMapping("/logout")
    //@ResponseBody
    public String logout(HttpSession httpSession) {//退出登录
        httpSession.invalidate();//清除
        return "redirect:/login";
    }


}
