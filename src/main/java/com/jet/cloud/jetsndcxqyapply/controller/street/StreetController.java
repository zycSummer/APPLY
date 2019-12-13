package com.jet.cloud.jetsndcxqyapply.controller.street;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.common.StringHelper;
import com.jet.cloud.jetsndcxqyapply.entity.SystemUser;
import com.jet.cloud.jetsndcxqyapply.mapper.SystemUserMapper;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import com.jet.cloud.jetsndcxqyapply.service.user.StreetService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// 镇(街道)Controller
@Controller
public class StreetController {
    @Autowired
    private CompanySiteService companySiteService;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private StreetService streetService;

    /**
     * 如果当前用户的角色属于测评账号，则在进入测评页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户“当前不允许测评申报，允许测评申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。”
     */
    @GetMapping("/street/checkIsRegisterNo")
    @ResponseBody
    public Dto checkIsRegisterNo(@RequestParam(value = "userId") String userId) {
        Dto dto = companySiteService.checkIsRegisterNo(userId);
        return dto;
    }

    // 所属工业园
    @GetMapping("/street/getFactoryPark")
    @ResponseBody
    public Dto getFactoryPark(HttpSession session) {
        String user = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(user);
        String streetId = systemUser.getStreetId();
        Dto park = streetService.getSysIndustrialPark(streetId);
        return park;
    }

    /**
     * 测评页面
     * status 上报状态  0是未上报 1是已上报
     */
    @GetMapping("/street/getTownStreet")
    @ResponseBody
    public Dto getTownStreet(@RequestParam Map<String, Object> jsonObject, HttpSession session) {
        Integer page = Integer.parseInt(jsonObject.get("page").toString());
        Integer limit = Integer.parseInt(jsonObject.get("limit").toString());
        String parkId = jsonObject.get("parkId").toString();
        String siteName = jsonObject.get("siteName").toString();

        String user = StringHelper.nvl(session.getAttribute("loginUser"), "");
        SystemUser systemUser = systemUserMapper.getUserByUserId(user);
        String streetId = systemUser.getStreetId();
        Map<String, Object> map = new HashMap<>();
        map.put("streetId", streetId);
        Integer page1 = (page - 1) * limit;
        map.put("page", page1);
        map.put("limit", limit);
        map.put("parkId", parkId);
        map.put("siteName", siteName);
        Dto allStreet = streetService.getTownStreet(map);
        return allStreet;
    }

    /**
     * 查询所有指标和标准值
     * 点击测评(查看详情)
     */
    @GetMapping("/street/getm")
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
     * 测评保存
     * <p>
     * 需加上企业的siteId(集合)
     */
    @PostMapping("/Street/insertStreet")
    @ResponseBody
    @RequiresPermissions("/Street/insertStreet")
    public Dto insertStreet(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.insertTbSiteApplicationIndexValue(jsonObject);
        return dto;
    }

    /**
     * 测评上报
     * <p>
     * 需加上企业的siteId(集合)
     */
    @PostMapping("/Street/updateStreet")
    @ResponseBody
    @RequiresPermissions("/Street/updateStreet")
    public Dto updateStreet(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.updateTbSiteApplicationIndexValue(jsonObject);
        return dto;
    }

    // 复审测评页面
    @PostMapping("/Street/streetReview")
    @ResponseBody
    @RequiresPermissions("/Street/streetReview")
    public Dto streetReview(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.streetReview(jsonObject);
        return dto;
    }

    /**
     * 复审测评保存
     */
    @PostMapping("/Street/insertStreetReview")
    @ResponseBody
    @RequiresPermissions("/Street/insertStreetReview")
    public Dto insertStreetReview(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.insertStreetReview(jsonObject);
        return dto;
    }

    /**
     * 复审测评上报
     */
    @PostMapping("/Street/updateStreetReview")
    @ResponseBody
    @RequiresPermissions("/Street/updateStreetReview")
    public Dto updateStreetReview(@RequestBody JSONObject jsonObject) {
        Dto dto = streetService.updateStreetReview(jsonObject);
        return dto;
    }
}
