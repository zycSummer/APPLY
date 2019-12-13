package com.jet.cloud.jetsndcxqyapply.controller.district;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.entity.Company.TbSiteApplication;
import com.jet.cloud.jetsndcxqyapply.entity.UserRole;
import com.jet.cloud.jetsndcxqyapply.entity.district.TbSySPoints;
import com.jet.cloud.jetsndcxqyapply.service.user.DistrictCommitteeService;
import com.jet.cloud.jetsndcxqyapply.service.user.StreetService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 区委员会Controller
@Controller
public class DistrictCommitteeController {
    @Autowired
    private StreetService streetService;

    @Autowired
    private DistrictCommitteeService districtCommitteeService;

    /**
     * 查询所有指标和标准值
     * 点击测评(查看详情)
     */
    @GetMapping("/districtCommittee/getm")
    @ResponseBody
    public Dto getm(@RequestParam(value = "siteId") String siteId, @RequestParam(value = "year") String year) {
        Dto dto = null;
        if (year != "") {
            int i = Integer.parseInt(year);
            dto = streetService.getm(siteId, i - 1);
        } else {
            dto = streetService.getm(siteId, null);
        }
        return dto;
    }

    /**
     * 查询所有指标和标准值
     * 复审(查看详情)
     */
    @GetMapping("/districtCommittee/getm1")
    @ResponseBody
    public Dto getm1(@RequestParam(value = "siteId") String siteId, @RequestParam(value = "year") String year) {
        Dto dto = districtCommitteeService.getm(siteId, year);
        return dto;
    }

    /**
     * 汇总页面
     */
    @GetMapping("/districtCommittee/getAllDistrictCommittee")
    @ResponseBody
    public Dto getAllDistrictCommittee(@RequestParam Map<String, Object> jsonObject) {
        Integer page = Integer.parseInt(jsonObject.get("page").toString());
        Integer limit = Integer.parseInt(jsonObject.get("limit").toString());
        String parkId = jsonObject.get("parkId").toString();
        String siteName = jsonObject.get("siteName").toString();
        String streetId = jsonObject.get("streetId").toString();

        Map<String, Object> map = new HashMap<>();
        map.put("streetId", streetId);
        Integer page1 = (page - 1) * limit;
        map.put("page", page1);
        map.put("limit", limit);
        map.put("parkId", parkId);
        map.put("siteName", siteName);
        Dto allStreet = districtCommitteeService.getAllDistrictCommittee(map);
        return allStreet;
    }

    // 授予称号的请选择
    @GetMapping("/districtCommittee/getTbSysTitle")
    @ResponseBody
    public Dto getTbSysTitle() {
        Dto allStreet = districtCommitteeService.getTbSysTitle();
        return allStreet;
    }

    // 授予称号点击保存
    @PostMapping("/districtCommittee/updateDistrictCommitteeTitle")
    @ResponseBody
    @RequiresPermissions("/districtCommittee/updateDistrictCommitteeTitle")
    public Dto updateDistrictCommitteeTitle(@RequestBody JSONObject jsonObject) {
        List<Map<String, Object>> tbSiteApplications = jsonObject.getObject("tableData", List.class);
        String titleId = jsonObject.getObject("titleId", String.class);
        Dto dto = districtCommitteeService.updateDistrictCommitteeTitle(tbSiteApplications, titleId);
        return dto;
    }

    // 复审页面展示(复审结论reviewResult 0-通过 1-撤销)
    @GetMapping("/districtCommittee/getAllReviewDistrictCommittee")
    @ResponseBody
    public Dto getAllReviewDistrictCommittee(@RequestParam Map<String, Object> jsonObject) throws Exception {
        Integer page = Integer.parseInt(jsonObject.get("page").toString());
        Integer limit = Integer.parseInt(jsonObject.get("limit").toString());
        String parkId = jsonObject.get("parkId").toString();
        String siteName = jsonObject.get("siteName").toString();
        String streetId = jsonObject.get("streetId").toString();
        String year = jsonObject.get("year").toString();
        Integer year1 = 0;
        if (year != null && year != "") {
            year1 = Integer.parseInt(year);
        } else {
            year1 = Calendar.getInstance().get(Calendar.YEAR);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("streetId", streetId);
        Integer page1 = (page - 1) * limit;
        map.put("page", page1);
        map.put("limit", limit);
        map.put("parkId", parkId);
        map.put("siteName", siteName);
        map.put("year", year1 - 1);
        Dto allStreet = districtCommitteeService.getAllReviewDistrictCommittee(map);
        return allStreet;
    }

    /**
     * 复审页面(保存)
     * <p>
     * 数组 tbSiteReviewIndexValue:[{year,siteId,indexId,reviewValue}]
     * tbSiteReview:{year,siteId,reviewResult,reviewUserId}
     * <p>
     * 用户输入或者选择的指标 的区三方委员会年度复审值 满足 “指标.sheet”中一票否决条件列设置的条件，则年度复审结论的值固定为撤销。(即reviewResult=1)
     */
    @PostMapping("/districtCommittee/replaceReviewDistrictCommittee")
    @ResponseBody
    @RequiresPermissions("/districtCommittee/replaceReviewDistrictCommittee")
    public Dto replaceReviewDistrictCommittee(@RequestBody JSONObject jsonObject) {
        return districtCommitteeService.replaceTbSiteReviewIndexValueAndTbSiteReview(jsonObject);
    }


    /**
     * 账号管理页面
     * userId
     * userName
     * page
     * limit
     */
    @GetMapping("/districtCommittee/getSystemUser")
    @ResponseBody
    public Dto getSystemUser(@RequestParam Map<String, Object> jsonObject) {
        return districtCommitteeService.getSystemUser(jsonObject);
    }

    // 账户时限管理查询
    @GetMapping("/districtCommittee/getUserRole")
    @ResponseBody
    public Dto getUserRole(@RequestParam Map<String, Object> jsonObject) {
        return districtCommitteeService.getUserRole(jsonObject);
    }

    /**
     * 账户时限管理修改
     * startDate="" xxxx-xx-xx
     * endDate=""
     * memo=""
     * updateUserId(当期登录人id)=""
     */
    @GetMapping("/districtCommittee/updateUserRole")
    @ResponseBody
    @RequiresPermissions("/districtCommittee/updateUserRole")
    public Dto updateUserRole(UserRole userRole) {
        return districtCommitteeService.updateUserRole(userRole);
    }

    /**
     * 查看系统提示
     */
    @GetMapping("/districtCommittee/getSysPoints")
    @ResponseBody
    public Dto getSysPoints() {
        return districtCommitteeService.getSysPoints();
    }

    /**
     * 保存系统提示
     * points
     */
    @PostMapping("/districtCommittee/insertSysPoints")
    @ResponseBody
    @RequiresPermissions("/districtCommittee/insertSysPoints")
    public Dto insertSysPoints(@RequestBody TbSySPoints tbSySPoints) {
        return districtCommitteeService.insertSysPoints(tbSySPoints);
    }

    /**
     * 系统日志查询
     * operatorId
     * beginDate
     * endDate
     */
    @GetMapping("/districtCommittee/getSysLog")
    @ResponseBody
    public Dto getSysLog(@RequestParam Map<String, Object> jsonObject) {
        return districtCommitteeService.getSysLog(jsonObject);
    }
}
