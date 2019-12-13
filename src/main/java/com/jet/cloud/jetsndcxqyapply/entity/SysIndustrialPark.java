package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//工业园实体类
@Data
@TableName("tb_sys_industrial_park")
public class SysIndustrialPark {
    @TableId
    private Integer seqId;
    private String parkId;
    private String parkName;
    private String streetId;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
