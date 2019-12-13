package com.jet.cloud.jetsndcxqyapply.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;
import java.util.Map;

//所属镇(街道)实体类
@Data
@TableName("tb_sys_street")
public class SysStreet {
    @TableId
    private Integer seqId;
    private String streetId;
    private String streetName;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;

    @TableField(exist = false)
    private List<SysIndustrialPark> children;
}
