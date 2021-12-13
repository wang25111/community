package com.mycoder.community.service;

import com.mycoder.community.dao.LoginTicketMapper;
import com.mycoder.community.dao.UserMapper;
import com.mycoder.community.entity.LoginTicket;
import com.mycoder.community.entity.User;
import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.CommunityUtil;
import com.mycoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author cj
 * @create 2021-12-09 18:08
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    //根据id查询到用户
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    //处理注册
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }


        //验证重要信息是否为空
        if(StringUtils.isBlank(user.getUserName())){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }


        //验证重要信息是否重复，根据输入信息去数据库中查找，查到了说明重复
        User u;
        //验证账号（用户名）
        u = userMapper.selectByName(user.getUserName());
        if(u != null){
            map.put("usernameMsg", "账号已被注册！");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "邮箱已被注册！");
            return map;
        }

        //验证通过后，进行注册
        //设置密码： 原密码 + 随机字符串 进行MD5加密
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));

        user.setType(0);//普通用户
        user.setStatus(0);//未激活
        user.setActivationCode(CommunityUtil.generateUUID());//激活码
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));//设置初始头像
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        //发送注册邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //url = http://localhost:8080/community/activation/id/激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号" , content);

        return map;
    }

    //返回激活状态
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);

        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_SUCCESS;
        }
    }

    //处理登录
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        //验证
        //验证用户名是否存在
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "账号不存在");
            return map;
        }
        //验证是否激活
        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        //验证密码是否正确
        password = CommunityUtil.md5( password + user.getSalt()); //注意顺序
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码错误");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);//0-有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }
}
