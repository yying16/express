package com.express.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.express.common.vo.R;
import com.express.user.domain.User;
import com.express.user.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RestTemplate restTemplate;

    //判断该用户账号密码是否正确
    public User getUser(User user) {
        try {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("account", user.getAccount());
            wrapper.eq("password", user.getPassword());
            return userMapper.selectOne(wrapper);
        } catch (Exception e) {
            return null;
        }
    }

    //判断用户是否已经注册
    public boolean checkUserHasRegister(String account) {
        try {
            return userMapper.selectById(account) != null;
        } catch (Exception e) {
            return false;
        }
    }

    //注册用户，将用户的数据写入数据库(创建成功则返回true，失败则返回false)
    public boolean createUser(User user) {
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param account 用户账号
     * @param oldPsw 旧密码
     * @param newPsw 新密码
     * @return
     */
    public boolean changePassword(String account, String oldPsw, String newPsw) {
        QueryWrapper<User> wrapper = new QueryWrapper();
        wrapper.eq("account", account);
        User user = userMapper.selectById(account);
        if (user.getPassword().equals(oldPsw)) {
            user.setPassword(newPsw);
            userMapper.updateById(user);
            return true;
        }
        return false;
    }

}
