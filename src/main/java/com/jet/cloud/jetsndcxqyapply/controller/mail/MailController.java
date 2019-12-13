package com.jet.cloud.jetsndcxqyapply.controller.mail;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.controller.user.UserController;
import com.jet.cloud.jetsndcxqyapply.service.mail.MailService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

/**
 * 邮件Controller
 */
@Controller
public class MailController {
    @Autowired
    private MailService mailService;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/getCheckCode")
    @ResponseBody
    public Dto getCheckCode(@RequestParam(value = "email") String email,HttpSession session) {
       /* Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();*/
        String checkCode = String.valueOf(new Random().nextInt(899999) + 100000);
        String message = "您的注册验证码为：" + checkCode;
        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            logger.info("您的注册验证码为：message={}", message);
            Dto dto = mailService.sendSimpleMail(email, "注册验证码", message, session, checkCode);
            return dto;
        } catch (Exception e) {
            logger.info("注册验证码失败,e={}", e);
            return null;
        }
    }

    /**
     * 校验验证码
     */
    @RequestMapping(value = "/mail/checkVerify", method = RequestMethod.POST)
    @ResponseBody
    public Dto checkVerify(@RequestBody JSONObject requestMap, HttpSession session) {
        try {
            //从session中获取随机数
            String inputStr = StringHelper.nvl(requestMap.get("inputString"), "");
            String randomcodevalication = (String)session.getAttribute("RANDOMCODEVALICATION");
            if (randomcodevalication == null) {
                return new Dto(false);
            }
            if (randomcodevalication.equals(inputStr)) {
                return new Dto(true);
            } else {
                return new Dto(false);
            }
        } catch (Exception e) {
            logger.error("验证码校验失败", e);
            return new Dto(false);
        }
    }
}