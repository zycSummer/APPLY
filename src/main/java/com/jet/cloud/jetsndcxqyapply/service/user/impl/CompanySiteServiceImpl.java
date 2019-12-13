package com.jet.cloud.jetsndcxqyapply.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.controller.user.UserController;
import com.jet.cloud.jetsndcxqyapply.entity.Company.*;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.InetAddress;
import java.util.*;


@Service
public class CompanySiteServiceImpl implements CompanySiteService {
    @Autowired
    private CompanySiteMapper companySiteMapper;
    @Autowired
    private SystemUserMapper systemUserMapper;

    String[] strarry = new String[]{"一级指标", "二级指标", "三级指标", "四级指标", "五级指标", "六级指标", "七级指标"};
    Logger logger = LoggerFactory.getLogger(CompanySiteServiceImpl.class);

    /**
     * 如果当前用户的角色属于注册账号，则在进入申报自评页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户当前不允许自评申报，允许自评申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。
     */
    public Dto checkIsRegisterNo(String userId) {
        SystemUser user = systemUserMapper.getUserByUserId(userId);
        if (user != null) {
            String roleId = user.getRoleId();
            Integer integer = companySiteMapper.checkIsRegisterNo(roleId, userId);
            Map<String, Object> roleDate = companySiteMapper.getRoleDate(roleId);
            String end_date = StringHelper.nvl(roleDate.get("end_date"), "");
            String start_date = StringHelper.nvl(roleDate.get("start_date"), "");
            Map<String, String> map = new HashMap<>();
            map.put("start_date", start_date);
            map.put("end_date", end_date);
            if (integer != null) {
                return new Dto(true, "日期", map);
            } else {
                return new Dto(false, "日期", map);
            }
        }
        return new Dto(false);
    }

    /**
     * 如果当前用户（企业）没有提交过自评，展示自评申报页面；
     * 如果已经提交过，则提示用户“您已提交自评申报，请到申报进展页面查询进展”。
     * success=true msg=""  data="" (已经提交过)
     * msg=fail (没有提交过)
     */
    @Override
    public Dto checkIsCommit() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        String siteId = systemUser.getSiteId();
        TbSiteApplication tbSiteApplication = companySiteMapper.checkIsCommit(siteId);
        if (tbSiteApplication != null && tbSiteApplication.getSelfSubmitTime() != null) {
            return new Dto(true, "已经提交过", tbSiteApplication);
        }
        return new Dto(false);
    }

    //查询所有指标和标准值
    @Override
    public Dto getm(String flag) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        List<Muji> aa = companySiteMapper.getm(null);
        System.out.println(aa.toString());
        List<Map<String, String>> list = new ArrayList<>();
        int what = getList(list, aa, 1, null, session, flag);
        System.out.println(what);
        System.out.println(list.toString());
        String[] temparry = new String[what];
        temparry = Arrays.copyOf(strarry, what);
        List<String> listAll = Arrays.asList(temparry);
        System.out.println(new Dto(true, "查询成功", list, listAll));
        return new Dto(true, "查询成功", list, listAll);
    }

    public int getList(List<Map<String, String>> list, List<Muji> Mujilist, int index, Map<String, String> map, Session session, String flag) {
        String key = "index" + index++;
        int depth = 0;
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser userByUserId = systemUserMapper.getUserByUserId(userId);
        String siteId = userByUserId.getSiteId();
        Map<String, Object> maps = companySiteMapper.sumScore(siteId);
        String sumSelfValye = "";
        String sumStreetValue = "";
        String sumDistrictValue = "";
        if (maps != null) {
            sumSelfValye = StringHelper.nvl(maps.get("sumSelfValye"), "");
            sumStreetValue = StringHelper.nvl(maps.get("sumStreetValue"), "");
            sumDistrictValue = StringHelper.nvl(maps.get("sumDistrictValue"), "");
        }
        for (Muji muji : Mujilist) {
            String index_name = muji.getIndexName();
            List<Muji> tempmujilist = muji.getMujis();
            Map<String, String> tempmap = new TreeMap<String, String>();
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
                    if ("2".equals(flag)) {
                        if (tbSiteApplication.getSelfSubmitTime() != null && tbSiteApplication.getStreetSubmitTime() != null
                                && tbSiteApplication.getDistrictSubmitTime() != null && tbSiteApplication.getDistrictCommitteeTime() != null) {
                            tempmap.put("selfValue", selfValue);
                            tempmap.put("streetValue", streetValue);
                            tempmap.put("districtValue", districtValue);
                            tempmap.put("streetSuggestion", tbSiteApplication.getStreetSuggestion());
                            tempmap.put("districtSuggestion", tbSiteApplication.getDistrictSuggestion());
                            if ("1001".equals(tbSiteApplication.getDistrictCommitteeTitle())) {
                                tempmap.put("districtCommitteeTitle", "和谐先进企业");
                            } else if ("1002".equals(tbSiteApplication.getDistrictCommitteeTitle())) {
                                tempmap.put("districtCommitteeTitle", "和谐企业");
                            } else {
                                tempmap.put("districtCommitteeTitle", "");
                            }
                            tempmap.put("sumSelfValye", sumSelfValye);
                            tempmap.put("sumStreetValue", sumStreetValue);
                            tempmap.put("sumDistrictValue", sumDistrictValue);
                        } else if (tbSiteApplication.getSelfSubmitTime() != null && tbSiteApplication.getStreetSubmitTime() != null
                                && tbSiteApplication.getDistrictSubmitTime() != null) {
                            tempmap.put("selfValue", selfValue);
                            tempmap.put("streetValue", streetValue);
                            tempmap.put("districtValue", districtValue);
                            tempmap.put("streetSuggestion", tbSiteApplication.getStreetSuggestion());
                            tempmap.put("districtSuggestion", tbSiteApplication.getDistrictSuggestion());
                            tempmap.put("districtCommitteeTitle", "");
                            tempmap.put("sumSelfValye", sumSelfValye);
                            tempmap.put("sumStreetValue", sumStreetValue);
                            tempmap.put("sumDistrictValue", sumDistrictValue);
                        } else if (tbSiteApplication.getSelfSubmitTime() != null && tbSiteApplication.getStreetSubmitTime() != null) {
                            tempmap.put("selfValue", selfValue);
                            tempmap.put("streetValue", streetValue);
                            tempmap.put("districtValue", districtValue);
                            tempmap.put("streetSuggestion", tbSiteApplication.getStreetSuggestion());
                            tempmap.put("districtSuggestion", "");
                            tempmap.put("districtCommitteeTitle", "");
                            tempmap.put("sumSelfValye", sumSelfValye);
                            tempmap.put("sumStreetValue", sumStreetValue);
                            tempmap.put("sumDistrictValue", "");
                        } else if (tbSiteApplication.getSelfSubmitTime() != null) {
                            tempmap.put("selfValue", selfValue);
                            tempmap.put("streetValue", streetValue);
                            tempmap.put("districtValue", districtValue);
                            tempmap.put("streetSuggestion", "");
                            tempmap.put("districtSuggestion", "");
                            tempmap.put("districtCommitteeTitle", "");
                            tempmap.put("sumSelfValye", sumSelfValye);
                            tempmap.put("sumStreetValue", "");
                            tempmap.put("sumDistrictValue", "");
                        } else {
                            tempmap.put("selfValue", "");
                            tempmap.put("streetValue", "");
                            tempmap.put("districtValue", "");
                            tempmap.put("streetSuggestion", "");
                            tempmap.put("districtSuggestion", "");
                            tempmap.put("districtCommitteeTitle", "");
                            tempmap.put("sumSelfValye", "");
                            tempmap.put("sumStreetValue", "");
                            tempmap.put("sumDistrictValue", "");
                        }
                    } else {
                        tempmap.put("selfValue", selfValue);
                        tempmap.put("streetValue", streetValue);
                        tempmap.put("districtValue", districtValue);
                        tempmap.put("streetSuggestion", tbSiteApplication.getStreetSuggestion());
                        tempmap.put("districtSuggestion", tbSiteApplication.getDistrictSuggestion());
                        if ("1001".equals(tbSiteApplication.getDistrictCommitteeTitle())) {
                            tempmap.put("districtCommitteeTitle", "和谐先进企业");
                        } else if ("1002".equals(tbSiteApplication.getDistrictCommitteeTitle())) {
                            tempmap.put("districtCommitteeTitle", "和谐企业");
                        } else {
                            tempmap.put("districtCommitteeTitle", "");
                        }
                        tempmap.put("sumSelfValye", sumSelfValye);
                        tempmap.put("sumStreetValue", sumStreetValue);
                        tempmap.put("sumDistrictValue", sumDistrictValue);
                    }
                } else {
                    tempmap.put("selfValue", "");
                    tempmap.put("streetValue", "");
                    tempmap.put("districtValue", "");
                    tempmap.put("streetSuggestion", "");
                    tempmap.put("districtSuggestion", "");
                    tempmap.put("districtCommitteeTitle", "");
                    tempmap.put("sumSelfValye", "");
                    tempmap.put("sumStreetValue", "");
                    tempmap.put("sumDistrictValue", "");
                }
                list.add(tempmap);
                index--;
            } else {
                int tempdepth = getList(list, tempmujilist, index, tempmap, session, flag);
                depth = Math.max(tempdepth, depth);
            }
            depth = Math.max(depth, index);
        }
        return depth;
    }

    /**
     * 企业的保存方法
     */
    @Override
    @Transactional
    public Dto insertTbSiteApplicationIndexValue(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        List<Map<String, Object>> list = jsonObject.getObject("tableData", List.class);
        String beforeSiteId = jsonObject.getObject("siteId", String.class);
        List<Map<String, Object>> file = jsonObject.getObject("file", List.class);
        List<String> fileName1 = new ArrayList<>();
        for (Map<String, Object> map : file) {
            String name = StringHelper.nvl(map.get("fileName"), "");
            fileName1.add(name);
        }
        boolean b = cheakIsRepeat(fileName1);
        if (!b) {
            return new Dto(false, "该企业存在重复文件", null);
        }
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
            if (listIndex != null && !listIndex.isEmpty()) {
                return new Dto(false, "存在一票否决", listIndex);
            }
            List<TbSiteApplicationIndexValue> listTbSiteApplicationIndexValue = new ArrayList<>();
            Set<String> listFileName = new HashSet<>();
            List<TbSiteApplication> listTbSiteApplication = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    String indexId = StringHelper.nvl(map.get("indexId"), "");
                    String selfValue = StringHelper.nvl(map.get("selfValue"), "");//企业自评值
                    String streetValue = StringHelper.nvl(map.get("streetValue"), "");//镇（街道）测评值
                    String districtValue = StringHelper.nvl(map.get("districtValue"), "");//区三方评定值
                    String streetSuggestion = StringHelper.nvl(map.get("streetSuggestion"), "");//测评建议
                    String districtSuggestion = StringHelper.nvl(map.get("districtSuggestion"), "");//评定建议

                    TbSiteApplicationIndexValue value = companySiteMapper.getTbSiteApplicationIndexValue(siteId, indexId);
                    if (value == null) {
                        TbSiteApplicationIndexValue t = new TbSiteApplicationIndexValue();
                        t.setSiteId(siteId);
                        t.setIndexId(indexId);
                        t.setSelfValue(selfValue);
                        t.setStreetValue(streetValue);
                        t.setDistrictValue(districtValue);
                        t.setFileNames("");
                        listTbSiteApplicationIndexValue.add(t);
                    } else {
                        value.setSelfValue(selfValue);
                        value.setStreetValue(streetValue);
                        value.setDistrictValue(districtValue);
                        value.setFileNames("");
                        listTbSiteApplicationIndexValue.add(value);
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
                }
                companySiteMapper.insertTbSiteApplicationIndexValue(listTbSiteApplicationIndexValue);
                companySiteMapper.insertTbSiteApplication(listTbSiteApplication);
            }
            List<TbSiteApplicationIndexValue> listIndexValue = new ArrayList<>();
            if (file != null && !file.isEmpty()) {
                for (Map<String, Object> map : file) {
                    String seqId = StringHelper.nvl(map.get("seqId"), "");
                    String appendixType = StringHelper.nvl(map.get("appendixType"), "");
                    String path = StringHelper.nvl(map.get("path"), "");
                    String memo = StringHelper.nvl(map.get("memo"), "");
                    String review = StringHelper.nvl(map.get("review"), "");
                    String fileName = StringHelper.nvl(map.get("fileName"), "");

                    String caselsh = fileName.substring(0, fileName.lastIndexOf("."));
                    // 通过文件名前缀查询指标信息表(如果存在继续判断indexId,不存在提示用户)
                    List<TBIndexMappingFileName> tbIndexFileName = companySiteMapper.getFileName(caselsh);
                    if (!"苏州高新区和谐劳动关系创建活动申报表".equals(caselsh) && !"职工满意度调查表".equals(caselsh) && !"申报承诺书".equals(caselsh)) {
                        if (tbIndexFileName != null && !tbIndexFileName.isEmpty()) {
                            for (int i = 0; i < tbIndexFileName.size(); i++) {
                                String fileNames = tbIndexFileName.get(i).getFileName();
                                String indexId = tbIndexFileName.get(i).getIndexId();
                                TbSiteApplicationIndexValue value = companySiteMapper.getTbSiteApplicationIndexValue(siteId, indexId);
                                if (value != null) {
                                    String names = StringHelper.nvl(value.getFileNames(), "");
                                    // 空字符串说明第一次插入,否则拼接字符串更新fileName字段
                                    if ("".equals(names)) {
                                        value.setFileNames(fileNames);
                                        listIndexValue.add(value);
                                    } else {
                                        value.setFileNames(fileNames + "," + names);
                                        listIndexValue.add(value);
                                    }
                                } else {
                                    TbSiteApplicationIndexValue t = new TbSiteApplicationIndexValue();
                                    t.setSiteId(siteId);
                                    t.setIndexId(indexId);
                                    t.setFileNames(tbIndexFileName.get(i).getFileName());
                                    t.setSelfValue("");
                                    t.setStreetValue("");
                                    t.setDistrictValue("");
                                    listIndexValue.add(t);
                                }
                            }
                        } else {
                            return new Dto(false, "上传的文件中有未关联的文件名" + fileName, caselsh);
                        }
                        companySiteMapper.insertTbSiteApplicationIndexValue(listIndexValue);
                    }
                    TbSiteApplicationAppendix appendix = companySiteMapper.getTbSiteApplicationAppendix(seqId);
                    if (appendix == null) {
                        TbSiteApplicationAppendix tb = new TbSiteApplicationAppendix();
                        tb.setSiteId(siteId);
                        tb.setAppendixType(appendixType);
                        tb.setPath(path);
                        tb.setMemo(memo);
                        tb.setCreateUserId(userId);
                        tb.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        tb.setUpdateUserId(userId);
                        tb.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        tb.setReview(review);
                        tb.setFileName(fileName);
                        companySiteMapper.insertTbSiteApplicationAppendix(tb);
                    } else {
                        appendix.setAppendixType(appendixType);
                        appendix.setPath(path);
                        appendix.setMemo(memo);
                        appendix.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        appendix.setUpdateUserId(userId);
                        appendix.setReview(review);
                        appendix.setFileName(fileName);
                        companySiteMapper.updateTbSiteApplicationAppendix(appendix);
                    }
                }
            }

            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
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
                tbSysLog.setMeunId("1005");
                tbSysLog.setFuncId("10005");
                tbSysLog.setUrl("/Street/insertStreet");
            } else {
                tbSysLog.setOperateContent("申报自评保存");
                tbSysLog.setUrl("/company/insertTbSiteApplicationIndexValue");
                tbSysLog.setMeunId("1001");
                tbSysLog.setFuncId("10001");
            }
            tbSysLog.setOperateResult("success");
            tbSysLog.setMethod("post");
            insertTbSysLog(tbSysLog);
        } catch (Exception e) {
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(StringHelper.nvl(session.getAttribute("loginUser"), ""));
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());

                TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);
                if (application != null) {
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
                    } else {
                        tbSysLog.setOperateContent("申报自评保存");
                        tbSysLog.setUrl("/company/insertTbSiteApplicationIndexValue");
                        tbSysLog.setMeunId("1001");
                        tbSysLog.setFuncId("10001");
                    }
                }
                tbSysLog.setMethod("post");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setMethod("post");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
                e.printStackTrace();
                logger.info("insertTbSiteApplicationIndexValue=保存失败{}", e);
                return new Dto(true, "保存失败", e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return new Dto(true, "保存成功", null);
    }

    // 企业提交方法
    @Override
    @Transactional
    public Dto updateTbSiteApplicationIndexValue(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);

        String siteId = systemUser.getSiteId();
        String roleId = systemUser.getRoleId();
        List<TbSiteApplication> listTbSiteApplication = new ArrayList<>();

        try {
            Integer menuFunc = companySiteMapper.getTbSysMenuFunc(roleId, "/company/commitTbSiteApplicationIndexValue");
            if (menuFunc < 1) {
                return new Dto(false, "没有权限", null);
            } else {
                TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);
                if ("1001".equals(roleId)) {
                    application.setSelfUserId(userId);
                    application.setSelfSubmitTime(DateUtils.dateFormatYYMMDDHHmmss());
                    listTbSiteApplication.add(application);
                    companySiteMapper.insertTbSiteApplication(listTbSiteApplication);
                }

                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());

                String selfSubmitTime = application.getSelfSubmitTime();
                String streetSubmitTime = application.getStreetSubmitTime();
                String districtSubmitTime = application.getDistrictSubmitTime();

                tbSysLog.setOperateContent("申报自评提交");
                tbSysLog.setUrl("/company/updateTbSiteApplicationIndexValue");
                tbSysLog.setMeunId("1001");
                tbSysLog.setFuncId("10002");

                tbSysLog.setOperateResult("success");
                tbSysLog.setMethod("post");
                insertTbSysLog(tbSysLog);
            }
        } catch (Exception e) {
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(StringHelper.nvl(session.getAttribute("loginUser"), ""));
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());

                TbSiteApplication application = companySiteMapper.checkIsCommit(siteId);
                String selfSaveTime = application.getSelfSaveTime();
                String streetSaveTime = application.getStreetSaveTime();
                String districtSaveTime = application.getDistrictSaveTime();

                tbSysLog.setOperateContent("申报自评提交");
                tbSysLog.setUrl("/company/updateTbSiteApplicationIndexValue");
                tbSysLog.setMeunId("1001");
                tbSysLog.setFuncId("10002");

                tbSysLog.setOperateResult("fail");
                tbSysLog.setMethod("post");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Dto(false);
        }
        return new Dto(true, "提交成功", null);
    }


    @Override
    public void insertTbSysLog(TbSysLog tbSysLog) {
        companySiteMapper.insertLog(tbSysLog);
    }

    //查询如果用户有没有提交自评申报,有的话就展示申报进展信息
    @Override
    public Dto checkIsCommit(String siteId, String indexId) {
        TbSiteApplication s = companySiteMapper.checkIsCommit(siteId);
        TbSiteApplicationIndexValue tb = companySiteMapper.getTbSiteApplicationIndexValue(siteId, indexId);
        String submitTime = StringHelper.nvl(s.getSelfSubmitTime(), "");
        if (submitTime == "") {
            return new Dto(false);
        }
        return new Dto(true, "成功", tb);
    }

    //查询企业申报附件表
    @Override
    public Dto getTbSiteApplicationAppendix1() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        String siteId = systemUser.getSiteId();
        List<TbSiteApplicationAppendix> list = companySiteMapper.getTbSiteApplicationAppendix1(siteId);
        return new Dto(list);
    }

    // 注册信息保存
    @Override
    @Transactional
    public int updateSite(CompanySite company) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        String siteId = systemUser.getSiteId();
        String roleId = systemUser.getRoleId();
        int i = 0;
        try {
            Integer menuFunc = companySiteMapper.getTbSysMenuFunc(roleId, "/company/updateSite");
            if (menuFunc < 1) {
                return 0;
            } else {
                CompanySite companySite = systemUserMapper.getSiteBySiteId(siteId);
                companySite.setApplicant(StringHelper.nvl(company.getApplicant(), ""));
                companySite.setSiteName(StringHelper.nvl(company.getSiteName(), ""));
                companySite.setOrgCode(StringHelper.nvl(company.getOrgCode(), ""));
                companySite.setSiteTypeId(StringHelper.nvl(company.getSiteTypeId(), ""));
                companySite.setIndustryTypeId(StringHelper.nvl(company.getIndustryTypeId(), ""));
                companySite.setStreetId(StringHelper.nvl(company.getStreetId(), ""));
                companySite.setParkId(StringHelper.nvl(company.getParkId(), ""));
                companySite.setAddr(StringHelper.nvl(company.getAddr(), ""));
                companySite.setContacts(StringHelper.nvl(company.getContacts(), ""));
                companySite.setTel(StringHelper.nvl(company.getTel(), ""));
                companySite.setMobile(StringHelper.nvl(company.getMobile(), ""));
                companySite.setMail(StringHelper.nvl(company.getMail(), ""));
                companySite.setUpdateUserId(userId);
                companySite.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                i = systemUserMapper.updateSite(companySite);

                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("注册信息保存");
                tbSysLog.setOperateResult("success");
                tbSysLog.setUrl("/company/updateSite");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1003");
                tbSysLog.setFuncId("10003");
                insertTbSysLog(tbSysLog);
            }
        } catch (Exception e) {
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("注册信息保存");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/company/updateSite");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1003");
                tbSysLog.setFuncId("10003");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return i;
    }

    // 校验企业是否有复审权限
    @Override
    public Dto checkCompanyReview(String userId) {
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        String siteId = systemUser.getSiteId();
        TbSiteApplication tbSiteApplication = companySiteMapper.checkCompanyReview(siteId);
        if (tbSiteApplication != null) {
            String time = tbSiteApplication.getDistrictCommitteeTime().substring(0, 4);
            Integer date = Calendar.getInstance().get(Calendar.YEAR) - 1;
            if (Integer.parseInt(time) == date) {
                return new Dto(true, "有权限", null);
            }
            return new Dto(false, "没有权限", null);
        } else {
            return new Dto(false, "没有权限", null);
        }
    }

    //企业复审上传文件后保存
    @Override
    @Transactional
    public Dto insertTbSiteApplicationAppendix(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        String beforeSiteId = jsonObject.getObject("siteId", String.class);
        List<Map<String, Object>> file = jsonObject.getObject("file", List.class);
        List<String> fileName1 = new ArrayList<>();
        for (Map<String, Object> map : file) {
            String name = StringHelper.nvl(map.get("fileName"), "");
            fileName1.add(name);
        }
        boolean b = cheakIsRepeat(fileName1);
        if (!b) {
            return new Dto(false, "该企业存在重复文件", null);
        }
        SystemUser systemUser = systemUserMapper.getUserByUserId(userId);
        String siteId = systemUser.getSiteId();
        if (siteId == null || "".equals(siteId)) {
            siteId = beforeSiteId;
        }
        try {
            if (file != null && !file.isEmpty()) {
                for (Map<String, Object> map : file) {
                    String seqId = StringHelper.nvl(map.get("seqId"), "");
                    String appendixType = StringHelper.nvl(map.get("appendixType"), "");
                    String path = StringHelper.nvl(map.get("path"), "");
                    String memo = StringHelper.nvl(map.get("memo"), "");
                    String review = StringHelper.nvl(map.get("review"), "");
                    String fileName = StringHelper.nvl(map.get("fileName"), "");

                    TbSiteApplicationAppendix appendix = companySiteMapper.getTbSiteApplicationAppendix(seqId);
                    if (appendix == null) {
                        TbSiteApplicationAppendix tb = new TbSiteApplicationAppendix();
                        tb.setSiteId(siteId);
                        tb.setAppendixType(appendixType);
                        tb.setPath(path);
                        tb.setMemo(memo);
                        tb.setCreateUserId(userId);
                        tb.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        tb.setUpdateUserId(userId);
                        tb.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        tb.setReview(review);
                        tb.setFileName(fileName);
                        companySiteMapper.insertTbSiteApplicationAppendix(tb);
                    } else {
                        appendix.setAppendixType(appendixType);
                        appendix.setPath(path);
                        appendix.setMemo(memo);
                        appendix.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                        appendix.setUpdateUserId(userId);
                        appendix.setReview(review);
                        appendix.setFileName(fileName);
                        companySiteMapper.updateTbSiteApplicationAppendix(appendix);
                    }
                }
            }
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("企业复审上传文件后保存");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/company/insertTbSiteApplicationAppendix");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1016");
            tbSysLog.setFuncId("10022");
            insertTbSysLog(tbSysLog);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("企业复审上传文件后保存");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/company/insertTbSiteApplicationAppendix");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1016");
                tbSysLog.setFuncId("10022");
                tbSysLog.setMemo(e.getMessage());
            } catch (Exception e1) {
                logger.info("insertTbSiteApplicationAppendix---e1={}", e1);
            }
            logger.info("insertTbSiteApplicationAppendix---e={}", e);
            return new Dto(true, "上传失败", e);
        }
        return new Dto(true, "上传成功", null);
    }

    @Override
    @Transactional
    public Dto deleteTbSiteApplicationAppendix(String seqId) {
        try {
            // 删除对应的指标下的文件名
            TbSiteApplicationAppendix tbSiteApplicationAppendix = companySiteMapper.getTbSiteApplicationAppendix(seqId);
            List<TbSiteApplicationIndexValue> tbSiteApplication = companySiteMapper.getTbSiteApplicationIndexValueByFileName(tbSiteApplicationAppendix.getFileName());
            for (TbSiteApplicationIndexValue indexValue : tbSiteApplication) {
                String seqId1 = indexValue.getSeqId().toString();
                String fileNames = indexValue.getFileNames();
                String replace = fileNames.replace("," + tbSiteApplicationAppendix.getFileName(), "");
                int i = replace.indexOf(",");
                if (replace.equals(fileNames) && i > 0) {
                    replace = fileNames.replace(tbSiteApplicationAppendix.getFileName() + ",", "");
                } else if (replace.equals(fileNames) && i < 0) {
                    replace = fileNames.replace(tbSiteApplicationAppendix.getFileName(), "");
                }
                companySiteMapper.updateIndexValue(replace, seqId1);
            }

            Integer integer = companySiteMapper.deleteTbSiteApplicationAppendix(seqId);
            return new Dto(true, "删除成功", integer);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("deleteTbSiteApplicationAppendix---e={}", e);
            return new Dto(false, "删除失败", e);
        }
    }

    @Override
    public Dto getAllAppendix() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser userByUserId = systemUserMapper.getUserByUserId(userId);
        List<TbSiteApplicationAppendix> allAppendix = companySiteMapper.getAllAppendix(userByUserId.getSiteId());
        return new Dto(true, "成功", allAppendix);
    }

    public static boolean cheakIsRepeat(List<String> array) {
        HashSet<String> hashSet = new HashSet<String>();
        for (int i = 0; i < array.size(); i++) {
            hashSet.add(array.get(i));
        }
        if (hashSet.size() == array.size()) {
            return true;
        } else {
            return false;
        }
    }

    public static String setToString(Set<String> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;

        //第一个前面不拼接","
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }

    public List<String> stringToList(String strs) {
        String str[] = strs.split(",");
        return Arrays.asList(str);
    }
}
