package com.mycoder.community.controller;

import com.mycoder.community.entity.User;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author cj
 * @create 2021-12-11 14:31
 */
@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    UserService userService;

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

        return "/site/operate-result";
    }
}
