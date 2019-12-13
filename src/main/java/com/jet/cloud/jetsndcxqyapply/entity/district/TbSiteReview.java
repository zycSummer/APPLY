package com.jet.cloud.jetsndcxqyapply.entity.district;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//企业年度复审信息表
@Data
@TableName("tb_site_review")
public class TbSiteReview {
    @TableId
    private Integer seqId;
    private String year;
    private String siteId;
    private String reviewResult;
    private String reviewUserId;
    private String reviewTime;
    private String streetReviewResult;
    private String districtReviewResult;
    private String streetReviewTime;
    private String districtReviewTime;
    private String streetReviewUserId;
    private String districtReviewUserId;
}
