package com.jet.cloud.jetsndcxqyapply.entity.district;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//企业年度复审指标分值表
@Data
@TableName("tb_site_review_index_value")
public class TbSiteReviewIndexValue {
    @TableId
    private Integer seqId;
    private String year;
    private String siteId;
    private String indexId;
    private String reviewValue;
    private String streetReviewValue;
    private String districtReviewValue;
}
