package com.jet.cloud.jetsndcxqyapply.controller.analysis;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.entity.Company.Muji;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.service.analysis.AnalysisIndexService;
import com.jet.cloud.jetsndcxqyapply.service.analysis.AnalysisService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 企业Controller
@Controller
public class AnalysisController {
    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AnalysisIndexService analysisIndexService;


    /**
     * 查询所有指标和标准值
     */
    @GetMapping("/Analysis/getm")
    @ResponseBody
    public Dto getm() {
        List<Muji> getm = companySiteMapper.getm(null);
        return new Dto(true, "查询成功", getm);
    }

    /**
     * 企业类型
     */
    @GetMapping("/Analysis/getTbSySSiteType")
    @ResponseBody
    public Dto getTbSySSiteType() {
        Dto tbSySSiteType = analysisService.getTbSySSiteType();
        return new Dto(true, "查询成功", tbSySSiteType);
    }

    /**
     * 分析对象
     * 全区、具体镇(街道)、具体工业园 3级树状结构展示（多选、并且级联选择）
     */
    @GetMapping("/Analysis/getStreetAndIndustry")
    @ResponseBody
    public Dto getStreetAndIndustry() {
        Dto dto = analysisService.getStreetAndIndustry();
        return dto;
    }

    /**
     * 查询数据
     */
    @PostMapping("/Analysis/getAllScore")
    @ResponseBody
    @RequiresPermissions("/Analysis/getAllScore")
    public Dto getAllScore(@RequestBody JSONObject jsonObject) {
        Dto dto = analysisService.getAllScore(jsonObject);
        return dto;
    }

    /**
     * 查询得分分组统计
     */
    @PostMapping("/Analysis/getGroupScore")
    @ResponseBody
    @RequiresPermissions("/Analysis/getGroupScore")
    public Dto getGroupScore(@RequestBody JSONObject jsonObject) {
        Dto dto = analysisService.getGroupScore(jsonObject);
        return dto;
    }

    /**
     * 指标得分对比1
     */
    @PostMapping("/Analysis/getIndexScore1")
    @ResponseBody
    @RequiresPermissions("/Analysis/getIndexScore1")
    public Dto getIndexScore1(@RequestBody JSONObject jsonObject) {
        Dto dto = analysisIndexService.getIndexScore1(jsonObject);
        return dto;
    }

    /**
     * 指标得分对比2
     */
    @PostMapping("/Analysis/getIndexScore2")
    @ResponseBody
    @RequiresPermissions("/Analysis/getIndexScore2")
    public Dto getIndexScore2(@RequestBody JSONObject jsonObject) {
        Dto dto = analysisIndexService.getIndexScore2(jsonObject);
        return dto;
    }
}
