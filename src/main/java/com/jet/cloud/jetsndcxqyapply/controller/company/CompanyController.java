package com.jet.cloud.jetsndcxqyapply.controller.company;

import com.alibaba.fastjson.JSONObject;
import com.jet.cloud.jetsndcxqyapply.common.Dto;
import com.jet.cloud.jetsndcxqyapply.entity.CompanySite;
import com.jet.cloud.jetsndcxqyapply.service.user.CompanySiteService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


// 企业Controller
@Controller
public class CompanyController {
    @Autowired
    private CompanySiteService companySiteService;

    /**
     * 如果当前用户的角色属于注册账号，则在进入申报自评页面时需要检查当前时间是否在允许使用的时间范围内（tb_sys_role表中的start_date和end_date），
     * 如果不在，则提示用户当前不允许自评申报，允许自评申报的时间范围是xxxx-xx-xx至xxxx-xx-xx。
     */
    @GetMapping("/company/checkIsRegisterNo")
    @ResponseBody
    public Dto checkIsRegisterNo(@RequestParam(value = "userId") String id) {
        Dto dto = companySiteService.checkIsRegisterNo(id);
        return dto;
    }

    /**
     * 如果当前用户（企业）没有提交过自评，展示自评申报页面；
     * 如果已经提交过，则提示用户“您已提交自评申报，请到申报进展页面查询进展”。
     * success=true msg=""  data="" (已经提交过)
     * msg=fail (没有提交过)
     */
    @GetMapping("/company/checkIsCommitSelfEva")
    @ResponseBody
    public Dto checkIsCommitSelfEva() {
        Dto dto = companySiteService.checkIsCommit();
        return dto;
    }

    //查询所有指标和标准值
    @GetMapping("/company/getm")
    @ResponseBody
    public Dto getm(@RequestParam(value = "flag") String flag) {
        Dto dto = companySiteService.getm(flag);
        return dto;
    }

    /**
     * 企业申报自评的保存方法
     * siteId--企业Id
     * indexId--指标id
     * selfValue--企业自评值
     * selfUserId--自评人id
     * selfSaveTime--自评保存时间
     * appendixType--附件类型
     * path--文件路径
     */
    @PostMapping("/company/insertTbSiteApplicationIndexValue")
    @ResponseBody
    @RequiresPermissions("/company/insertTbSiteApplicationIndexValue")
    public Dto insertTbSiteApplicationIndexValue(@RequestBody JSONObject jsonObject) {
        Dto dto = companySiteService.insertTbSiteApplicationIndexValue(jsonObject);
        return dto;
    }

    /**
     * 企业自评的提交
     */
    @PostMapping("/company/updateTbSiteApplicationIndexValue")
    @ResponseBody
    @RequiresPermissions("/company/commitTbSiteApplicationIndexValue")
    public Dto updateTbSiteApplicationIndexValue(@RequestBody JSONObject jsonObject) {
        Dto dto = companySiteService.updateTbSiteApplicationIndexValue(jsonObject);
        return dto;
    }

    //申报进展
    /**
     * success=true msg=""  data="" (已经提交过)
     * msg=fail (没有提交过)
     */
    @GetMapping("/company/getDeclarationProgress")
    @ResponseBody
    public Dto getDeclarationProgress(@RequestParam(value = "flag") String flag) {
        Dto getm = companySiteService.getm(flag);
        return getm;
    }

    // 注册信息点保存
    @PostMapping("/company/updateSite")
    @ResponseBody
    @RequiresPermissions("/company/commitTbSiteApplicationIndexValue")
    public Dto updateSite(@RequestBody CompanySite companySite) {
        int i = companySiteService.updateSite(companySite);
        if (i == 0) {
            return new Dto(false, "没有权限", null);
        } else {
            return new Dto(true, "更新成功", i);
        }
    }

    // 查询所有的企业附件
    @GetMapping("/company/getTbSiteApplicationAppendix")
    @ResponseBody
    public Dto getTbSiteApplicationAppendix() {
        Dto dto = companySiteService.getTbSiteApplicationAppendix1();
        return dto;
    }

    // 校验企业是否有复审权限
    @GetMapping("/company/checkCompanyReview")
    @ResponseBody
    public Dto checkCompanyReview(@RequestParam(value = "userId") String userId) {
        Dto dto = companySiteService.checkCompanyReview(userId);
        return dto;
    }

    // 企业复审上传文件后保存
    @PostMapping("/company/insertTbSiteApplicationAppendix")
    @ResponseBody
    @RequiresPermissions("/company/insertTbSiteApplicationAppendix")
    public Dto insertTbSiteApplicationAppendix(@RequestBody JSONObject jsonObject) {
        Dto dto = companySiteService.insertTbSiteApplicationAppendix(jsonObject);
        return dto;
    }

    // 删除附件
    @PostMapping("/company/deleteTbSiteApplicationAppendix")
    @ResponseBody
    public Dto deleteTbSiteApplicationAppendix(@RequestBody JSONObject jsonObject) {
        String seqId = jsonObject.getString("seqId");
        Dto dto = companySiteService.deleteTbSiteApplicationAppendix(seqId);
        return dto;
    }

    // 复审材料页面展示
    @GetMapping("/company/getAllAppendix")
    @ResponseBody
    public Dto getAllAppendix() {
        Dto dto = companySiteService.getAllAppendix();
        return dto;
    }
}
