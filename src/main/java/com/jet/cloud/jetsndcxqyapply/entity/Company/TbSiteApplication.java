package com.jet.cloud.jetsndcxqyapply.entity.Company;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//企业申报信息表实体类
@Data
@TableName("tb_site_application")
public class TbSiteApplication {
    @TableId
    private Integer seqId;
    private String siteId;// 企业id
    private String selfUserId; // 自评人id
    private String selfSaveTime; // 自评保存时间
    private String selfSubmitTime;//自评提交时间
    private String streetSuggestion;//测评建议
    private String streetUserId;//测评人Id
    private String streetSaveTime;//测评保存时间
    private String streetSubmitTime;//测评上报时间
    private String districtSuggestion;//评定建议
    private String districtUserId;//评定人id
    private String districtSaveTime;//评定保存时间
    private String districtSubmitTime;//评定上报时间
    private String districtCommitteeTitle;//授予称号
    private String districtCommitteeUserId;//授予人id
    private String districtCommitteeTime;//授予时间
}
