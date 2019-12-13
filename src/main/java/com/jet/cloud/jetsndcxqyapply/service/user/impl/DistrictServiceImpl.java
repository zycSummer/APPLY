package com.jet.cloud.jetsndcxqyapply.service.user.impl;


import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplicationIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReview;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictCommitteeMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.StreetMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.DistrictService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistrictServiceImpl implements DistrictService {
    Logger logger = LoggerFactory.getLogger(StreetServiceImpl.class);

    @Autowired
    private DistrictMapper districtMapper;

    @Autowired
    private StreetMapper streetMapper;

    @Autowired
    private DistrictCommitteeMapper districtCommitteeMapper;

    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Override
    public Dto getAllDistrict(Map<String, Object> map) {
        List<Map<String, Object>> allDistrict = districtMapper.getAllDistrict(map);
        Integer districtSum = districtMapper.getDistrictSum(map);
        if (allDistrict != null && !allDistrict.isEmpty()) {
            for (Map<String, Object> district : allDistrict) {
                //status 0是未上报,1是已上报
                String districtSubmitTime = StringHelper.nvl(district.get("districtSubmitTime"), "");
                if (districtSubmitTime != "") {
                    district.put("status", 1);
                } else {
                    district.put("status", 0);
                }
            }
            return new Dto(true, "查询成功", allDistrict, districtSum);
        }
        // 没有提交
        return new Dto(false, "没有企业", null);
    }

    @Override
    public Dto districtReview(JSONObject jsonObject) {
        try {
            List<String> siteIds = jsonObject.getObject("siteIds", List.class);
            String streetId = StringHelper.nvl(jsonObject.get("streetId"), "");
            String parkId = StringHelper.nvl(jsonObject.get("parkId"), "");
            String siteName = StringHelper.nvl(jsonObject.get("siteName"), "");
            Integer page = jsonObject.getObject("page", Integer.class);
            Integer limit = jsonObject.getObject("limit", Integer.class);
            Integer year = Integer.parseInt(jsonObject.get("year").toString());
            Map<String, Object> map = new HashMap<>();
            map.put("siteIds", siteIds);
            map.put("streetId", streetId);
            map.put("parkId", parkId);
            map.put("siteName", siteName);
            map.put("year", year);
            map.put("year1", year - 1);
            Integer page1 = (page - 1) * limit;
            map.put("page", page1);
            map.put("limit", limit);
            List<Map<String, Object>> list = districtMapper.districtReview(map);
            for (Map<String, Object> map1 : list) {
                if ("1001".equals(map1.get("districtCommitteeTitle"))) {
                    map1.put("districtCommitteeTitle", "和谐先进企业");
                } else if ("1002".equals(map1.get("districtCommitteeTitle"))) {
                    map1.put("districtCommitteeTitle", "和谐先进企业");
                }
                List<Map<String, Object>> seqIds = streetMapper.getSeqIdsBySiteId(map1.get("siteId").toString());
                map1.put("file", seqIds);
            }
            Integer integer = districtMapper.sumDistrictReview(map);
            return new Dto(true, "成功", list, integer);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Dto(false, "失败", e.getMessage());
        }
    }

    @Override
    public Dto insertDistrictReview(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        List<Map<String, Object>> list = jsonObject.getObject("tableData", List.class);
        System.out.println(list);
        String siteId = jsonObject.getObject("siteId", String.class);
        Integer year = jsonObject.getObject("year", Integer.class);
        try {
            List<TbSiteReviewIndexValue> listSiteReviewIndexValue = new ArrayList<>();
            for (Map<String, Object> map : list) {
                String indexId = StringHelper.nvl(map.get("indexId"), "");
                String streetReviewValue = StringHelper.nvl(map.get("streetReviewValue"), "");//镇街道年度复审值
                String districtReviewValue = StringHelper.nvl(map.get("districtReviewValue"), "");//区三方年度复审值
                TbSiteReviewIndexValue tbSiteReviewIndexValue = districtMapper.getTbSiteReviewIndexValue(year.toString(), siteId, indexId);
                tbSiteReviewIndexValue.setDistrictReviewValue(districtReviewValue);
                listSiteReviewIndexValue.add(tbSiteReviewIndexValue);
            }
            Integer integer = districtMapper.insertDistrictReview(listSiteReviewIndexValue);

            TbSiteReview tbSiteReview = new TbSiteReview();
            tbSiteReview.setYear(year.toString());
            tbSiteReview.setSiteId(siteId);
            TbSiteReview siteReview = streetMapper.getTbSiteReview(tbSiteReview);
            siteReview.setDistrictReviewTime(DateUtils.dateFormatYYMMDDHHmmss());
            siteReview.setDistrictReviewUserId(userId);
            streetMapper.updateStreetReview(siteReview);

            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("复审评定保存");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/district/insertDistrictReview");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1015");
            tbSysLog.setFuncId("10020");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("复审评定保存");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/district/insertDistrictReview");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1015");
                tbSysLog.setFuncId("10020");
                tbSysLog.setMemo(e.getMessage());
            } catch (Exception e1) {
                logger.info("insertDistrictReview---e1={}", e1);
            }
            logger.info("insertDistrictReview---e={}", e);
            return new Dto(false, "失败", e.getMessage());
        }
    }

    public void insertTbSysLog(TbSysLog tbSysLog) {
        companySiteMapper.insertLog(tbSysLog);
    }

    @Override
    public Dto updateDistrictReview(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        try {
            Integer year = jsonObject.getObject("year", Integer.class);
            List<String> list = jsonObject.getObject("siteIds", List.class);
            for (String s : list) {
                TbSiteReview tbSiteReview = new TbSiteReview();
                tbSiteReview.setYear(year.toString());
                tbSiteReview.setSiteId(s);
                TbSiteReview siteReview = streetMapper.getTbSiteReview(tbSiteReview);
                if (siteReview != null) {
                    siteReview.setDistrictReviewResult("1");
                    siteReview.setStreetReviewResult("1");
                    siteReview.setDistrictReviewTime(DateUtils.dateFormatYYMMDDHHmmss());
                    siteReview.setDistrictReviewUserId(userId);
                    streetMapper.updateStreetReview(siteReview);
                }
            }
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("复审评定上报");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/district/updateDistrictReview");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1015");
            tbSysLog.setFuncId("10021");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("复审评定上报");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/district/updateDistrictReview");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1015");
                tbSysLog.setFuncId("10021");
                tbSysLog.setMemo(e.getMessage());
            } catch (Exception e1) {
                logger.info("updateDistrictReview---e1={}", e1);
            }
            logger.info("updateDistrictReview---e={}", e);
            return new Dto(false, "失败", e.getMessage());
        }
    }
}
