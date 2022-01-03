package com.mycoder.community.config;

import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author cj
 * @create 2021-12-30 22:11
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    /**对于 resources/  下的所有资源都不拦截 */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权：访问以下路径，需要有以下权限
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/user/updatePassword",
                        "/like",
                        "/letter/**",
                        "/notice/**",
                        "/follow",
                        "/unfollow",
                        "/followees/**",
                        "/followers/**",
                        "/discuss/add/**",
                        "/comment/add/**"
                ).hasAnyAuthority(
                        AUTHORITY_USER, AUTHORITY_MODERATOR, AUTHORITY_ADMIN
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                ).hasAnyAuthority(AUTHORITY_MODERATOR, AUTHORITY_ADMIN)
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                ).hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()//其它请求，全部允许
                .and().csrf().disable();//关闭csrf检查了........


        //权限不够的处理
        http.exceptionHandling()
                //未登录时
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String requestHeader = request.getHeader("x-requested-with");
                        //如果是异步请求，则返回一个json字符串
                        if("XMLHttpRequest".equals(requestHeader)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "未登录，无权限"));
                        }else{
                            //如果是同步请求，则重定向到登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                //权限不够时,处理方法同上
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String requestHeader = request.getHeader("x-requested-with");
                        //如果是异步请求，则返回一个json字符串
                        if("XMLHttpRequest".equals(requestHeader)){
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "权限不够"));
                        }else{
                            //如果是同步请求，则重定向到登录页面
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        //Security底层默认会拦截/logout请求，这样就无法执行自己写的逻辑
        //处理方法：修改拦截路径
        http.logout().logoutUrl("/sslogout");


    }
}
