package com.mycoder.community.config;

import com.mycoder.community.controller.interceptor.AlphaInterceptor;
import com.mycoder.community.controller.interceptor.DataInterceptor;
import com.mycoder.community.controller.interceptor.LoginTicketInterceptor;
import com.mycoder.community.controller.interceptor.MessageInterceptor;
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
    private AlphaInterceptor alphaInterceptor;

    /*引入了security，因此不再需要登录拦截器*/
    //@Autowired
    //LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)  //注册alphaInterceptor
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg","/**/*.png")  //不拦截哪些请求
                .addPathPatterns("/register","/login"); //拦截哪些请求


        registry.addInterceptor(loginTicketInterceptor)  //loginTicketInterceptor
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/**/*.gif");


//        registry.addInterceptor(loginRequiredInterceptor)  //注册登录拦截器
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/**/*.gif");


        registry.addInterceptor(messageInterceptor)  //注册messageInterceptor
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/**/*.gif");

        registry.addInterceptor(dataInterceptor)  //注册dataInterceptor
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg", "/**/*.gif");
    }
}
