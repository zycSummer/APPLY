package com.jet.cloud.jetsndcxqyapply.entity.Company;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 指标信息表实体类
 */
@Data
@TableName("tb_index")
public class TbIndex {
    @TableId
    private Integer seqId;
    private String indexId;
    private String indexName;
    private String parentId;
    private String valueType;//分值类型
    private double min;//最小值
    private double max;//最大值
    @TableField("enum")
    private String enum1;
    private String vetoCondition;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
