package com.mycoder.community.controller;

import com.mycoder.community.service.AlphaService;
import com.mycoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author cj
 * @create 2021-12-06 19:56
 */
//--------------------------------第一天-----------------------------------------------------
@Controller
@RequestMapping("/day01")
public class HelloController {

    @RequestMapping("/Hello")
    @ResponseBody
    public String HelloCommunity(){
        return "Hello SpringBoot!";
    }

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getDao(){
        return alphaService.find();
    }

    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name, int age){
        //通过ajax获取前台数据
        System.out.println(name);
        System.out.println(age);

        //返回数据给前台
        return CommunityUtil.getJSONString(200, "IDEA OK!", null);
    }
}

//====================================第二天================================================
@Controller
@RequestMapping("/day02")
class AlphaController{

    //处理请求、相应请求的底层
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }

        System.out.println(request.getParameter("name"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");

        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>牛客网<h1>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //以下处理请求
    //GET请求
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            //相当于上面的  request.getParameter("name")
            @RequestParam(name = "name", required = false, defaultValue = "小明") String name,
            @RequestParam(name = "age", required = false, defaultValue = "15")int age){

        System.out.println(age);
        System.out.println(name);
        return "students";
    }

    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getstudent(@PathVariable(value = "id")int id){
        System.out.println(id);
        return "a student";
    }

    @RequestMapping(path = "/add")
    @ResponseBody
    public String addStudent(String name, String age){//???神奇
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //以上处理请求
    //以下响应数据，响应 html
    //方式一
    @RequestMapping(path = "teacher", method = RequestMethod.GET)
    public ModelAndView returnTeacher(){
        ModelAndView mdv = new ModelAndView();
        //模型数据
        mdv.addObject("name", "李六");
        mdv.addObject("age", 48);
        //视图
        mdv.setViewName("/demo/view");

        return mdv;
    }
    //方式二
    @RequestMapping(value = "/shcool", method = RequestMethod.GET)
    public String returnSchool(Model model){
        model.addAttribute("name", "南邮");
        model.addAttribute("age", "80");

        return "/demo/view";
    }

    @GetMapping("/emp")
    @ResponseBody
    public Map<String, Object> returnEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("二狗", 8000);

        return emp;
    }

    @GetMapping("/emplist")
    @ResponseBody
    public List<Map<String, Object>> returnEmps(){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("二狗", 8000);
        list.add(emp);

        emp.put("大狗", 9000);
        list.add(emp);
        return list;
    }

    @GetMapping("/hellocontroller/index")
    public String getIndex(){

        return "/index.html";
    }

    //cookie示例
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie, key - value
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //生效范围
        cookie.setPath("/community/day02");
        //设置存活时间: (s)
        cookie.setMaxAge(60 * 30);
        //发送cookie
        response.addCookie(cookie);

        return "cookie 设置完成！";
    }

    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String key){
        System.out.println(key);
        return "获得了cookie:  " + key;
    }

    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id", 001);
        session.setAttribute("name", "谷歌浏览器~~");
        return "session 设置成功";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "session 获取成功";
    }

}
