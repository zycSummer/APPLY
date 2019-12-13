package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//工业园实体类
@Data
@TableName("tb_sys_industrial_park")
public class SysIndustrialPark2 {
    @TableId
    private Integer seqId;

    private String parkId;

    @TableField(value = "park_name")
    private String name;

    private String streetId;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;

    @TableField(exist = false)
    private String value;

}
