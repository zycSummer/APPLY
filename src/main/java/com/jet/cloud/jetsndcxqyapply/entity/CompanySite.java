package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 企业实体类
 */
@Data
@TableName("tb_site")
public class CompanySite {
    @TableId
    private Integer seqId;
    private String siteId;
    private String siteName;
    private String orgCode;
    private String siteTypeId;
    private String industryTypeId;
    private String streetId;
    private String parkId;
    private String addr;
    private String applicant;
    private String contacts;
    private String tel;
    private String mobile;
    private String mail;
    private String businessLicense;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
