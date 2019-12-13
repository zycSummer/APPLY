package com.jet.cloud.jetsndcxqyapply.service.user;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;

import java.util.Map;

/**
 * 镇(街道)service
 */
public interface StreetService {
    //查询当前登录的镇（街道）账号所关联的具体的镇（街道）下的工业园列表
    Dto getSysIndustrialPark(String streetId);

    // 即默认查询当前登录的镇（街道）账号所关联的具体的镇（街道）下的所有工业园的所有企业（已经提交自评的企业）
    Dto getTownStreet(Map<String, Object> map);

    //查询所有指标和标准值
    Dto getm(String siteId, Integer year);

    // 镇街道，区三方 提交方法
    Dto insertTbSiteApplicationIndexValue(JSONObject jsonObject);

    // 镇街道，区三方 提交方法
    Dto updateTbSiteApplicationIndexValue(JSONObject jsonObject);

    // 复审测评
    Dto streetReview(JSONObject jsonObject);

    // 复审测评保存
    Dto insertStreetReview(JSONObject jsonObject);

    // 复审测评上报
    Dto updateStreetReview(JSONObject jsonObject);
}
