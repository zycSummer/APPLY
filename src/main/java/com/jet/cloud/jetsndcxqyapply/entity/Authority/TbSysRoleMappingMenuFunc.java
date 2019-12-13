package com.jet.cloud.jetsndcxqyapply.entity.Authority;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色-菜单关联表实体类
 */
@Data
@TableName("tb_sys_role_mapping_menu_func")
public class TbSysRoleMappingMenuFunc {
    @TableId
    private Integer seqId;
    private String roleId;
    private String meunId;
    private String menmo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
