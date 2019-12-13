package com.jet.cloud.jetsndcxqyapply.service.user;

import com.jet.cloud.jetsndcxqyapply.entity.*;
import com.jet.cloud.jetsndcxqyapply.common.Dto;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户service
 */
public interface UserService {
    // 根据userId查询用户
    public Dto getUserByUserId(String id);

    // 新增用户
    public Dto insertUser(SystemUser systemUser);

    // 查找全部系统角色
    public Dto getUserRoles();

    // 更新用户
    public Dto updateUser(SystemUser systemUser);

    // 删除用户
    public Dto deleteUser(String id);

    // 根据siteId查询企业
    public Dto getSiteBySiteId(String id);

    // 新增企业
    public Dto insertSite(Map<String, Object> map);

    // 更新企业
    public Dto updateSite(CompanySite CompanySite);

    //查询企业类型
    public Dto getSysSiteType();

    //查询所属行业
    public Dto getSysIndustyType();

    //查询所属镇(街道)
    public Dto getSysStreet();

    //查询所属工业园
    public Dto getSysIndustrialPark();

    //查询企业siteId
    public String getSiteId();

    // 根据userid 查询企业邮箱
    public Dto getMailByUserId(String userId);

    // 更新用户密码
    public Dto updateUserByPwd(String oldPwd, String newPwd);

    // 更新用户密码
    public Dto updateUserTime(SystemUser systemUser);
}
