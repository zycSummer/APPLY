package com.jet.cloud.jetsndcxqyapply.entity.district;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//系统提示表
@Data
@TableName("tb_sys_prompt")
public class TbSySPoints {
    @TableId
    private Integer seqId;
    private String points;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
