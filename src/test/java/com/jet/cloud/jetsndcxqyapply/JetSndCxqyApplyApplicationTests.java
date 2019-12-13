package com.jet.cloud.jetsndcxqyapply;

import com.jet.cloud.jetsndcxqyapply.service.mail.MailService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class JetSndCxqyApplyApplicationTests {

    @Autowired
    private MailService mailService;

    @Value("${spring.mail.username}")
    private String from;

    //测试邮件发送是否成功
    @Test
    public void sendHtmlMail() throws Exception {
        String checkCode = String.valueOf(new Random().nextInt(899999) + 100000);
//        mailService.sendSimpleMail(from, "您的注册验证码为", checkCode);
    }
}

