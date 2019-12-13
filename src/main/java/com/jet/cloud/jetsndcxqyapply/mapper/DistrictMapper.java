package com.jet.cloud.jetsndcxqyapply.mapper;


import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//区三方接口类
@Component
public interface DistrictMapper {
    // 区三方办公室页面展示
    List<Map<String, Object>> getAllDistrict(Map<String, Object> map);

    // 查询总条数
    Integer getDistrictSum(Map<String, Object> map);

    // 复审评定
    List<Map<String, Object>> districtReview(Map<String, Object> map);

    Integer sumDistrictReview(Map<String, Object> map);

    // 通过year，siteId，indexId查询
    TbSiteReviewIndexValue getTbSiteReviewIndexValue(@Param(value = "year") String year, @Param(value = "siteId") String siteId, @Param(value = "indexId") String indexId);

    // 复审评定保存
    Integer insertDistrictReview(List<TbSiteReviewIndexValue> tbSiteReviewIndexValue);
}
