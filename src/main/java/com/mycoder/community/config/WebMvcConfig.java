package com.mycoder.community.config;

import com.mycoder.community.controller.interceptor.AlphaInterceptor;
import com.mycoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.mycoder.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author cj
 * @create 2021-12-15 15:10
 */
//对拦截器进行配置
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    AlphaInterceptor alphaInterceptor;

    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)  //注册拦截器
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg","/**/*.png")  //不拦截哪些请求
                .addPathPatterns("/register","/login"); //拦截哪些请求


        registry.addInterceptor(loginTicketInterceptor)  //注册拦截器
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/**/*.gif");


        registry.addInterceptor(loginRequiredInterceptor)  //注册拦截器
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/**/*.gif");
    }
}
