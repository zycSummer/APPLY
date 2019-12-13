package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//企业类型实体类
@Data
@TableName("tb_sys_site_type")
public class SysSiteType {
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
