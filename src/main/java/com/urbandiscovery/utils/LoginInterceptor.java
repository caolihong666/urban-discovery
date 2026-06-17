package com.urbandiscovery.utils;

import com.urbandiscovery.dto.UserDTO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    //业务规则：用户登录一次就刷新一次有效期
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否需要去拦截
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            //设置拦截状态
            response.setStatus(401);

            //拦截
            return false;
        }

        //放行
        return true;
    }
}
