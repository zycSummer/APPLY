package com.jet.cloud.jetsndcxqyapply.service.analysis;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;

import java.util.Map;

/*
 * 分析service
 */
public interface AnalysisService {
    /**
     * 分析对象查找
     * 全区、具体镇(街道)、具体工业园 3级树状结构展示（多选、并且级联选择）
     */
    Dto getStreetAndIndustry();

    /**
     * 查询企业类型
     *
     * @return
     */
    Dto getTbSySSiteType();

    /**
     * 查询得分分布
     */
    Dto getAllScore(JSONObject jsonObject);

    /**
     * 查询得分分组统计
     */
    Dto getGroupScore(JSONObject jsonObject);

}
