package com.jet.cloud.jetsndcxqyapply.entity.Company;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//企业申报附件表实体类
@Data
@TableName("tb_site_application_appendix")
public class TbSiteApplicationAppendix {
    @TableId
    private Integer seqId;
    private String siteId;
    private String appendixType;
    private String path;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
    private String review;
    private String fileName;
}

