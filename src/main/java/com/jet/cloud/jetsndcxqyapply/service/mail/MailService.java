package com.jet.cloud.jetsndcxqyapply.service.mail;

import com.jet.cloud.jetsndcxqyapply.common.Dto;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * 邮件sevice层
 */
@Service("mailService")
public class MailService {
    public MailService() {
        super();
    }

    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public Dto sendSimpleMail(String to, String title, String content, HttpSession session, String checkCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
        logger.info("邮件发送成功");
        session.removeAttribute("RANDOMCODEVALICATION");
        session.setAttribute("RANDOMCODEVALICATION", checkCode);
        return new Dto(null);
    }
}
