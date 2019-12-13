package com.jet.cloud.jetsndcxqyapply.service.user;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplicationAppendix;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;

import java.util.List;

public interface CompanySiteService {
    /**
     * 如果当前用户的角色属于注册账号，则在进入申报自评页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户“当前不允许自评申报，允许自评申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。
     */
    Dto checkIsRegisterNo(String userId);

    /**
     * 如果当前用户（企业）没有提交过自评，展示自评申报页面；
     * 如果已经提交过，则提示用户“您已提交自评申报，请到申报进展页面查询进展”。
     */
    Dto checkIsCommit();

    //查询所有指标和标准值
    Dto getm(String flag);

    // 企业申报自评的保存方法
    Dto insertTbSiteApplicationIndexValue(JSONObject jsonObject);

    // 企业申报自评的提交方法
    Dto updateTbSiteApplicationIndexValue(JSONObject jsonObject);

    // 增加日志
    void insertTbSysLog(TbSysLog tbSysLog);

    //查询如果用户有没有提交自评申报,有的话就展示申报进展信息
    Dto checkIsCommit(String siteId, String indexId);

    //查询企业申报附件表
    Dto getTbSiteApplicationAppendix1();

    // 注册信息点击保存
    int updateSite(CompanySite company);

    // 校验企业是否有复审权限
    Dto checkCompanyReview(String siteId);

    // 企业复审点击保存
    Dto insertTbSiteApplicationAppendix(JSONObject jsonObject);

    // 删除附件
    Dto deleteTbSiteApplicationAppendix(String seqId);

    // 复审材料页面查看
    Dto getAllAppendix();
}