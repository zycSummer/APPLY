package com.jet.cloud.jetsndcxqyapply.service.user.impl;

import com.jet.cloud.jetsndcxqyapply.common.AES;
import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.entity.UserRole;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.service.user.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

//用户service
@Service
public class UserSeriviceImpl implements UserService {
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private CompanySiteMapper companySiteMapper;

    // 根据userId查询用户
    @Override
    public Dto getUserByUserId(String id) {
        SystemUser user = systemUserMapper.getUserByUserId(id);
        if (user == null) {
            return new Dto(false);
        }
        return new Dto(user);
    }

    // 新增用户
    @Override
    public Dto insertUser(SystemUser systemUser) {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
            String roleId = systemUser.getRoleId();
//            String userId = systemUser.getUserId();
            if (StringUtils.isEmpty(roleId)) {
                return new Dto(false, "参数无效", null);
            }
            if ("1001".equals(roleId)) {
                systemUser.setSiteId(getSiteId());
                systemUser.setRegistrationTime(DateUtils.dateFormatYYMMDDHHmmss());
            }

//            SystemUser user = systemUserMapper.getUserByUserId(userId);
//            if (user != null) {
//                // 存在就更新
//                systemUser.setLastLoginTime(DateUtils.dateFormatYYMMDDHHmmss());
//                systemUser.setLastLoginIp(InetAddress.getLocalHost().getHostAddress());
//                systemUser.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
//                systemUser.setUpdateUserId(session.getId());
//                systemUserMapper.updateUser(systemUser);
//            } else {
            //没有就插入
            String userPwd = systemUser.getUserPwd();
            String pwd = AES.encrypt(userPwd);
            systemUser.setUserPwd(pwd);
            systemUser.setLastLoginTime(DateUtils.dateFormatYYMMDDHHmmss());
            systemUser.setLastLoginIp(InetAddress.getLocalHost().getHostAddress());
            systemUser.setCreateUserId(userId);
            systemUser.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
            systemUser.setUpdateUserId(userId);
            systemUser.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            int rs = systemUserMapper.insertUser(systemUser);
            if (rs < 1) {
                return new Dto(false, "插入失败", null);
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Dto(null);
    }

    //查找全部系统角色
    @Override
    public Dto getUserRoles() {
        List<UserRole> userRoles = systemUserMapper.getUserRoles();
        if (userRoles != null && !userRoles.isEmpty()) ;
        return new Dto(userRoles);
    }

    // 更新用户
    @Override
    public Dto updateUser(SystemUser systemUser) {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
            SystemUser user = systemUserMapper.getUserByUserId(systemUser.getUserId());
            String userName = systemUser.getUserName();
            if (!StringUtils.isEmpty(userName)) {
                user.setUserName(userName);
            }
            user.setUserPwd(AES.encrypt(systemUser.getUserPwd()));
            user.setLastLoginTime(DateUtils.dateFormatYYMMDDHHmmss());
            user.setLastLoginIp(InetAddress.getLocalHost().getHostAddress());
            user.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            user.setRoleId(systemUser.getRoleId());
            user.setUpdateUserId(userId);
            String streetId = systemUser.getStreetId();
            if (streetId != null) {
                user.setStreetId(streetId);
            }
            user.setMemo(systemUser.getMemo());
            int i = systemUserMapper.updateUser(user);
            if (i < 1) {
                return new Dto(false, "更新失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Dto(false, "更新失败", e);
        }
        return new Dto(null);
    }

    @Override
    public Dto deleteUser(String id) {
        int i = systemUserMapper.deleteUser(id);
        if (i > 0) {
            return new Dto(true, "删除成功", i);
        } else {
            return new Dto(false, "删除失败", null);
        }
    }

    // 更新用户密码
    @Override
    public Dto updateUserByPwd(String oldPwd, String newPwd) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser user = systemUserMapper.getUserByUserId(userId);
        String userPwd = user.getUserPwd();//用户原先密码
        if (!userPwd.equals(AES.encrypt(oldPwd))) {
            return new Dto(false, "与原先密码不符", oldPwd);
        }
        user.setUserPwd(AES.encrypt(newPwd));
        systemUserMapper.updateUser(user);
        return new Dto(true, "更新成功", null);
    }

    @Override
    public Dto updateUserTime(SystemUser systemUser) {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
            SystemUser user = systemUserMapper.getUserByUserId(systemUser.getUserId());
            String userName = systemUser.getUserName();
            if (!StringUtils.isEmpty(userName)) {
                user.setUserName(userName);
            }
            user.setUserPwd(systemUser.getUserPwd());
            user.setLastLoginTime(DateUtils.dateFormatYYMMDDHHmmss());
            user.setLastLoginIp(InetAddress.getLocalHost().getHostAddress());
            user.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            user.setRoleId(systemUser.getRoleId());
            user.setUpdateUserId(userId);
            String streetId = systemUser.getStreetId();
            if (streetId != null) {
                user.setStreetId(streetId);
            }
            user.setMemo(systemUser.getMemo());
            int i = systemUserMapper.updateUserTime(user);
            if (i < 1) {
                return new Dto(false, "更新失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Dto(false, "更新失败", e);
        }
        return new Dto(null);
    }

    // 根据siteId查询用户
    @Override
    public Dto getSiteBySiteId(String id) {
        CompanySite siteBySiteId = systemUserMapper.getSiteBySiteId(id);
        return new Dto(siteBySiteId);
    }

    // 新增企业
    @Override
    public Dto insertSite(Map<String, Object> map) {
        try {
            SystemUser systemUser = new SystemUser();
            systemUser.setUserId(StringHelper.nvl(map.get("userId"), ""));//用户id
            systemUser.setUserName(StringHelper.nvl(map.get("userName"), ""));//用户名
            systemUser.setUserPwd(StringHelper.nvl(map.get("userPwd"), ""));//密码
            systemUser.setRoleId("1001");//所属角色id
            systemUser.setSiteId(getSiteId());//所属企业
            systemUser.setRegistrationTime(StringHelper.nvl(map.get("registrationTime"), ""));//注册时间
            systemUser.setMemo(StringHelper.nvl(map.get("memo"), ""));//备注
            systemUser.setCreateUserId(StringHelper.nvl(map.get("userId"), ""));
            systemUser.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
            systemUser.setUpdateUserId(StringHelper.nvl(map.get("userId"), ""));
            systemUser.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            Dto dto = insertUser(systemUser);
            //设置企业siteId(8位)
            CompanySite companySite = new CompanySite();
            companySite.setSiteId(getSiteId());
            companySite.setSiteName(StringHelper.nvl(map.get("siteName"), ""));
            String orgCode = StringHelper.nvl(map.get("orgCode"), "");
            List<CompanySite> codeByYear = companySiteMapper.getOrgCodeByYear();
            for (CompanySite c : codeByYear) {
                if (orgCode.equals(c.getOrgCode())) {
                    return new Dto(false, "当年已经申请过的组织机构代码不能进行申报", orgCode);
                }
            }
            companySite.setOrgCode(StringHelper.nvl(map.get("orgCode"), ""));
            companySite.setSiteTypeId(StringHelper.nvl(map.get("siteTypeId"), ""));
            companySite.setIndustryTypeId(StringHelper.nvl(map.get("industyTypeId"), ""));
            companySite.setStreetId(StringHelper.nvl(map.get("streetId"), ""));
            companySite.setParkId(StringHelper.nvl(map.get("parkId"), ""));
            companySite.setAddr(StringHelper.nvl(map.get("addr"), ""));
            companySite.setApplicant(StringHelper.nvl(map.get("applicant"), ""));
            companySite.setContacts(StringHelper.nvl(map.get("contacts"), ""));
            companySite.setTel(StringHelper.nvl(map.get("tel"), ""));
            companySite.setMobile(StringHelper.nvl(map.get("mobile"), ""));
            companySite.setMail(StringHelper.nvl(map.get("mail"), ""));
            companySite.setBusinessLicense(StringHelper.nvl(map.get("businessLicense"), ""));
            companySite.setMemo(StringHelper.nvl(map.get("memo"), ""));
            companySite.setCreateUserId(StringHelper.nvl(map.get("userId"), ""));
            companySite.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
            companySite.setUpdateUserId(StringHelper.nvl(map.get("userId"), ""));
            companySite.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            int i = systemUserMapper.insertSite(companySite);
            if (i < 1) {
                return new Dto(false, "插入失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Dto(null);
    }

    // 更新企业
    @Override
    public Dto updateSite(CompanySite companySite) {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            companySite.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            companySite.setUpdateUserId(session.getAttribute("loginUser").toString());
            int i = systemUserMapper.updateSite(companySite);
            if (i < 1) {
                return new Dto(false, "更新失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Dto(null);
    }

    //查询企业类型
    @Override
    public Dto getSysSiteType() {
        return new Dto(systemUserMapper.getSysSiteType());
    }

    //查询所属行业
    @Override
    public Dto getSysIndustyType() {
        return new Dto(systemUserMapper.getSysIndustyType());
    }

    //查询所属镇(街道)
    @Override
    public Dto getSysStreet() {
        return new Dto(systemUserMapper.getSysStreet());
    }

    //查询所属工业园
    @Override
    public Dto getSysIndustrialPark() {
        return new Dto(systemUserMapper.getSysIndustrialPark());
    }

    @Override
    public String getSiteId() {
        return systemUserMapper.getSiteId();
    }

    // 根据userid查询企业邮箱
    @Override
    public Dto getMailByUserId(String userId) {
        Map<String, Object> mailByUserId = systemUserMapper.getMailByUserId(userId);
        if (mailByUserId == null) {
            return new Dto(false);
        }
        return new Dto(mailByUserId);
    }
}
