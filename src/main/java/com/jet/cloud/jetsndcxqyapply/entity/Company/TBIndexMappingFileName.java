package com.jet.cloud.jetsndcxqyapply.entity.Company;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 朱一成
 * @create 2019/5/6 9:15
 * @desc 指标信息关联文件名表实体类
 */

@Data
public class TBIndexMappingFileName {
    @TableId
    private Integer seqId;
    private String indexId;
    private String fileName;
    private String memo;
    private String createUserId;
    private String createTime;
    private String updateUserId;
    private String updateTime;
}
