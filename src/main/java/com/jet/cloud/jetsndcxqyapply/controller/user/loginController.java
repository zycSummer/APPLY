package com.jet.cloud.jetsndcxqyapply.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.AES;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// 用户登录Controller
@Controller
public class loginController {
    @Autowired
    private UserService userService;
    @Autowired
    private SystemUserMapper systemUserMapper;


    Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping(value = "/user/login")
    @ResponseBody
    public Dto login(HttpServletRequest request, @RequestBody JSONObject systemUser, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return new Dto(false, "登陆失败", "/admin/login");
        }
        String userId = StringHelper.nvl(systemUser.get("userId"), "");
        String userPwd = StringHelper.nvl(systemUser.get("userPwd"), "");
        String roleId1 = StringHelper.nvl(systemUser.get("roleId"), "");
        String inputStr = StringHelper.nvl(systemUser.get("inputStr"), "");

        Dto user = userService.getUserByUserId(userId);
        String str = JSONObject.toJSONString(user);
        JSONObject object = (JSONObject) JSONObject.parse(str);
        String msg1 = StringHelper.nvl(object.get("msg"), "");
        if ("fail".equals(msg1)) {
            return new Dto(false, "用户不存在", "/admin/login");
        }
        String username = userId;
        String userRoleId = roleId1;
        String password = "";
        if (userPwd != null) {
            password = userPwd;
        }
        UsernamePasswordToken token = new UsernamePasswordToken(userId, AES.encrypt(password));

        //获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.getSession().setAttribute("loginUser", username);
            Dto dto1 = checkVerify(inputStr, session);

            String roleId = StringHelper.nvl(object.getJSONObject("data").get("roleId"), "");
            String userName = StringHelper.nvl(object.getJSONObject("data").get("userName"), "");
            String message = StringHelper.nvl(dto1.get("msg"), "");

            if (!"true".equals(message)) {
                return new Dto(false, "验证码错误", "/admin/login");
            } else if (!roleId.equals(userRoleId)) {
                return new Dto(false, "用户类型错误", "/admin/login");
            }
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            logger.info("对用户[" + username + "]进行登录验证..验证开始");
            currentUser.login(token);
            logger.info("对用户[" + username + "]进行登录验证..验证通过");

            SystemUser userAll = systemUserMapper.getUserByUserId(userId);
            Dto dto = userService.updateUserTime(userAll);
            String msg = dto.get("msg").toString();
            Map<String, Object> map = new HashMap<>();
            if ("success".equals(msg)) {
                if ("1001".equals(roleId)) {
                    //登录成功,防止表单重复提交,可以重定向到主页
                    map.put("url", "/admin/index");
                    map.put("userName", userName);
                    return new Dto(true, "登陆成功", map);
                } else if ("1002".equals(roleId)) {
                    map.put("url", "/admin/townStreet");
                    map.put("userName", userName);
                    return new Dto(true, "登陆成功", map);
                } else if ("1003".equals(roleId)) {
                    map.put("url", "/admin/officeArea");
                    map.put("userName", userName);
                    return new Dto(true, "登陆成功", map);
                } else {
                    map.put("url", "/admin/committee");
                    map.put("userName", userName);
                    return new Dto(true, "登陆成功", map);
                }
            } else {
                return new Dto(false, "登录失败", "/admin/login");
            }
        } catch (UnknownAccountException uae) {
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
            return new Dto(false, "未知账户", "/admin/login");
//            redirectAttributes.addAttribute("message", "未知账户");
        } catch (IncorrectCredentialsException ice) {
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
            return new Dto(false, "密码不正确", "/admin/login");
//            redirectAttributes.addAttribute("message", "密码不正确");
        } catch (AuthenticationException ae) {
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
            logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
            ae.printStackTrace();
            return new Dto(false, "用户名或密码不正确", "/admin/login");
        }
    }

    /**
     * 校验验证码
     */
    @ResponseBody
    public Dto checkVerify(@RequestBody String inputStr, HttpSession session) {
        try {
            //从session中获取随机数
            String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
            if (random == null) {
                return new Dto(false);
            }
            if (random.equals(inputStr)) {
                return new Dto(true);
            } else {
                return new Dto(false);
            }
        } catch (Exception e) {
            logger.error("验证码校验失败", e);
            return new Dto(false);
        }
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        logger.info("------没有权限-------");
        return "403";
    }
}
