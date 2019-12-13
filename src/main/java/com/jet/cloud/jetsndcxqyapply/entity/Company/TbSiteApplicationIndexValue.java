package com.jet.cloud.jetsndcxqyapply.entity.Company;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 企业申报指标分值表实体类
 */
@Data
@TableName("tb_site_application_index_value")
public class TbSiteApplicationIndexValue {
    @TableId
    private Integer seqId;
    private String siteId;
    private String indexId;
    private String selfValue;
    private String streetValue;
    private String districtValue;
    private String fileNames;
}
