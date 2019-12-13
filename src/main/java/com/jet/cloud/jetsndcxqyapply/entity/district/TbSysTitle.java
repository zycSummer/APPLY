package com.jet.cloud.jetsndcxqyapply.entity.district;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 称号列表实体类
 */
@Data
@TableName("tb_sys_title")
public class TbSysTitle {
    @TableId
    private Integer seqId;
    private String titleId;
    private String titleName;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
