package com.jet.cloud.jetsndcxqyapply.user;

import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.controller.user.UserController;
import com.jet.cloud.jetsndcxqyapply.entity.Authority.TbSysMenuFunc;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@Ignore
@SpringBootTest
public class UserTests {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController controller;

    //根据用户id查找用户
    @Test
    public void getUserById() {
        SystemUser systemUser = systemUserMapper.getUserByUserId("ljkj");
        System.out.println(systemUser);
    }

    /*@Test
    public void insertUser() throws Exception {
        SystemUser systemUser = new SystemUser();
        systemUser.setUserId("zyc1");
        systemUser.setUserName("朱一成");
        systemUser.setUserPwd("1111");
        systemUser.setRoleId("1001");
        systemUser.setSiteId("1003");
        systemUser.setStreetId("1003");
        systemUser.setRegistrationTime(DateUtils.dateFormatYYMMDDHHmmss());
        systemUser.setLastLoginTime(DateUtils.dateFormatYYMMDDHHmmss());
        systemUser.setLastLoginIp(InetAddress.getLocalHost().getHostAddress());
        systemUser.setMemo("1");
        systemUser.setCreateUserId("zyc");
        systemUser.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
        systemUser.setUpdateUserId("1111");
        systemUser.setUserName(DateUtils.dateFormatYYMMDDHHmmss());
        int i = systemUserMapper.insertUser(systemUser);
        System.out.println("成功" + i);
    }
*/
    @Test
    public void getUserRoles() {
        Dto ljkj = controller.getSysIndustyType();
        System.out.println(ljkj);

    }

    @Test
    public void updateUser() throws Exception {
        SystemUser qmjd = systemUserMapper.getUserByUserId("qmjd");
        qmjd.setLastLoginTime(DateUtils.dateFormatYYMMDDHHmmss());
        qmjd.setLastLoginIp(InetAddress.getLocalHost().getHostAddress());
        Dto dto = userService.updateUser(qmjd);
        System.out.println(dto);
    }

    @Test
    public void getMailByUserId() {
        Map<String, Object> qmjd = systemUserMapper.getMailByUserId("qmjd");
        System.out.println(qmjd);
    }

    @Test
    public void getMailByUserId1() {
        SystemUser user = systemUserMapper.getUserByUserId("zycTest");
        Dto siteBySiteId = userService.getSiteBySiteId(user.getSiteId());
        System.out.println(siteBySiteId);
    }

    @Test
    public void getMailByUserId2() {
        Integer size = 4;
        System.out.println(!(size.equals(4)));
    }

    @Test
    public void getTbSysMenuFunc() {
        List<TbSysMenuFunc> tbSysMenuFunc = systemUserMapper.getTbSysMenuFunc("1001");
        System.out.println(tbSysMenuFunc);
    }


}
