package com.jet.cloud.jetsndcxqyapply.service.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.DateUtils;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.Company.Muji;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplication;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplicationIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.entity.SysStreet;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.entity.UserRole;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReview;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSiteReviewIndexValue;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSySPoints;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSysTitle;
import com.jet.cloud.jetsndcxqyapply.entity.log.TbSysLog;
import com.jet.cloud.jetsndcxqyapply.mapper.CompanySiteMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictCommitteeMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.DistrictMapper;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.DistrictCommitteeService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.util.*;

@Service
public class DistrictCommitteeServiceImpl implements DistrictCommitteeService {

    public static Map<String, Map> dic = new HashMap<String, Map>();

    @Autowired
    private DistrictCommitteeMapper districtCommitteeMapper;

    @Autowired
    private CompanySiteMapper companySiteMapper;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private DistrictMapper districtMapper;

    String[] strarry = new String[]{"一级指标", "二级指标", "三级指标", "四级指标", "五级指标", "六级指标", "七级指标"};

    public static void func(List<Map<String, Object>> list) {
        Map son;
        for (Map map : list) {
            String siteId = (String) map.get("siteId");
            if (!DistrictCommitteeServiceImpl.isEmpty(siteId)) {
                if (DistrictCommitteeServiceImpl.isEmpty(son = dic.get(siteId))) {
                    son = new HashMap();
                    son.putAll(map);
                    dic.put(siteId, son);
                } else {
                    son.putAll(map);
                }
            }
        }

    }

    public static boolean isEmpty(Object pObj) {
        if (pObj == null)
            return true;
        if (pObj == "")
            return true;
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

    // 汇总页面
    @Override
    public Dto getAllDistrictCommittee(Map<String, Object> map) {
        List<Map<String, Object>> allDistrict = districtCommitteeMapper.getAllDistrictCommittee(map);
        Integer districtSum = districtCommitteeMapper.getDistrictCommitteeSum(map);
        if (allDistrict != null && !allDistrict.isEmpty()) {
            for (Map<String, Object> district : allDistrict) {
                String districtCommitteeTitle = StringHelper.nvl(district.get("districtCommitteeTitle"), "");
                TbSysTitle tbSysTitle = districtCommitteeMapper.getTbSysTitleByTitleId(districtCommitteeTitle);
                if (tbSysTitle != null) {
                    String titleName = tbSysTitle.getTitleName();
                    district.put("districtCommitteeTitle", titleName);
                } else {
                    district.put("districtCommitteeTitle", "");
                }
            }
            return new Dto(true, "查询成功", allDistrict, districtSum);
        }
        // 没有提交
        return new Dto(false, "没有企业", null);
    }

    //查询称号列表
    @Override
    public Dto getTbSysTitle() {
        List<TbSysTitle> tbSysTitle = districtCommitteeMapper.getTbSysTitle();
        if (tbSysTitle != null && !tbSysTitle.isEmpty()) {
            return new Dto(true, "查询成功", tbSysTitle);
        }
        return new Dto(false, "查询失败", null);
    }

    // 保存授予编号
    @Override
    public Dto updateDistrictCommitteeTitle(List<Map<String, Object>> map, String titleId) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        try {
            List<TbSiteApplication> list = new ArrayList<>();
            for (Map<String, Object> tb : map) {
                String siteId = StringHelper.nvl(tb.get("siteId"), "");
                TbSiteApplication tbSiteApplication = companySiteMapper.checkIsCommit(siteId);
                tbSiteApplication.setDistrictCommitteeTitle(titleId);
                tbSiteApplication.setDistrictCommitteeUserId(userId);
                tbSiteApplication.setDistrictCommitteeTime(DateUtils.dateFormatYYMMDDHHmmss());
                list.add(tbSiteApplication);
            }
            companySiteMapper.insertTbSiteApplication(list);
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("授予称号点击保存");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/districtCommittee/updateDistrictCommitteeTitle");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1007");
            tbSysLog.setFuncId("1008");
            tbSysLog.setMemo("");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "更新成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("授予称号点击保存");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/districtCommittee/updateDistrictCommitteeTitle");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1007");
                tbSysLog.setFuncId("1008");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Dto(false, "更新失败", e);
        }
    }

    public void insertTbSysLog(TbSysLog tbSysLog) {
        companySiteMapper.insertLog(tbSysLog);
    }

    // 复审页面展示
    @Override
    public Dto getAllReviewDistrictCommittee(Map<String, Object> map) throws Exception {
        List<Map<String, Object>> allDistrict = districtCommitteeMapper.getAllReviewDistrictCommittee(map);
        System.out.println(allDistrict.size());
        Integer districtSum = districtCommitteeMapper.getSumReviewDistrictCommittee(map);
        List<String> listSiteId = new ArrayList<>();
        if (allDistrict != null && !allDistrict.isEmpty()) {
            for (Map<String, Object> district : allDistrict) {
                String siteId = StringHelper.nvl(district.get("siteId"), "");
                listSiteId.add(siteId);

                String districtCommitteeTitle = StringHelper.nvl(district.get("districtCommitteeTitle"), "");
                TbSysTitle tbSysTitle = districtCommitteeMapper.getTbSysTitleByTitleId(districtCommitteeTitle);
                String titleName = tbSysTitle.getTitleName();
                district.put("districtCommitteeTitle", titleName);

                String districtSubmit = StringHelper.nvl(district.get("districtSubmitTime"), "");
                if (districtSubmit != "") {
                    String districtSubmitTime = DateUtils.getPayTime(districtSubmit);
                    district.put("districtSubmitTime", districtSubmitTime);
                }
                String streetSumbit = StringHelper.nvl(district.get("streetSumbitTime"), "");
                if (streetSumbit != "") {
                    String streetSumbitTime = DateUtils.getPayTime(streetSumbit);
                    district.put("streetSumbitTime", streetSumbitTime);
                }
                String districtCommittee = StringHelper.nvl(district.get("districtCommitteeTime"), "");
                if (districtCommittee != "") {
                    String districtCommitteeTime = DateUtils.getPayTime(districtCommittee);
                    district.put("districtCommitteeTime", districtCommitteeTime);
                }
            }
            /*Map<String, Object> map1 = new HashMap<>();
            map1.put("year", StringHelper.nvl(map.get("year"), ""));
            map1.put("siteList", listSiteId);

            List<Map<String, Object>> reValueAndResult = districtCommitteeMapper.getReValueAndResult(map1);
            List listAll = new ArrayList();
            allDistrict.addAll(reValueAndResult);
            DistrictCommitteeServiceImpl.func(allDistrict);
            Map rs = new HashMap();
            System.out.println(dic);

            for (Object value : dic.values()) {
                listAll.add(value);
                System.out.println("Value = " + value);
            }
            System.out.println(allDistrict.size());*/
            return new Dto(true, "查询成功", allDistrict, districtSum);
        }
        // 没有企业
        return new Dto(false, "没有企业", null);
    }

    @Override
    public Dto getm(String siteId, String year) {
        List<Muji> aa = companySiteMapper.getm(null);
        System.out.println(aa.toString());
        List<Map<String, String>> list = new ArrayList<>();
        int what = getList(list, aa, 1, null, siteId, year);
        System.out.println(what);
        System.out.println(list.toString());
        String[] temparry = new String[what];
        temparry = Arrays.copyOf(strarry, what);
        List<String> listAll = Arrays.asList(temparry);
        System.out.println(new Dto(true, "查询成功", list, listAll));
        return new Dto(true, "查询成功", list, listAll);
    }

    public int getList(List<Map<String, String>> list, List<Muji> Mujilist, int index, Map<String, String> map, String siteId, String year) {
        String key = "index" + index++;
        int depth = 0;
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

                TbSiteReviewIndexValue tb = new TbSiteReviewIndexValue();
                Integer year1 = 0;
                if (year != null && year != "") {
                    year1 = Integer.parseInt(year) - 1;
                } else {
                    year1 = Calendar.getInstance().get(Calendar.YEAR) - 1;
                }
                String s = String.valueOf(year1);
                tb.setYear(s);
                tb.setSiteId(siteId);
                tb.setIndexId(indexId);
                // 企业年度复审指标分值表
                TbSiteReviewIndexValue tbSiteReviewIndexValue = districtCommitteeMapper.getTbSiteReviewIndexValue(tb);

                TbSiteReview review = new TbSiteReview();
                review.setYear(year);
                review.setSiteId(siteId);
                TbSiteReview tbSiteReview = districtCommitteeMapper.getTbSiteReview(review);

                TbSiteApplicationIndexValue value = companySiteMapper.getTbSiteApplicationIndexValue(siteId, indexId);
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
                    if (tbSiteReviewIndexValue != null) {
                        tempmap.put("reviewValue", tbSiteReviewIndexValue.getReviewValue());
                        tempmap.put("streetReviewValue", tbSiteReviewIndexValue.getStreetReviewValue());
                        tempmap.put("districtReviewValue", tbSiteReviewIndexValue.getDistrictReviewValue());
                    } else {
                        tempmap.put("reviewValue", "");
                        tempmap.put("streetReviewValue", "");
                        tempmap.put("districtReviewValue", "");
                    }
                    if (tbSiteReview != null) {
                        tempmap.put("reviewResult", tbSiteReview.getReviewResult());
                        tempmap.put("streetReviewResult", tbSiteReview.getStreetReviewResult());
                        tempmap.put("districtReviewResult", tbSiteReview.getDistrictReviewResult());
                    } else {
                        tempmap.put("reviewResult", "");
                        tempmap.put("streetReviewResult", "");
                        tempmap.put("districtReviewResult", "");
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
                }
                list.add(tempmap);
                index--;
            } else {
                int tempdepth = getList(list, tempmujilist, index, tempmap, siteId, year);
                depth = Math.max(tempdepth, depth);
            }
            depth = Math.max(depth, index);
        }
        return depth;
    }

    // 复审保存
    @Override
    public Dto replaceTbSiteReviewIndexValueAndTbSiteReview(JSONObject jsonObject) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        try {
            String reviewResult = jsonObject.getObject("reviewResult", String.class);
            String siteId = jsonObject.getObject("siteId", String.class);
            String year = jsonObject.getObject("year", String.class);
            List<Map<String, Object>> list = jsonObject.getObject("tableData", List.class);
            List<String> listIndex = new ArrayList<>();
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    String reviewValue = StringHelper.nvl(map.get("reviewValue"), "");
                    if ("0".equals(reviewResult) && "Y".equals(reviewValue)) {
                        String indexId = StringHelper.nvl(map.get("indexId"), "");
                        listIndex.add(indexId);
                    }
                }
                if (listIndex != null && !listIndex.isEmpty()) {
                    return new Dto(false, "存在一票否决条件列设置的条件", listIndex);
                }
            }

            List<TbSiteReviewIndexValue> tbSiteReviewIndexValueList = new ArrayList<>();
            for (Map<String, Object> map : list) {
                String indexId = StringHelper.nvl(map.get("indexId"), "");
                TbSiteReviewIndexValue tbSiteReviewIndexValue = districtMapper.getTbSiteReviewIndexValue(year.toString(), siteId, indexId);
                tbSiteReviewIndexValue.setReviewValue(StringHelper.nvl(map.get("reviewValue"), ""));
                tbSiteReviewIndexValueList.add(tbSiteReviewIndexValue);
            }
            districtCommitteeMapper.replaceTbSiteReviewIndexValue(tbSiteReviewIndexValueList);

            TbSiteReview tbSiteReview = new TbSiteReview();
            tbSiteReview.setYear(year);
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                    //年度复审值
                    String reviewValue = StringHelper.nvl(map.get("reviewValue"), "");
                    if ("Y".equals(reviewValue)) {
                        tbSiteReview.setReviewResult("1");
                        break;
                    } else {
                        tbSiteReview.setReviewResult(reviewResult);
                    }
                }
            }
            tbSiteReview.setSiteId(siteId);
            tbSiteReview.setReviewUserId(userId);
            tbSiteReview.setReviewTime(DateUtils.dateFormatYYMMDDHHmmss());
            districtCommitteeMapper.replaceTbSiteReview(tbSiteReview);

            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("复审保存");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/districtCommittee/replaceReviewDistrictCommittee");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1008");
            tbSysLog.setFuncId("1009");
            tbSysLog.setMemo("");
            insertTbSysLog(tbSysLog);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("复审保存");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/districtCommittee/replaceReviewDistrictCommittee");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1008");
                tbSysLog.setFuncId("1009");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Dto(false, "保存失败", null);
        }
        return new Dto(true, "保存成功", null);
    }

    @Override
    public Dto getSystemUser(Map<String, Object> map) {
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer limit = Integer.parseInt(map.get("limit").toString());
        Integer page1 = (page - 1) * limit;
        map.put("page", page1);
        map.put("limit", limit);
        List<SystemUser> systemUser = districtCommitteeMapper.getSystemUser(map);
        for (SystemUser user : systemUser) {
            String roleId = user.getRoleId();
            String siteId = user.getSiteId();
            String streetId = user.getStreetId();

            String createTime = StringHelper.nvl(user.getCreateTime(), "");
            if (createTime != "") {
                String payTime = DateUtils.getPayTime(createTime);
                user.setCreateTime(payTime);
            }
            String updateTime = StringHelper.nvl(user.getUpdateTime(), "");
            if (updateTime != "") {
                String payTime = DateUtils.getPayTime(updateTime);
                user.setUpdateTime(payTime);
            }
            String lastLoginTime = StringHelper.nvl(user.getLastLoginTime(), "");
            if (lastLoginTime != "") {
                String payTime = DateUtils.getPayTime(lastLoginTime);
                user.setLastLoginTime(payTime);
            }
            String registrationTime = StringHelper.nvl(user.getRegistrationTime(), "");
            if (registrationTime != "") {
                String payTime1 = DateUtils.getPayTime(user.getRegistrationTime());
                user.setRegistrationTime(payTime1);
            }
            UserRole userRoles = systemUserMapper.getUserRolesByRoleId(roleId);
            if (userRoles != null) {
                String roleName = userRoles.getRoleName();
                user.setRoleName(roleName);
            }
            CompanySite companySite = systemUserMapper.getSiteBySiteId(siteId);
            if (companySite != null) {
                String siteName = StringHelper.nvl(companySite.getSiteName(), "");
                user.setSiteName(siteName);
            }

            SysStreet sysStreetByStreetId = systemUserMapper.getSysStreetByStreetId(streetId);
            if (sysStreetByStreetId != null) {
                String streetName = StringHelper.nvl(sysStreetByStreetId.getStreetName(), "");
                user.setStreetName(streetName);
            }
        }
        Integer sumSystemUser = districtCommitteeMapper.getSumSystemUser(map);
        if (systemUser != null && !systemUser.isEmpty()) {
            return new Dto(true, "查询成功", systemUser, sumSystemUser);
        } else {
            return new Dto(false, "查询失败", null);
        }
    }

    @Override
    public Dto getUserRole(Map<String, Object> map) {
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer limit = Integer.parseInt(map.get("limit").toString());
        Integer page1 = (page - 1) * limit;
        map.put("page", page1);
        map.put("limit", limit);
        List<UserRole> userRole = districtCommitteeMapper.getUserRole(map);
        for (UserRole role : userRole) {
            String createTime = StringHelper.nvl(role.getCreateTime(), "");
            if (createTime != "") {
                String payTime1 = DateUtils.getPayTime(createTime);
                role.setCreateTime(payTime1);
            }

            String updateTime = StringHelper.nvl(role.getUpdateTime(), "");
            if (updateTime != "") {
                String payTime1 = DateUtils.getPayTime(updateTime);
                role.setUpdateTime(payTime1);
            }
        }
        Integer sumUserRole = districtCommitteeMapper.getSumUserRole(map);
        if (userRole != null && !userRole.isEmpty()) {
            return new Dto(true, "查询成功", userRole, sumUserRole);
        } else {
            return new Dto(false, "查询失败", null);
        }
    }

    @Override
    public Dto updateUserRole(UserRole userRole) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        try {
            userRole.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
            userRole.setUpdateUserId(userId);
            Integer integer = districtCommitteeMapper.updateUserRole(userRole);
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("账户时限管理修改");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/districtCommittee/updateUserRole");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1010");
            tbSysLog.setFuncId("1010");
            tbSysLog.setMemo("");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "更新成功", integer);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("账户时限管理修改");
                tbSysLog.setOperateResult("fail");
                tbSysLog.setUrl("/districtCommittee/updateUserRole");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1010");
                tbSysLog.setFuncId("1010");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Dto(false, "更新失败", e);
        }
    }

    @Override
    public Dto getSysPoints() {
        TbSySPoints sysPoints = districtCommitteeMapper.getSysPoints();
        if (sysPoints != null) {
            return new Dto(true, "查询成功", sysPoints);
        } else {
            return new Dto(false, "查询失败", null);
        }
    }

    @Override
    public Dto insertSysPoints(TbSySPoints tbSySPoints) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        String userId = StringHelper.nvl(session.getAttribute("loginUser"), "");
        try {
            Integer integer = 0;
            TbSySPoints sysPoints = districtCommitteeMapper.getSysPoints();
            if (sysPoints != null) {
                sysPoints.setPoints(tbSySPoints.getPoints());
                sysPoints.setUpdateUserId(userId);
                sysPoints.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                districtCommitteeMapper.updateSysPoints(sysPoints);
            } else {
                tbSySPoints.setCreateUserId(userId);
                tbSySPoints.setCreateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSySPoints.setUpdateUserId(userId);
                tbSySPoints.setUpdateTime(DateUtils.dateFormatYYMMDDHHmmss());
                integer = districtCommitteeMapper.insertSysPoints(tbSySPoints);
            }
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setOperatorId(userId);
            tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
            tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
            tbSysLog.setOperateContent("保存系统提示");
            tbSysLog.setOperateResult("success");
            tbSysLog.setUrl("/districtCommittee/insertSysPoints");
            tbSysLog.setMethod("post");
            tbSysLog.setMeunId("1011");
            tbSysLog.setFuncId("1011");
            tbSysLog.setMemo("");
            insertTbSysLog(tbSysLog);
            return new Dto(true, "保存成功", integer);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                TbSysLog tbSysLog = new TbSysLog();
                tbSysLog.setOperatorId(userId);
                tbSysLog.setOperateTime(DateUtils.dateFormatYYMMDDHHmmss());
                tbSysLog.setOperateIp(InetAddress.getLocalHost().getHostAddress());
                tbSysLog.setOperateContent("保存系统提示");
                tbSysLog.setOperateResult("success");
                tbSysLog.setUrl("/districtCommittee/insertSysPoints");
                tbSysLog.setMethod("post");
                tbSysLog.setMeunId("1011");
                tbSysLog.setFuncId("1011");
                tbSysLog.setMemo("");
                insertTbSysLog(tbSysLog);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return new Dto(false, "保存失败", e);
        }
    }

    @Override
    public Dto getSysLog(Map<String, Object> map) {
        Integer page = Integer.parseInt(map.get("page").toString());
        Integer limit = Integer.parseInt(map.get("limit").toString());
        Integer page1 = (page - 1) * limit;
        map.put("page", page1);
        map.put("limit", limit);
        List<TbSysLog> sysLog = districtCommitteeMapper.getSysLog(map);
        for (TbSysLog t : sysLog) {
            String payTime = DateUtils.getPayTime(t.getOperateTime());
            t.setOperateTime(payTime);
        }
        Integer sumSysLog = districtCommitteeMapper.getSumSysLog(map);
        return new Dto(true, "查询成功", sysLog, sumSysLog);
    }

    public static JSONArray joinJSONArray(JSONArray... arrays) throws Exception {
        JSONArray ret = new JSONArray();
        Set<String> ids = new HashSet<String>();
        for (JSONArray array : arrays) {
            if (array == null) continue;
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (ids.contains(obj.get("id"))) continue;
                ret.add(obj);
                ids.add(obj.getString("id"));
            }
        }
        return ret;
    }
}
