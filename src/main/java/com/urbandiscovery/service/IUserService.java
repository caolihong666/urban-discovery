package com.urbandiscovery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.urbandiscovery.dto.LoginFormDTO;
import com.urbandiscovery.dto.Result;
import com.urbandiscovery.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {

    //发送手机验证码
    Result sendCode(String phone, HttpSession session);

    //实现登录功能
    Result login(LoginFormDTO loginForm, HttpSession session);
}
