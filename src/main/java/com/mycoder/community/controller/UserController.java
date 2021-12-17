package com.mycoder.community.controller;

import com.mycoder.community.annotation.LoginRequired;
import com.mycoder.community.entity.User;
import com.mycoder.community.service.UserService;
import com.mycoder.community.util.CommunityUtil;
import com.mycoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author cj
 * @create 2021-12-16 14:01
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @GetMapping("/setting")
    @LoginRequired              //登录才能调用此方法
    public String getSettingPage(){
        return "site/setting";
    }

    /**上传用户头像*/
    @PostMapping("/upload")
    @LoginRequired              //登录才能调用此方法
    public String upLoadHeader(MultipartFile headerImg, Model model){
        //若头像为空
        if(headerImg == null){
            model.addAttribute("error", "头像不能为空");
            return "/site/setting";
        }

        //获取用户上传文件的文件名后缀,如".jpg"...
        String fileName = headerImg.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        //更改文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);

        try {
            //把headerImg文件写入到dest的路径中
            headerImg.transferTo(dest);
        } catch (IOException e) {
            log.error("头像上传失败" + e.getMessage());
            throw new RuntimeException("上传文件失败" + e);
        }


        //更新当前用户的头像路径（web访问路径）
        //localhost:8080/community/user/header/xxx.jpg
        User user = hostHolder.getUser();
        String headUrl = domain +  contextPath + "/user/header/" + fileName;
        userService.updateHeadUrl(user.getId(), headUrl);

        return "redirect:/index";
//        return "/index";//会发生异常
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //文件在服务器中存放的路径
        fileName = uploadPath + "/" +fileName;

        //响应图片
        response.setContentType("image/" + suffix);
        try(
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ){
                byte[] buffer = new byte[1024];//设置缓冲区
                int b = 0;

                while((b = fis.read(buffer)) != -1){
                    os.write(buffer, 0, b);
                }
        }catch (IOException e){
            log.error("读取头像失败" + e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model){
        User user = hostHolder.getUser();

        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);

        if(map.containsKey("oldPasswordMsg")){
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            return "/site/setting";
        }

        if(map.containsKey("newPasswordMsg")){
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }

        return "redirect:/logout";
    }
}
