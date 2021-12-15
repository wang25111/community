package com.mycoder.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cj
 * @create 2021-12-15 15:03
 */
//拦截器
@Component
public class AlphaInterceptor implements HandlerInterceptor {

    public static final Logger log = LoggerFactory.getLogger(AlphaInterceptor.class);

    //controller层之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("========= preHandle  " + handler);
        return true;
    }

    //Controller之后，视图层之前执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("==========  postHandle  " + handler);
    }

    //TemplateEngine之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.debug("==========  afterCompletion  " + handler);
    }
}
