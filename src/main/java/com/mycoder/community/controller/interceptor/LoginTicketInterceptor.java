package com.mycoder.community.controller.interceptor;

import com.mycoder.community.entity.LoginTicket;
import com.mycoder.community.entity.User;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CookieUtil;
import com.mycoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
    拦截器写完记得配置
 */
@Controller
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    HostHolder hostHolder;

    //controller层之前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ticket = CookieUtil.getValue("ticket", request);
        if(ticket != null){
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //若查询出的loginTicket是有效的（不为空、状态为有效状态、未过期）
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {

                User user = userService.findUserById(loginTicket.getUserId());
                //查询出的用户在模板处要用到，需要暂存起来，存在当前线程中
                hostHolder.setUser(user);

                //构建用户认证的结果，并存入SecurityContext,以便于Security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));

                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }

        return true;
    }

    //controller层之后，模板引擎之前调用，获取用户,传给modelAndView
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();

        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",  user);
        }
    }

    //请求完成后清理保存的用户，springMVC使用的是线程池，为了避免前世的影响，需要清除
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
