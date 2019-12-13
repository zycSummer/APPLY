package com.jet.cloud.jetsndcxqyapply.controller.file;

import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.controller.user.UserController;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplicationAppendix;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 多文件上传Controller
 */

@Controller
public class FileUploadController {
    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${spring.servlet.multipart.location}")
    private String UPLOADED_FOLDER;

    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @PostMapping("/file/moreFileUpload")
    @ResponseBody
    public Dto moreFileUpload(@RequestParam("file") MultipartFile[] files) {
        System.out.println(files.length);
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        logger.info("userId={}", userId);
        for (MultipartFile file : files) {
            if (userId != "") {
                SystemUser userByUserId = systemUserMapper.getUserByUserId(userId);
                logger.info("file.getOriginalFilename()={}", file.getOriginalFilename());
                TbSiteApplicationAppendix tbSiteApplicationAppendix = companySiteMapper.checkIsExistAppendix(userByUserId.getSiteId(), file.getOriginalFilename());
                if (tbSiteApplicationAppendix != null) {
                    return new Dto(false, "该企业存在重复文件", tbSiteApplicationAppendix.getSiteId());
                }
            }
        }
        if (files.length == 0) {
            return new Dto(false);
        }
        Path path = null;
        Map<String, Object> map = null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                byte[] bytes = file.getBytes();
                path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                map = new HashMap<>();
                String string = path.toString();
                map.put("path", string);
                map.put("appendixType", file.getOriginalFilename());
                map.put("fileName", file.getOriginalFilename());
                list.add(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Dto(true, "上传成功", list);
    }
}
