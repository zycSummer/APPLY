package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//所属行业实体类
@Data
@TableName("tb_sys_industry_type")
public class SysIndustyType {
    @TableId
    private Integer seqId;
    private String typeId;
    private String typeName;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
