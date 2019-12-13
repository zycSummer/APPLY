package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户类型entity
 */
@Data
@TableName("tb_sys_role")
public class UserRole {
    @TableId
    private Integer seqId;
    private String roleId;
    private String roleName;
    private String startDate;
    private String endDate;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
