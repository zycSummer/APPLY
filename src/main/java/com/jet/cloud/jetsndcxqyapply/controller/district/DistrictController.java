package com.jet.cloud.jetsndcxqyapply.controller.district;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import com.jet.cloud.jetsndcxqyapply.service.user.DistrictService;
import com.jet.cloud.jetsndcxqyapply.service.user.StreetService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 区三方Controller
@Controller
public class DistrictController {
    @Autowired
    private CompanySiteService companySiteService;

    @Autowired
    private StreetService streetService;

    @Autowired
    private DistrictService districtService;

    /**
     * 如果当前用户的角色属于评定账号，则在进入评定页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户“当前不允许评定申报，允许评定申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。”
     */
    @GetMapping("/district/checkIsRegisterNo")
    @ResponseBody
    public Dto checkIsRegisterNo(@RequestParam(value = "userId") String userId) {
        Dto dto = companySiteService.checkIsRegisterNo(userId);
        return dto;
    }

    /**
     * 查询所有指标和标准值
     * 汇总(查看详情)
     */
    @GetMapping("/district/getm")
    @ResponseBody
    public Dto getm(@RequestParam(value = "siteId") String siteId, @RequestParam(value = "year") String year) {
        Dto dto = null;
        if (year != "") {
            int i = Integer.parseInt(year);
            dto = streetService.getm(siteId, i);
        } else {
            dto = streetService.getm(siteId, null);
        }
        return dto;
    }

    /**
     * 评定页面
     * status 上报状态  0是未上报 1是已上报
     */
    @GetMapping("/district/getAllDistrict")
    @ResponseBody
    public Dto getAllDistrict(@RequestParam Map<String, Object> jsonObject) {
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
        Dto allStreet = districtService.getAllDistrict(map);
        return allStreet;
    }

    /**
     * 评定保存
     * <p>
     * 需加上企业的siteId(集合)
     */
    @PostMapping("/district/insertDistrict")
    @ResponseBody
    @RequiresPermissions("/district/insertDistrict")
    public Dto insertDistrict(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.insertTbSiteApplicationIndexValue(jsonObject);
        return dto;
    }

    /**
     * 评定上报
     * <p>
     * 需加上企业的siteId(集合)
     */
    @PostMapping("/district/updateDistrict")
    @ResponseBody
    @RequiresPermissions("/district/updateDistrict")
    public Dto updateDistrict(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.updateTbSiteApplicationIndexValue(jsonObject);
        return dto;
    }

    // 复审评定页面
    @PostMapping("/district/districtReview")
    @ResponseBody
    @RequiresPermissions("/district/streetReview")
    public Dto districtReview(@RequestBody JSONObject jsonObject) {
        Dto dto = districtService.districtReview(jsonObject);
        return dto;
    }

    /**
     * 复审评定保存
     */
    @PostMapping("/district/insertDistrictReview")
    @ResponseBody
    @RequiresPermissions("/district/insertDistrictReview")
    public Dto insertDistrictReview(@RequestBody JSONObject jsonObject) {
        Dto dto = districtService.insertDistrictReview(jsonObject);
        return dto;
    }

    /**
     * 复审评定上报
     */
    @PostMapping("/district/updateDistrictReview")
    @ResponseBody
    @RequiresPermissions("/district/updateDistrictReview")
    public Dto updateDistrictReview(@RequestBody JSONObject jsonObject) {
        Dto dto = districtService.updateDistrictReview(jsonObject);
        return dto;
    }
}
