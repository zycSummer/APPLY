package com.jet.cloud.jetsndcxqyapply.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.Company.*;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.SysIndustrialPark;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReview;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictCommitteeMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.StreetMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.StreetService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.util.*;

/**
 * 镇(街道)serviceImpl
 */
@Service
public class StreetServiceImpl implements StreetService {
    Logger logger = LoggerFactory.getLogger(StreetServiceImpl.class);

    @Autowired
    private StreetMapper streetMapper;

    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private DistrictCommitteeMapper districtCommitteeMapper;

    String[] strarry = new String[]{"一级指标", "二级指标", "三级指标", "四级指标", "五级指标", "六级指标", "七级指标"};

    @Override
    public Dto getSysIndustrialPark(String streetId) {
        List<SysIndustrialPark> park = streetMapper.getSysIndustrialPark(streetId);
        if (park != null && !park.isEmpty()) {
            return new Dto(true, "成功", park);
        } else {
            return new Dto(false, "没有工业园", null);
        }
    }

    // 即默认查询当前登录的镇（街道）账号所关联的具体的镇（街道）下的所有工业园的所有企业（已经提交自评的企业）
    @Override
    public Dto getTownStreet(Map<String, Object> map) {
        List<Map<String, Object>> townStreet = streetMapper.getTownStreet(map);
        for (Map<String, Object> stringObjectMap : townStreet) {
            List<Map<String, Object>> seqIds = streetMapper.getSeqIdsBySiteIdZero(stringObjectMap.get("siteId").toString());
            stringObjectMap.put("file", seqIds);
        }
        Integer townStreetSum = streetMapper.getTownStreetSum(map);
        if (townStreet != null && !townStreet.isEmpty()) {
            for (Map<String, Object> mapStreet : townStreet) {
                //status 0是未上报,1是已上报
                String streetSumbitTime = StringHelper.nvl(mapStreet.get("streetSumbitTime"), "");
                if (streetSumbitTime != "") {
                    mapStreet.put("status", 1);
                } else {
                    mapStreet.put("status", 0);
                }
            }
            return new Dto(true, "查询成功", townStreet, townStreetSum);
        }
        // 没有提交
        return new Dto(false, "没有企业", null);
    }

    @Override
    public Dto getm(String siteId, Integer year) {
        List<Muji> aa = companySiteMapper.getm(null);
        System.out.println(aa.toString());
        List<Map<String, Object>> list = new ArrayList<>();
        int what = getList(list, aa, 1, null, siteId, year);
        System.out.println(what);
        System.out.println(list.toString());
        String[] temparry = new String[what];
        temparry = Arrays.copyOf(strarry, what);
        List<String> listAll = Arrays.asList(temparry);
        System.out.println(new Dto(true, "查询成功", list, listAll));
        return new Dto(true, "查询成功", list, listAll);
    }

    /**
     * 镇街道，区三方 自评的保存方法
     */
    @Override
    public Dto insertTbSiteApplicationIndexValue(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        List<Map<String, Object>> list = jsonObject.getObject("tableData", List.class);
        String beforeSiteId = jsonObject.getObject("siteId", String.class);
        String streetSuggestion = jsonObject.getObject("streetSuggestion", String.class);
        String districtSuggestion = jsonObject.getObject("districtSuggestion", String.class);

        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        //只有角色是1001企业的时候才查的到，否则没有前端要传
        String siteId = systemUser.getSiteId();
        if (siteId == null || "".equals(siteId)) {
            siteId = beforeSiteId;
        }
        String roleId = systemUser.getRoleId();
        try {
            List<String> listIndex = new ArrayList<>();
            for (Map<String, Object> map : list) {
                String selfValue = StringHelper.nvl(map.get("selfValue"), "");//企业自评值
                String streetValue = StringHelper.nvl(map.get("streetValue"), "");//镇街道评分值
                String districtValue = StringHelper.nvl(map.get("districtValue"), "");//区三方评定值
                if ("Y".equals(selfValue) || "Y".equals(streetValue) || "Y".equals(districtValue)) {
                    String index3 = StringHelper.nvl(map.get("indexId"), "");
                    listIndex.add(index3);
                }
            }
            List<TbSiteApplicationIndexValue> listTbSiteApplicationIndexValue = new ArrayList<>();
            List<TbSiteApplication> listTbSiteApplication = new ArrayList<>();
            List<TbSiteApplicationAppendix> listTbSiteApplicationAppendix = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    String indexId = StringHelper.nvl(map.get("indexId"), "");
                    String selfValue = StringHelper.nvl(map.get("selfValue"), "");//企业自评值
                    String streetValue = StringHelper.nvl(map.get("streetValue"), "");//镇（街道）测评值
                    String districtValue = StringHelper.nvl(map.get("districtValue"), "");//区三方评定值

                    TbSiteApplicationIndexValue value = companySiteMapper.getTbSiteApplicationIndexValue(siteId, indexId);
                    if (value == null) {
                        TbSiteApplicationIndexValue t = new TbSiteApplicationIndexValue();
                        t.setSiteId(siteId);
                        t.setIndexId(indexId);
                        t.setSelfValue(selfValue);
                        t.setStreetValue(streetValue);
                        t.setDistrictValue(districtValue);
                        listTbSiteApplicationIndexValue.add(t);
                    } else {
                        value.setSelfValue(selfValue);
                        value.setStreetValue(streetValue);
                        value.setDistrictValue(districtValue);
                        listTbSiteApplicationIndexValue.add(value);
                    }
                }
                companySiteMapper.insertTbSiteApplicationIndexValue(listTbSiteApplicationIndexValue);
            }
            TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);
            if (application == null) {
                TbSiteApplication tb = new TbSiteApplication();
                tb.setSiteId(siteId);
                tb.setSelfUserId(userId);
                tb.setSelfSaveTime(DateUtils.dateFormatYYMMDDHHmmss());
                listTbSiteApplication.add(tb);
            } else {
                if ("1001".equals(roleId)) {
                    application.setSelfSaveTime(DateUtils.dateFormatYYMMDDHHmmss());
                }
                if ("1002".equals(roleId)) {
                    application.setStreetSuggestion(streetSuggestion);
                    application.setStreetUserId(userId);
                    application.setStreetSaveTime(DateUtils.dateFormatYYMMDDHHmmss());
                }
                if ("1003".equals(roleId)) {
                    application.setDistrictSuggestion(districtSuggestion);
                    application.setDistrictUserId(userId);
                    application.setDistrictSaveTime(DateUtils.dateFormatYYMMDDHHmmss());
                }
                listTbSiteApplication.add(application);
            }
            companySiteMapper.insertTbSiteApplication(listTbSiteApplication);

            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            String selfSubmitTime = application.getSelfSubmitTime();
            String streetSubmitTime = application.getStreetSubmitTime();
            String districtSubmitTime = application.getDistrictSubmitTime();
            if (selfSubmitTime != null && streetSubmitTime != null && districtSubmitTime == null) {
                tbSysLog.setOperateContent("区三方评定保存");
                tbSysLog.setMeunId("1006");
                tbSysLog.setFuncId("10006");
                tbSysLog.setUrl("/district/insertDistrict");
            } else if (selfSubmitTime != null && districtSubmitTime == null) {
                tbSysLog.setOperateContent("镇街道评定保存");
                tbSysLog.setMeunId("1005");
                tbSysLog.setFuncId("10005");
                tbSysLog.setUrl("/Street/insertStreet");
            }
            tbSysLog.setOperateResult("success");
            tbSysLog.setMethod("post");
            insertTbSysLog(tbSysLog);
            if (listIndex != null && !listIndex.isEmpty()) {
                return new Dto(true, "存在一票否决", listIndex);
            }
        } catch (Exception e) {
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(StringHelper.nvl(session.getAttribute("loginUser"), ""));
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());

                TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);
                String selfSubmitTime = application.getSelfSubmitTime();
                String streetSubmitTime = application.getStreetSubmitTime();
                String districtSubmitTime = application.getDistrictSubmitTime();
                if (selfSubmitTime != null && streetSubmitTime != null && districtSubmitTime == null) {
                    tbSysLog.setOperateContent("区三方评定保存");
                    tbSysLog.setMeunId("1006");
                    tbSysLog.setFuncId("10006");
                    tbSysLog.setUrl("/district/insertDistrict");
                } else if (selfSubmitTime != null && districtSubmitTime == null) {
                    tbSysLog.setOperateContent("镇街道评定保存");
                    tbSysLog.setUrl("/Street/insertStreet");
                    tbSysLog.setMeunId("1005");
                    tbSysLog.setFuncId("10004");
                }
                tbSysLog.setMethod("post");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setMethod("post");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
                e.printStackTrace();
                return new Dto(false, "保存失败", e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return new Dto(true, "保存成功", null);
    }

    @Override
    public Dto updateTbSiteApplicationIndexValue(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        //只有角色是1001企业的时候才查的到，否则没有前端要传
        String roleId = systemUser.getRoleId();
        List<Map<String, Object>> list = jsonObject.getObject("tableData", List.class);
        List<TbSiteApplication> listTbSiteApplication = new ArrayList<>();
        try {
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    String siteId = StringHelper.nvl(map.get("siteId"), "");
                    TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);
                    TbSiteApplication tbSiteApplication = companySiteMapper.checkIsCommit(siteId);
                    if ("1002".equals(roleId)) {
                        tbSiteApplication.setStreetSubmitTime(DateUtils.dateFormatYYMMDDHHmmss());
                    }
                    if ("1003".equals(roleId)) {
                        tbSiteApplication.setDistrictSubmitTime(DateUtils.dateFormatYYMMDDHHmmss());
                    }
                    listTbSiteApplication.add(tbSiteApplication);

                    TbSysLog tbSysLog = new TbSysLog();
                    tbSysLog.setOperatorId(userId);
                    tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                    tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());

                    String selfSubmitTime = application.getSelfSubmitTime();
                    String streetSubmitTime = application.getStreetSubmitTime();
                    String districtSubmitTime = application.getDistrictSubmitTime();
                    if (selfSubmitTime != null && streetSubmitTime != null && districtSubmitTime == null) {
                        tbSysLog.setOperateContent("区三方评定上报");
                        tbSysLog.setMeunId("1006");
                        tbSysLog.setFuncId("10006");
                        tbSysLog.setUrl("/district/insertDistrict");
                    } else if (selfSubmitTime != null && districtSubmitTime == null) {
                        tbSysLog.setOperateContent("镇街道评定上报");
                        tbSysLog.setUrl("/Street/insertStreet");
                        tbSysLog.setMeunId("1005");
                        tbSysLog.setFuncId("10004");
                    }
                    tbSysLog.setOperateResult("success");
                    tbSysLog.setMethod("post");
                    insertTbSysLog(tbSysLog);
                }
                companySiteMapper.insertTbSiteApplication(listTbSiteApplication);
            }
        } catch (Exception e) {
            try {
                if (list != null && !list.isEmpty()) {
                    for (Map<String, Object> map : list) {
                        String siteId = StringHelper.nvl(map.get("siteId"), "");
                        TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);

                        TbSysLog tbSysLog = new TbSysLog();
                        tbSysLog.setOperatorId(StringHelper.nvl(session.getAttribute("loginUser"), ""));
                        tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());

                        String selfSaveTime = application.getSelfSaveTime();
                        String streetSaveTime = application.getStreetSaveTime();
                        String districtSaveTime = application.getDistrictSaveTime();
                        if (selfSaveTime != null && streetSaveTime != null && districtSaveTime == null) {
                            tbSysLog.setOperateContent("区三方评定上报");
                            tbSysLog.setMeunId("1006");
                            tbSysLog.setFuncId("10007");
                            tbSysLog.setUrl("/district/updateDistrict");
                        } else if (selfSaveTime != null && districtSaveTime == null) {
                            tbSysLog.setOperateContent("镇街道评定上报");
                            tbSysLog.setUrl("/Street/updateStreet");
                            tbSysLog.setMeunId("1005");
                            tbSysLog.setFuncId("10005");
                        }
                        tbSysLog.setOperateResult("fail");
                        tbSysLog.setMethod("post");
                        tbSysLog.setMemo("");
                        insertTbSysLog(tbSysLog);
                        e.printStackTrace();
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Dto(false);
        }
        return new Dto(true, "提交成功", null);
    }

    // 复审测评
    @Override
    public Dto streetReview(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
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

            if (parkId != "") {
                map.put("parkId", parkId);
            } else {
                SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
                String streetId1 = systemUser.getStreetId();
                List<SysIndustrialPark> park = streetMapper.getSysIndustrialPark(streetId1);
                List<String> list = new ArrayList<>();
                for (SysIndustrialPark industrialPark : park) {
                    list.add(industrialPark.getParkId());
                }
                map.put("parkIds", list);
            }
            map.put("siteName", siteName);
            map.put("year", year);
            map.put("year1", year - 1);
            Integer page1 = (page - 1) * limit;
            map.put("page", page1);
            map.put("limit", limit);
            List<Map<String, Object>> list = streetMapper.streetReview(map);
            for (Map<String, Object> map1 : list) {
                if ("1001".equals(map1.get("districtCommitteeTitle"))) {
                    map1.put("districtCommitteeTitle", "和谐先进企业");
                } else if ("1002".equals(map1.get("districtCommitteeTitle"))) {
                    map1.put("districtCommitteeTitle", "和谐先进企业");
                }
                List<Map<String, Object>> seqIds = streetMapper.getSeqIdsBySiteId(map1.get("siteId").toString());
                map1.put("file", seqIds);
            }
            Integer integer = streetMapper.sumStreetReview(map);
            return new Dto(true, "成功", list, integer);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Dto(false, "失败", e.getMessage());
        }
    }

    @Override
    public Dto insertStreetReview(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        List<Map<String, Object>> list = jsonObject.getObject("tableData", List.class);
        System.out.println(list);
        String siteId = jsonObject.getObject("siteId", String.class);
        Integer year = jsonObject.getObject("year", Integer.class);
        List<Map<String, Object>> index = streetMapper.getIndex();
        try {
            List<TbSiteReviewIndexValue> listSiteReviewIndexValue = new ArrayList<>();
            for (Map<String, Object> tbIndex : index) {
                String indexIdAll = tbIndex.get("indexId").toString();
                TbSiteReviewIndexValue t = new TbSiteReviewIndexValue();
                t.setYear(year.toString());
                t.setSiteId(siteId);
                t.setIndexId(indexIdAll);
                TbSiteReviewIndexValue value = districtCommitteeMapper.getTbSiteReviewIndexValue(t);
                if (value == null) {
                    tbIndex.put("streetReviewValue", tbIndex.get("max"));
                } else {
                    tbIndex.put("streetReviewValue", value.getStreetReviewValue());
                }
                tbIndex.remove("max");
                tbIndex.put("year", year);
                tbIndex.put("siteId", siteId);
                for (Map<String, Object> map : list) {
                    String indexId = StringHelper.nvl(map.get("indexId"), "");
                    if (indexIdAll.equals(indexId)) {
                        tbIndex.putAll(map);
                    }
                }
            }
            System.out.println(index);
            String string = JSONObject.toJSONString(index);
            System.out.println(string);
            Integer integer = streetMapper.insertStreetReview(index);

            TbSiteReview tbSiteReview = new TbSiteReview();
            tbSiteReview.setYear(year.toString());
            tbSiteReview.setSiteId(siteId);
            TbSiteReview siteReview = streetMapper.getTbSiteReview(tbSiteReview);
            if (siteReview == null) {
                TbSiteReview tbSiteReview1 = new TbSiteReview();
                tbSiteReview1.setYear(year.toString());
                tbSiteReview1.setSiteId(siteId);
                tbSiteReview1.setStreetReviewTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSiteReview1.setStreetReviewUserId(userId);
                streetMapper.insertSiteReview(tbSiteReview1);
            }
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("复审测评保存");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/Street/insertStreetReview");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1014");
            tbSysLog.setFuncId("10017");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("复审测评保存");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/Street/insertStreetReview");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1014");
                tbSysLog.setFuncId("10017");
                tbSysLog.setMemo(e.getMessage());
            } catch (Exception e1) {
                logger.info("insertStreetReview---e1={}", e1);
            }
            logger.info("insertStreetReview---e={}", e);
            return new Dto(false, "失败", e);
        }
    }

    @Override
    public Dto updateStreetReview(JSONObject jsonObject) {
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
                    siteReview.setStreetReviewResult("1");
                    siteReview.setStreetReviewTime(DateUtils.dateFormatYYMMDDHHmmss());
                    siteReview.setStreetReviewUserId(userId);
                    streetMapper.updateStreetReview(siteReview);
                }
            }
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("复审测评上报");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/Street/updateStreetReview");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1014");
            tbSysLog.setFuncId("10018");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("复审测评上报");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/Street/updateStreetReview");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1014");
                tbSysLog.setFuncId("10018");
                tbSysLog.setMemo(e.getMessage());
            } catch (Exception e1) {
                logger.info("updateStreetReview---e1={}", e1);
            }
            logger.info("updateStreetReview---e={}", e);
            return new Dto(false, "失败", e.getMessage());
        }
    }

    public void insertTbSysLog(TbSysLog tbSysLog) {
        companySiteMapper.insertLog(tbSysLog);
    }

    public int getList(List<Map<String, Object>> list, List<Muji> Mujilist, int index, Map<String, Object> map, String siteId, Integer year) {
        String key = "index" + index++;
        int depth = 0;

        for (Muji muji : Mujilist) {
            String index_name = muji.getIndexName();
            List<Muji> tempmujilist = muji.getMujis();
            Map<String, Object> tempmap = new TreeMap<String, Object>();
            if (map != null) {
                tempmap.putAll(map);
            }
            tempmap.put(key, index_name);
            if (tempmujilist.isEmpty()) {
                String score = muji.getMax() == null ? "0" : StringHelper.nvl(muji.getMax(), "");
                String min = muji.getMin() == null ? "0" : StringHelper.nvl(muji.getMin(), "");
                String valueType = muji.getValueType() == null ? "0" : StringHelper.nvl(muji.getValueType(), "");
                String enum1 = muji.getEnum1() == null ? "0" : StringHelper.nvl(muji.getEnum1(), "");
                String indexId = muji.getIndexId() == null ? "0" : StringHelper.nvl(muji.getIndexId(), "");
                if ("1088".equals(indexId) || "1090".equals(indexId) || "1091".equals(indexId) || "1092".equals(indexId) || "1094".equals(indexId) || "1095".equals(indexId)) {
                    tempmap.put("max", "N");
                    tempmap.put("score", "N");
                } else {
                    tempmap.put("max", score);
                    tempmap.put("score", score);
                }
                tempmap.put("min", min);
                tempmap.put("valueType", valueType);
                tempmap.put("enum", enum1);
                tempmap.put("indexId", indexId);

                TbSiteApplicationIndexValue value = companySiteMapper.getTbSiteApplicationIndexValue(siteId, indexId);
                if (value != null) {
                    String selfValue = value.getSelfValue();
                    String streetValue = value.getStreetValue();
                    String districtValue = value.getDistrictValue();
                    TbSiteApplication tbSiteApplication = companySiteMapper.checkIsCommit(siteId);
                    if (tbSiteApplication != null) {
                        if ("Y".equals(selfValue) || "Y".equals(streetValue) || "Y".equals(districtValue)) {
                            tempmap.put("red", "0");//0是红色
                        } else {
                            tempmap.put("red", "1");//1是正常
                        }
                        tempmap.put("selfValue", selfValue);
                        tempmap.put("streetValue", streetValue);
                        tempmap.put("districtValue", districtValue);
                        tempmap.put("streetSuggestion", tbSiteApplication.getStreetSuggestion());
                        tempmap.put("districtSuggestion", tbSiteApplication.getDistrictSuggestion());
                        tempmap.put("districtCommitteeTitle", tbSiteApplication.getDistrictCommitteeTitle());
                        if (value.getFileNames() != null && value.getFileNames().length() != 0) {
                            tempmap.put("fileNames", value.getFileNames().split(","));
                        } else {
                            tempmap.put("fileNames", new ArrayList<>());
                        }
                    } else {
                        tempmap.put("selfValue", "");
                        tempmap.put("streetValue", "");
                        tempmap.put("districtValue", "");
                        tempmap.put("streetSuggestion", "");
                        tempmap.put("districtSuggestion", "");
                        tempmap.put("districtCommitteeTitle", "");
                        tempmap.put("reviewValue", "");
                        tempmap.put("reviewResult", "");
                        tempmap.put("fileNames", new ArrayList<>());
                    }
                }
                if (year != null) {
                    TbSiteReviewIndexValue t = new TbSiteReviewIndexValue();
                    t.setYear(year.toString());
                    t.setSiteId(siteId);
                    t.setIndexId(indexId);
                    TbSiteReviewIndexValue reviewIndexValue = districtCommitteeMapper.getTbSiteReviewIndexValue(t);
                    System.out.println(reviewIndexValue);
                    if (reviewIndexValue != null) {
                        System.out.println(reviewIndexValue.getStreetReviewValue());
                        tempmap.put("reviewResult", reviewIndexValue.getReviewValue());
                        tempmap.put("streetReviewValue", reviewIndexValue.getStreetReviewValue());
                        tempmap.put("districtReviewValue", reviewIndexValue.getDistrictReviewValue());
                    }
                }
                list.add(tempmap);
                System.out.println(tempmap);
                index--;
            } else {
                int tempdepth = getList(list, tempmujilist, index, tempmap, siteId, year);
                depth = Math.max(tempdepth, depth);
            }
            depth = Math.max(depth, index);
        }
        return depth;
    }

}
