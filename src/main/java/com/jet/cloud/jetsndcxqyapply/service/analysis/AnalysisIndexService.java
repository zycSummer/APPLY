package com.jet.cloud.jetsndcxqyapply.service.analysis;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;


/**
 * 指标得分分析
 */
public interface AnalysisIndexService {
    /**
     * 指标得分对比1
     */
    Dto getIndexScore1(JSONObject jsonObject);

    /**
     * 指标得分对比2
     */
    Dto getIndexScore2(JSONObject jsonObject);
}
