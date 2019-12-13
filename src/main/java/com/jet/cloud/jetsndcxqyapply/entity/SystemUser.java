package com.jet.cloud.jetsndcxqyapply.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统用户实体类
 */
@Data
@TableName("tb_sys_user")
public class SystemUser implements Serializable {
    @TableId
    private Integer seqId;
    private String userId;//用户id
    private String userName;//用户名
    //    @JsonIgnore
    private String userPwd;//密码
    private String roleId;//所属角色ID
    private String siteId;//所属企业id
    private String streetId;//所属镇(街道)id
    private String registrationTime;//注册时间
    private String lastLoginTime;//上次登录时间
    private String lastLoginIp;//上次登录的ip
    private String memo;//备注
    private String createUserId;//创建者id
    private String createTime;//创建时间
    private String updateUserId;//修改者id
    private String updateTime;//修改时间

    @TableField(exist = false)
    private String inputStr;
    @TableField(exist = false)
    private String roleName;
    @TableField(exist = false)
    private String siteName;
    @TableField(exist = false)
    private String streetName;

    public SystemUser() {
        this.seqId = 1;
    }
}