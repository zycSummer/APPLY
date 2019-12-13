package com.jet.cloud.jetsndcxqyapply.entity.Authority;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统菜单功能表实体类
 */
@Data
@TableName("tb_sys_menu_func")
public class TbSysMenuFunc {
    @TableId
    private Integer seqId;
    private String meunId;
    private String funcName;
    private String url;
    private String method;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
