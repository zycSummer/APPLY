package com.jet.cloud.jetsndcxqyapply.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.RandomValidateCodeUtil;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户Controller
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemUserMapper systemUserMapper;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    // 新增用户
    @PostMapping("/user/insertUser")
    @ResponseBody
    public Dto insertUser(@RequestBody SystemUser systemUser) {
        Dto user = userService.insertUser(systemUser);
        return user;
    }

    // 更新用户
    @PostMapping("/user/updateUser")
    @ResponseBody
    public Dto updateUser(@RequestBody SystemUser systemUser) {
        Dto dto = userService.updateUser(systemUser);
        return dto;
    }

    // 删除
    @DeleteMapping("/user/deleteUser")
    @ResponseBody
    public Dto deleteUser(@RequestBody JSONObject jsonObject) {
        String userId = jsonObject.get("userId").toString();
        Dto dto = userService.deleteUser(userId);
        return dto;
    }

    // 通过userId查询用户
    @RequestMapping("/user/getUserByUserId")
    @ResponseBody
    public Dto getUserByUserId(@RequestParam(value = "userId") String id) {
        Dto user = userService.getUserByUserId(id);
        return user;
    }

    // 根据userid 查询企业邮箱
    @RequestMapping("/user/getMailByUserId")
    @ResponseBody
    public Dto getMailByUserId(@RequestParam(value = "userId") String id) {
        Dto user = userService.getMailByUserId(id);
        return user;
    }

    // 更新用户密码
    @RequestMapping("/user/updateUserByPwd")
    @ResponseBody
    public Dto updateUserByPwd(@RequestParam(value = "oldPwd") String oldPwd, @RequestParam(value = "newPwd") String newPwd) {
        Dto dto = userService.updateUserByPwd(oldPwd, newPwd);
        return dto;
    }

    // 查找全部系统角色
    @RequestMapping("/user/getUserRoles")
    @ResponseBody
    public Dto getUserRoles() {
        Dto userRoles = userService.getUserRoles();
        return userRoles;
    }

    // 根据siteId查询企业
    @RequestMapping("/user/getSiteBySiteId")
    @ResponseBody
    public Dto getSiteBySiteId(@RequestParam(value = "siteId") String id) {
        Dto siteBySiteId = userService.getSiteBySiteId(id);
        return siteBySiteId;
    }

    // 根据userId查询企业
    @RequestMapping("/user/getSiteByUserId")
    @ResponseBody
    public Dto getSiteByUserId(@RequestParam(value = "userId") String userId) {
        SystemUser user = systemUserMapper.getUserByUserId(userId);
        Dto siteBySiteId = userService.getSiteBySiteId(user.getSiteId());
        return siteBySiteId;
    }

    // 新增企业
    @RequestMapping("/user/insertSite")
    @ResponseBody
    public Dto insertSite(@RequestBody Map<String, Object> map) {
        Dto dto = userService.insertSite(map);
        return dto;
    }

    // 更新企业
    @RequestMapping("/user/updateSite")
    @ResponseBody
    public Dto updateSite(@RequestBody CompanySite companySite) {
        Dto dto = userService.updateSite(companySite);
        return dto;
    }

    //查询企业类型
    @RequestMapping("/user/getSysSiteType")
    @ResponseBody
    public Dto getSysSiteType() {
        Dto sysSiteType = userService.getSysSiteType();
        return sysSiteType;
    }

    //查询所属行业
    @RequestMapping("/user/getSysIndustyType")
    @ResponseBody
    public Dto getSysIndustyType() {
        return userService.getSysIndustyType();
    }

    //查询所属镇(街道)
    @RequestMapping("/user/getSysStreet")
    @ResponseBody
    public Dto getSysStreet() {
        return userService.getSysStreet();
    }

    //查询所属工业园
    @RequestMapping("/user/getSysIndustrialPark")
    @ResponseBody
    public Dto getSysIndustrialPark() {
        return userService.getSysIndustrialPark();
    }

    // 获取验证码
    @RequestMapping("/verify/verificationCode")
    public void getVerify(String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            logger.error("获取验证码失败>>>> ", e);
        }
    }
}
