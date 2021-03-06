package com.mycoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.mycoder.community.entity.User;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.CommunityUtil;
import com.mycoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author cj
 * @create 2021-12-11 14:31
 */
@Controller
public class LoginController implements CommunityConstant {

    public static final Logger  logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    RedisTemplate redisTemplate;



    //注意此处方法为GET
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegister(){
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //注意：此处方法为POST
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String Register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        //注册信息没有问题
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功, 我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));

            return getRegister();
        }
    }

    //url = http://localhost:8080/community/activation/id/激活码
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId")int userId, @PathVariable("code")String code){
        int result = userService.activation(userId, code);

        //根据result中的值，动态显示页面中的值（激活结果）
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以使用了！");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效的操作，该账号已经被激活");
            model.addAttribute("target", "/index");
        }else if(result == ACTIVATION_FAILURE){
            model.addAttribute("msg", "激活失败，激活码不正确");
            model.addAttribute("target", "/index");
        }
        //返回到的页面
        return "/site/operate-result";
    }


    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        //生成验证码：文字和图片
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //验证码文字存入session
        //session.setAttribute("kaptcha", text);

        //验证码重构： 验证码归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);


        //将验证码图片传给浏览器，并显示
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }

    }


    @PostMapping("/login")
    public String login(String username,  String password, String code, boolean rememberme,
                        Model model/*, HttpSession session*/, HttpServletResponse response,
                       @CookieValue("kaptchaOwner") String kaptchaOwner ){

        //检查验证码
        //String kaptcha = (String) session.getAttribute("kaptcha");

        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }

        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //验证成功
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setMaxAge(expiredSeconds);
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }


    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }


    @GetMapping("/forget")
    public String getForget(){
        return "/site/forget";
    }


    @GetMapping ("/forget/code")
    public String getCode(String email, Model model, HttpSession session){
        Map<String, Object> map = userService.sentCode(email);
        if(map.containsKey("code")){
            session.setAttribute("code", map.get("code"));
            session.setAttribute("email", map.get("email"));
        }else{
            model.addAttribute("emailMsg", map.get("emailMsg"));
        }
        return "/site/forget";
    }


    @PostMapping("/forget")
    public String reSetPassword(String email, String code, String newPassword, HttpSession session, Model model){
        //先验证码和邮箱是否一致
        if(!email.equals(session.getAttribute("email")) || !code.equals(session.getAttribute("code"))){
            model.addAttribute("codeMsg", "验证码不正确");
            return "site/forget";
        }

        Map<String, Object> map = userService.reSetPassword(email,newPassword);
        if(map.containsKey("newPassword")){
            model.addAttribute("msg", "密码重置成功");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else{
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/forget";
        }
    }
}
