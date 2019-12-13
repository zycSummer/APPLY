package com.jet.cloud.jetsndcxqyapply.service.user;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplication;
import com.jet.cloud.jetsndcxqyapply.entity.UserRole;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSySPoints;

import java.util.List;
import java.util.Map;

public interface DistrictCommitteeService {
    // 汇总页面
    Dto getAllDistrictCommittee(Map<String, Object> map);

    //查询称号列表
    Dto getTbSysTitle();

    // 保存授予编号
    Dto updateDistrictCommitteeTitle(List<Map<String, Object>> TbSiteApplication, String titleId);

    // 复审页面
    Dto getAllReviewDistrictCommittee(Map<String, Object> map) throws Exception;

    //查询所有指标和标准值
    Dto getm(String siteId, String year);

    //复审保存
    Dto replaceTbSiteReviewIndexValueAndTbSiteReview(JSONObject jsonObject);

    //查询账户管理
    Dto getSystemUser(Map<String, Object> map);

    // 账户时限管理查询
    Dto getUserRole(Map<String, Object> map);

    // 账户时限管理修改
    Dto updateUserRole(UserRole userRole);

    // 查看系统提示
    Dto getSysPoints();

    // 保存系统提示
    Dto insertSysPoints(TbSySPoints tbSySPoints);

    // 系统日志查询
    Dto getSysLog(Map<String, Object> map);
}
