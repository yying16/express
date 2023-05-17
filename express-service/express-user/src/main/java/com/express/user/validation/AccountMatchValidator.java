package com.express.user.validation;

import com.express.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountMatchValidator implements ConstraintValidator<AccountMatchConstraint, String> {

    @Autowired
    UserService userService;

    @Override
    public void initialize(AccountMatchConstraint accountMatchConstraint) {//对账号进行检验

    }

    @Override
    public boolean isValid(String Account, ConstraintValidatorContext constraintValidatorContext) {//重写校验方法
        return !userService.checkUserHasRegister(Account);//返回账号是否不在数据库中
    }


}
