package com.jet.cloud.jetsndcxqyapply.entity.log;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_sys_log")
public class TbSysLog {
    @TableId
    private Integer seqId;
    private String operatorId;
    private String operateTime;
    private String operateIp;
    private String operateContent;
    private String operateResult;
    private String url;
    private String method;
    private String meunId;
    private String funcId;
    private String memo;
}
