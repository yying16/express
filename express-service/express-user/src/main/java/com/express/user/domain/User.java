package com.express.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.express.user.validation.AccountMatchConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@TableName("t_user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @NotBlank
    @Size(min = 4, max = 18, message = "账号长度应为4-18个字符")
    @Pattern(regexp = "^\\w+$", message = "账号必须只包含字母,数字和下划线_")
    @AccountMatchConstraint(message = "该账号已注册，请直接登录")
    @TableId(type = IdType.ASSIGN_ID)
    String account;     //账号(非空，非空格前后缀，首字母必须为大写，账号长度为8-16)
    @NotBlank
    @Pattern(regexp = "^\\S+$",message = "密码不能包含空格")
    String password;    //密码(非空，非空格前后缀，首字母必须为大写，账号长度为8-16)
    @NotBlank
    @Pattern(regexp = "^.{2,12}$", message = "用户名长度应为2-12个字符")
    String username;    //用户名(长度为2-12)
    @NotBlank
    @Pattern(regexp = "^1[356789]\\d{9}$", message = "手机号不合法")
    String telephone;   //手机号码
    @NotBlank
    @Pattern(regexp = "^\\w+@\\w+(\\.\\w+)$", message = "邮箱格式不正确")
    String email;       //邮箱
}
