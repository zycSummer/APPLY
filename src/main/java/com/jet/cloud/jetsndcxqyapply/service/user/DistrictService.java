package com.jet.cloud.jetsndcxqyapply.service.user;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;

import java.util.Map;

public interface DistrictService {
    // 即默认查询当前登录的镇（街道）账号所关联的具体的镇（街道）下的所有工业园的所有企业（已经提交自评的企业）
    Dto getAllDistrict(Map<String, Object> map);

    // 复审评定
    Dto districtReview(JSONObject jsonObject);

    // 复审测评保存
    Dto insertDistrictReview(JSONObject jsonObject);

    // 复审测评上报
    Dto updateDistrictReview(JSONObject jsonObject);
}
