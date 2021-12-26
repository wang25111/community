package com.mycoder.community.service;

import com.mycoder.community.dao.UserMapper;
import com.mycoder.community.entity.LoginTicket;
import com.mycoder.community.entity.User;
import com.mycoder.community.util.CommunityConstant;
import com.mycoder.community.util.CommunityUtil;
import com.mycoder.community.util.MailClient;
import com.mycoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    /**根据id查询到用户，先从缓存中查，再从数据库中查*/
    public User findUserById(int id){
        //return userMapper.selectById(id);

        User user = getCache(id);
        if(user == null){
            user = userMapper.selectById(id);
            initCache(id);
        }

        return user;
    }

    //按名字查询用户
    public User findUserByName(String userName){
        return userMapper.selectByName(userName);
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
        //模板中需要动态设置的数据：email、url
        context.setVariable("email", user.getEmail());
        //url = http://localhost:8080/community/activation/id/激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //将数据 和 模板处理成字符串
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
            return ACTIVATION_FAILURE;
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
        //保存到数据库中
        //loginTicketMapper.insertLoginTicket(loginTicket);

        //保存到redis中
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);// 值为json字符串

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);

        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);//将登录状态改为1，即无效
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    /**向邮箱发送一个验证码*/
    public Map<String, Object> sentCode(String email){
        Map<String, Object> map = new HashMap<>();
        //邮箱为空
        if(email == null || StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //邮箱不存在时
        User user = userMapper.selectByEmail(email);
        if(user == null){
            map.put("emailMsg", "邮箱不存在");
            return map;
        }

        //发送邮件
        Context context = new Context();
        //数据：用户邮箱、验证码
        String code = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("email", email);
        context.setVariable("code", code);
        //模板 + 数据
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "coder-community 重置密码", content);

        map.put("code", code);
        map.put("email", email);
        return map;
    }


    /**忘记密码时，重置密码*/
    public Map<String, Object> reSetPassword(String email, String newPassWord){
        Map<String, Object> map = new HashMap<>();

        //邮箱为空
        if(email == null || StringUtils.isBlank(email)){
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //密码长度不够
        if(newPassWord == null || StringUtils.isBlank(newPassWord) || newPassWord.length() < 8){
            map.put("newPasswordMsg", "密码长度至少为8位");
            return map;
        }

        //邮箱不存在时
        User user = userMapper.selectByEmail(email);
        if(user == null){
            map.put("emailMsg", "邮箱不存在");
            return map;
        }

        //通过验证，新密码加密，并更新密码
        newPassWord = CommunityUtil.md5(newPassWord + user.getSalt());
        map.put("newPassword", newPassWord);
        userMapper.updatePassword(user.getId(), newPassWord);

        return map;
    }


    /**根据 凭证（String） 查询 LoginTicket 对象*/
    public LoginTicket findLoginTicket(String ticket){
        //return loginTicketMapper.selectByTicket(ticket);

        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);

    }


    /**更新用户头像的url，更新前先清缓存*/
    public int updateHeadUrl(int userId, String url){
        clearCache(userId);
        return userMapper.updateHeader(userId, url);
    }


    /**更新用户的密码*/
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword){
        Map<String, Object> map = new HashMap<>();
        //判断原密码是否为空
        if(StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg", "原密码不能为空");
            return map;
        }
        //判断原密码是否正确
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!oldPassword.equals(user.getPassword())){
            map.put("oldPasswordMsg", "密码不正确");
            return map;
        }

        //判断新密码是否符合规范
        if(StringUtils.isBlank(newPassword) || newPassword.length() < 8){
            map.put("newPasswordMsg", "新密码长度至少8位");
            return map;
        }
        //先加密，再更新
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);
        return map;
    }


//================================redis进行优化============================================================

    /**优先从缓存中取值*/
    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }

    /**取不到时，初始化缓存*/
    private User initCache(int userId){
        //先从数据库中取
        User user = userMapper.selectById(userId);
        //在放入redis中，同时设置过期时间
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 60, TimeUnit.MINUTES);

        return user;
    }

    /**数据变动时，删除数据*/
    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
