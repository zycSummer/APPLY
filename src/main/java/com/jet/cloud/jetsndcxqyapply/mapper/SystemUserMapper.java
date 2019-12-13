package com.jet.cloud.jetsndcxqyapply.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jet.cloud.jetsndcxqyapply.entity.*;
import com.jet.cloud.jetsndcxqyapply.entity.Authority.TbSysMenuFunc;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//系统用户的Mapper
@Component
public interface SystemUserMapper extends BaseMapper<SystemUser> {
    // 根据userId查询用户
    public SystemUser getUserByUserId(String id);

    public int insertUser(SystemUser systemUser);

    // 更新用户
    public int updateUser(SystemUser systemUser);

    //删除
    public int deleteUser(String userId);

    //获取全部用户角色
    public List<UserRole> getUserRoles();

    //获取根据roleId获取用户角色
    public UserRole getUserRolesByRoleId(String roleId);

    // 根据siteId查询企业
    public CompanySite getSiteBySiteId(String id);

    // 新增企业
    public int insertSite(CompanySite companySite);

    // 更新企业
    public int updateSite(CompanySite CompanySite);

    //删除
    public int deleteSite(String siteId);

    //查询企业类型
    public List<SysSiteType> getSysSiteType();

    //查询所属行业
    public List<SysIndustyType> getSysIndustyType();

    //查询所属镇(街道)
    public List<SysStreet> getSysStreet();

    //根据StreetId所属镇(街道)
    public SysStreet getSysStreetByStreetId(String streetId);

    //查询所属工业园
    public List<SysIndustrialPark> getSysIndustrialPark();

    //查询企业siteId
    public String getSiteId();

    // 根据userid 查询企业邮箱
    public Map<String, Object> getMailByUserId(String userId);

    //权限过滤
    public List<TbSysMenuFunc> getTbSysMenuFunc(String roleId);

    //权限过滤
    Integer updateUserTime(SystemUser systemUser);

}
