package com.mycoder.community.controller.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author cj
 * @create 2021-12-24 14:37
 */
/**对所有service的调用记录日志*/
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);
    //切点，在哪些包的哪些方法
    @Pointcut("execution(* com.mycoder.community.service.*.*(..))")
    public void pointcut(){}

    //通知，方法前后
   @Before("pointcut()")
   public void before(JoinPoint joinPoint){
        //用户[x.x.x.x]在 [xxxx] 访问了 [xxxxx]
       ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
       //引入消费者后，并非所有的service都通过controller调用，因此有可能为null
       if(attributes == null) return;

       HttpServletRequest request = attributes.getRequest();
       //获取ip
       String ip = request.getRemoteHost();
       //获取日期
       String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
       //类名 + . + 方法名
       String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

       logger.info(String.format("用户[%s], 在[%s], 访问了[%s].", ip, now, target));
   }


}
